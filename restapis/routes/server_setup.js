var mongo = require('mongodb');

global.Server = mongo.Server,
    Db = mongo.Db,
    BSON = mongo.BSONPure;

var server = new Server('localhost', 27017, {auto_reconnect: true});
db = new Db('instadb', server);

db.open(function(err, db) {
    if(!err) {
        //console.log("Star Controller up.");
        db.collection('stars', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The 'stars' collection doesn't exist. Creating it with sample data...");
                populateDB();
            }
        });
    }
});
global.uuidV1 = require('uuid/v1');
require('./firebase_admin_setup.js');