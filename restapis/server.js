var express = require('express'),
    path = require('path'),
    http = require('http'),
    posts = require('./routes/posts');
    users = require('./routes/users');
    authenticate = require('./routes/authenticate');

var app = express();

app.configure(function () {
    app.set('port', process.env.PORT || 8000);
    app.use(express.logger('dev'));  /* 'default', 'short', 'tiny', 'dev' */
    app.use(express.bodyParser()),
    app.use(express.static(path.join(__dirname, 'public')));
});

app.post('/authenticate', authenticate.login);

app.get('/users', users.findAll);
app.get('/users/:id', users.findById);
app.post('/users', users.addUser);
app.put('/users/:id', users.updateUser);
app.delete('/users/:id', users.deleteUser);
app.get('/users/:id/profile', users.findByIdprofile);

app.get('/posts', posts.findAll);
app.get('/posts/:id', posts.findById);
app.post('/posts', posts.addPost);
app.put('/posts/:id', posts.updatePost);
app.delete('/posts/:id', posts.deletePost);

app.get('/posts/:id/likes', posts.listLikes);
app.get('/posts/:id/like', posts.like);
app.post('/posts/:id/comment', posts.comment);
app.delete('/posts/:id/comment', posts.deleteComment);
app.get('/posts/:id/comments', posts.listComments);



http.createServer(app).listen(app.get('port'), function () {
    console.log("REST server listening on port " + app.get('port'));
});