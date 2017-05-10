var mongo = require('mongodb');

var Server = mongo.Server,
    Db = mongo.Db,
    BSON = mongo.BSONPure;

var server = new Server('localhost', 27017, {auto_reconnect: true});
db = new Db('instadb', server);

db.open(function(err, db) {
    if(!err) {
        console.log("Posts Controller up.");
        db.collection('posts', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The 'posts' collection doesn't exist. Creating it with sample data...");
                populateDB();
            }
        });
    }
});

exports.findById = function(req, res) {
    var id = req.params.id;
    console.log('Retrieving post: ' + id);
    db.collection('posts', function(err, collection) {
        collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
            res.send(item);
        });
    });
};

exports.listComments = function(req, res) {
    var id = req.params.id;
            console.log('Retrieving post: ' + id);
            db.collection('posts', function(err, collection) {
                collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
                console.log("sending-");
                console.log(item.reactions);
                     //db.collection('profiles', function(err, collection) {
                      //     collection.find({'userId': { "$in": item.likes}},{ name: 1, image: 1,_id: 0,userId:1 }).
                      //     toArray(function(err, items) {
                      //          res.send(items);
                      //     });
                      //  });
                    res.send(item.reactions);

                });
            });
};
exports.listLikes = function(req, res) {
    var id = req.params.id;
        console.log('Retrieving post: ' + id);
        db.collection('posts', function(err, collection) {
            collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
            console.log(item.likes);
                 db.collection('profiles', function(err, collection) {
                       //collection.findOne({'userId': { "$in": item.likes}},{ name: 1, image: 1 }, function(err, users) {
                       collection.find({'userId': { "$in": item.likes}},{ name: 1, image: 1,_id: 0,userId:1 }).
                       toArray(function(err, items) {
                            res.send(items);
                       });
                    });
               // res.send(item.likes);

            });
        });
};
exports.findAll = function(req, res) {
    var rQuery = req.query || {};
    var limit = rQuery.limit || 10;
    var page = rQuery.page || 1;
    var tstamp = rQuery.timestamp || Date.now();
    var userid = rQuery.userid;
    var recordorder = rQuery.order || 1;
    console.log(tstamp);
    console.log(userid);

    db.collection('profiles', function(err, collection) {
            collection.findOne({'userId': (userid)}, function(err, item) {
                following = item.following;
                console.log(following);
    db.collection('posts', function(err, collection) {
        //collection.find().sort().toArray(function(err, items) {

        //collection.find({ created_at: { $gt: new Date(tstamp) } }).sort({timestamp: -1}).limit(limit).skip(limit*(page-1)).toArray(function(err, items) {
        //collection.find().sort({created_at: -1}).limit(limit).skip(limit*(page-1)).toArray(function(err, items) {
        //collection.find().sort({created_at: -1}).limit(limit).skip(limit*(page-1)).toArray(function(err, items) {
        collection.find({ uid: { "$in":following,"$ne": userid }}).
                    sort({created_at: -1}).limit(limit).
                    skip(limit*(page-1)).toArray(function(err, items) {
            console.log(items);
            res.send(items);

        });
    });

            });
        });


///console.log('Adding post: ' + JSON.stringify(wine));
/*
    db.collection('users', function(err, collection) {
                    collection.findOne({"userId":"qmQPCUeRoUPsgs6x4hReHqw4ReF3"}, function(err, item) {
                        db.collection('posts', function(err, collection) {
                        console.log(item);
                               // wine.publisher_name = item.Name;
                               // wine.publisher_image = item.image;
res.send(item);

                            });
                        //res.send(item);
                    });
                });
*/

};

exports.reactions = function(req, res) {
   var id = req.params.id;
           console.log('Retrieving post: ' + id);
           db.collection('posts', function(err, collection) {
               collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
               console.log(item.likes);

                   res.send(item.reactions);

               });
           });
};

exports.addPost = function(req, res) {
    var wine = req.body;
    wine.created_at =new Date();
    wine.timestamp = Date.now();
    wine.likesCount = 0 ;
    wine.commentCount = 0;
    delete wine.likes;
    wine.likes = [] ;
    userid = wine.uid;
    console.log('userid-'+userid);


    console.log('Adding post: ' + JSON.stringify(wine));
    db.collection('profiles', function(err, collection) {
                    collection.findOne({'userId':(userid)}, function(err, item) {
                        console.log("Query result-"+JSON.stringify(item));

                        db.collection('posts', function(err, collection) {
                                                console.log(item);
                                                        wine.publisher_name = item.name;
                                                        wine.publisher_image = item.image;

                                                        collection.insert(wine, {safe:true}, function(err, result) {
                                                            if (err) {
                                                                res.send({'error':'An error has occurred'});
                                                            } else {
                                                                console.log('Success: ' + JSON.stringify(result[0]));
                                                                res.send(result[0]);
                                                            }
                                                        });
                                                    });
                        //res.send(item);
                    });
                });
//

}

exports.updatePost = function(req, res) {
    var id = req.params.id;
    var wine = req.body;
    console.log('Updating post: ' + id);
    console.log(JSON.stringify(wine));
    db.collection('posts', function(err, collection) {
        collection.update({'_id':new BSON.ObjectID(id)}, wine, {safe:true}, function(err, result) {
            if (err) {
                console.log('Error updating wine: ' + err);
                res.send({'error':'An error has occurred'});
            } else {
                console.log('' + result + ' document(s) updated');
                res.send(wine);
            }
        });
    });
}

exports.like = function(req, res) {
    var id = req.params.id;
    var op = req.query.op;
    var userid = req.query.userid ||"navraj";
    var likeobj =new Object();


    var wine = req.body;
    if(op==0)
        increment = -1;
    else
        increment = 1 ;

        db.collection('profiles', function(err, collection) {
                collection.findOne({'userId':userid}, function(err, item) {
                console.log(item._id);
                likeobj.id = (item._id);
                likeobj.name = item.Name;
                if(op==0)
                    oper = "$push";
                else
                    oper = "$pull";
                if(op ==1) {
                db.collection('posts', function(err, collection) {
                                           collection.update({'_id':new BSON.ObjectID(id)},
                                           { $inc: { "likesCount": increment },
                                           "$push": { "likes": userid } },
                                           {upsert: true, safe:true}, function(err, result) {
                                               if (err) {
                                                   console.log('Error updating wine: ' + err);
                                                   res.send({'error':'An error has occurred'});
                                               } else {
                                                   console.log('' + result + ' document(s) updated');
                                                   console.log(result);
                                                   res.send(wine);
                                               }
                                           });
                                   });
                }
                else {
                db.collection('posts', function(err, collection) {
                                           collection.update({'_id':new BSON.ObjectID(id)}, { $inc: { "likesCount": increment },
                                           "$pull": { "likes": userid } },
                                           {upsert: true, safe:true}, function(err, result) {
                                               if (err) {
                                                   console.log('Error updating wine: ' + err);
                                                   res.send({'error':'An error has occurred'});
                                               } else {
                                                   console.log('' + result + ' document(s) updated');
                                                   console.log(result);
                                                   res.send(wine);
                                               }
                                           });
                                   });
                }

                });
            });
    console.log('Updating post: ' + id);
    console.log(JSON.stringify(wine));

}

exports.deleteComment = function(req, res) {
       var id = req.params.id;
       var body = req.body;
       body = req.headers;
       console.log(body);
       var uid = body.uid;
       var tstamp = body.tstamp;

       console.log('Updating post: ' + id+'.Comment id-'+ uid+'.tstamp-'+tstamp);
       console.log(JSON.stringify(body));
           var userid = body.userid;

        db.collection('posts', function(err, collection) {
               collection.update({'_id':new BSON.ObjectID(id)}, { $inc: { "commentCount": -1 },
                                                  "$pull": { "reactions" : { "id": uid,"posted": new Date(tstamp) }} },
                                                  //"$pull": { "reactions" : { "id": comment }} },
                                                  {upsert: true, safe:true}, function(err, result) {
                                                      if (err) {
                                                          console.log('Error updating wine: ' + err);
                                                          res.send({'error':'An error has occurred'});
                                                      } else {
                                                          console.log('' + result + ' document(s) updated');
                                                          console.log(result);
                                                          res.send(body);
                                                      }
                                                  });
                                          });

}

exports.deleteCommentGet = function(req, res) {
    var id = req.params.id;
    var body = req.body;
    var uid = req.query.uid;
    var tstamp = req.query.tstamp;

    console.log('Updating post: ' + id+'.Comment id-'+ uid);
    console.log(JSON.stringify(body));
        var userid = body.userid;

     db.collection('posts', function(err, collection) {
            collection.update({'_id':new BSON.ObjectID(id)}, { $inc: { "commentCount": -1 },
                                               "$pull": { "reactions" : { "id": uid,"posted": new Date(tstamp) }} },
                                               //"$pull": { "reactions" : { "id": comment }} },
                                               {upsert: true, safe:true}, function(err, result) {
                                                   if (err) {
                                                       console.log('Error updating wine: ' + err);
                                                       res.send({'error':'An error has occurred'});
                                                   } else {
                                                       console.log('' + result + ' document(s) updated');
                                                       console.log(result);
                                                       res.send(body);
                                                   }
                                               });
                                       });
}

exports.comment = function(req, res) {
    var id = req.params.id;
        var op = req.query.op;
        var comment = req.query.comment;
        var userid = req.query.userid ||"navraj";
        var likeobj =new Object();


        var wine = req.body;


        console.log('Updating post: ' + id);
        console.log(JSON.stringify(wine));



}


exports.postcomment = function(req, res) {
    var id = req.params.id;
    var resp = req.body;
        var op = "";
        var comment = resp.comment;
        var userid = resp.userid ||"navraj";
        var image = resp.image ||"";
        var likeobj =new Object();
        var name = resp.name || "navraj";
        console.log(userid);
        var wine = req.body;
        if(op==0)
            increment = 1;
        else
            increment = 1 ;

            db.collection('profiles', function(err, collection) {
                    collection.findOne({'userId':resp.userid}, function(err, item) {
                    console.log(item._id);
                    likeobj.id = (item._id);
                    likeobj.name = item.Name;
                    oper = "$push";

                    db.collection('posts', function(err, collection) {
                                               collection.update({'_id':new BSON.ObjectID(id)},
                                               { $inc: { "commentCount": increment },
                                               "$push": { "reactions": {
                                                                             'posted': new Date(),
                                                                             'id': userid,
                                                                             'name': name,
                                                                             'url': image,
                                                                             'text': comment } } },
                                               {upsert: true, safe:true}, function(err, result) {
                                                   if (err) {
                                                       console.log('Error updating wine: ' + err);
                                                       res.send({'error':'An error has occurred'});
                                                   } else {
                                                       console.log('' + result + ' document(s) updated');
                                                       console.log(result);
                                                       console.log(collection.reactions);
                                                       res.send("200");

                                                   }
                                               });
                                       });
                    });
                });
        console.log('Updating post: ' + id);
        console.log(JSON.stringify(wine));
}

exports.deletePost = function(req, res) {
    var id = req.params.id;
    console.log('Deleting post: ' + id);
    db.collection('posts', function(err, collection) {
        collection.remove({'_id':new BSON.ObjectID(id)}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                res.send(req.body);
            }
        });
    });
}

