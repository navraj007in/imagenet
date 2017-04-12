var mongo = require('mongodb');

var Server = mongo.Server,
    Db = mongo.Db,
    BSON = mongo.BSONPure;

var server = new Server('localhost', 27017, {auto_reconnect: true});
db = new Db('winedb', server);

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
    console.log('Retrieving User: ' + id);
    db.collection('profiles', function(err, collection) {
        collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
            res.send(item);
        });
    });
};
exports.listLikes = function(req, res) {
    var id = req.params.id;
    console.log('Retrieving User: ' + id);
    db.collection('profiles', function(err, collection) {
        collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
            res.send(item);
        });
    });
};
exports.findAll = function(req, res) {
    var rQuery = req.query || {};
    var limit = rQuery.limit || 5;
    var page = rQuery.page || 1;
    var tstamp = rQuery.timestamp || Date.now();
    var recordorder = rQuery.order || 1;
    console.log(tstamp);
    db.collection('posts', function(err, collection) {
        //collection.find().sort().toArray(function(err, items) {

        //collection.find({ created_at: { $gt: new Date(tstamp) } }).sort({timestamp: -1}).limit(limit).skip(limit*(page-1)).toArray(function(err, items) {
        collection.find().sort({created_at: -1}).limit(limit).skip(limit*(page-1)).toArray(function(err, items) {
            res.send(items);
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

exports.addPost = function(req, res) {
    var wine = req.body;
    wine.created_at =new Date();
    wine.timestamp = Date.now();
    wine.likesCount = 0 ;
    wine.commentCount = 0;
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
    var wine = req.body;
    if(op==0)
        increment = -1;
    else
        increment = 1 ;
    console.log('Updating post: ' + id);
    console.log(JSON.stringify(wine));
 db.collection('posts', function(err, collection) {
        collection.update({'_id':new BSON.ObjectID(id)}, { $inc: { "likesCount": increment } }, {upsert: true, safe:true}, function(err, result) {
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

exports.deleteComment = function(req, res) {
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

exports.comment = function(req, res) {
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

