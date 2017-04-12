require('./server_setup.js');

var admin = require("firebase-admin");

var serviceAccount = require("./firetest-1f735-firebase-adminsdk-q3y0a-44bf3ab4c2.json");
console.log('fetching users');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://firetest-1f735.firebaseio.com"
});

var defaultAuth = admin.auth();
exports.login = function(req, res) {
   
    var idToken = req.headers['authorization'];
   
    admin.auth().verifyIdToken(idToken)
  .then(function(decodedToken) {
    var uid = decodedToken.uid;
    console.log(uid);
    // ...
  }).catch(function(error) {
      console.log("incorrect token");
    // Handle error
  });

        db.collection('stars', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};

