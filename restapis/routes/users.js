require('./server_setup.js');
console.log("User Controller up.");

exports.findByIdd = function(req, res) {
    var id = req.params.id;
    console.log('Retrieving User: ' + id);
    db.collection('posts', function(err, collection) {
        collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
            res.send(item);
        });
    });
};

exports.findByIdprofile = function(req, res) {
    var id = req.params.id;
    console.log('Retrieving User: ' + id);
    db.collection('profiles', function(err, collection) {
        collection.findOne({'userId':id}, function(err, item) {
        console.log(item);
            res.send(item);
        });
    });
};
exports.findById = function(req, res) {
    var id = req.params.id;
    console.log('Retrieving User: ' + id);
    db.collection('profiles', function(err, collection) {
        collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
            res.send(item);
        });
    });
};
exports.findAll = function(req, res) {
    db.collection('profiles', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};

exports.addUser = function(req, res) {
    var user = req.body;

    var email = req.body.email;
    console.log(req.body);
    var password = req.body.password;
    var name = req.body.name;

    console.log("Creating user for -"+email+"-"+password);

    var defaultAuth = admin.auth();


    admin.auth().createUser({
      email: email,
      emailVerified: false,
      password: password,
      displayName: name,
      disabled: false
    })
      .then(function(userRecord) {
        // A UserRecord representation of the newly created user is returned
        console.log("Created Firebase User successfully with id :", userRecord.uid);
            var user = req.body;
        user.userId = userRecord.uid;
        user.timestamp = Date.now();
        user.description = "";
        user.image ="";
        user.followers = "";
        user.followers_count = "";
        user.following = "" ;
        user.following_count = "" ;

        user.guid = uuidV1();
        delete user.password;
        status = "201";
        var reply = JSON.stringify(user);

        db.collection('profiles', function(err, collection) {
            collection.insert(user, {safe:true}, function(err, result) {
                if (err) {
                    user.status = "201";
                    user.message = "An error occured";
                    reply.set('status',"201");
                    res.status(201).send(user);

                } else {
                    console.log('Success: ' + JSON.stringify(result[0]));
                    status= "200";
                    user.status = "200";
                    user.message = "Account created Successfully";
                    res.status(200).send(user);
                }
            });
        });
      })
      .catch(function(error) {
                          user.message = "An error occured---";
                    user.status = "201";

        console.log("User Creation onf Firebase failed:", error);
                    res.status(201).send(user);
      });

}

exports.updateUser = function(req, res) {
    var id = req.params.id;
    var user = req.body;
    console.log('Updating post: ' + id);
    console.log(JSON.stringify(user));
    db.collection('profiles', function(err, collection) {
        collection.update({'_id':new BSON.ObjectID(id)}, user, {safe:true}, function(err, result) {
            if (err) {
                console.log('Error updating wine: ' + err);
                res.send({'error':'An error has occurred'});
            } else {
                console.log('' + result + ' document(s) updated');
                res.send(user);
            }
        });
    });
}

exports.deleteUser = function(req, res) {
    var id = req.params.id;
    console.log('Deleting post: ' + id);
    db.collection('profiles', function(err, collection) {
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

