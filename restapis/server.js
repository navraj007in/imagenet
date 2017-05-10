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
app.get('/users/:id/follow', users.followUser);
app.get('/users/:id/unfollow', users.unfollowUser);
app.get('/users/:id/profile', users.findByIdprofile);
app.get('/users/:id/follow', users.followUser);
app.get('/users/:id/unfollow', users.unfollowUser);
app.get('/users/:id/posts', users.findPosts);

app.get('/posts', posts.findAll);
app.get('/posts/:id', posts.findById);
app.post('/posts', posts.addPost);
app.put('/posts/:id', posts.updatePost);
app.delete('/posts/:id', posts.deletePost);
app.delete('/posts/:id/comment/:commentid', posts.deleteComment);

app.get('/posts/:id/likes', posts.listLikes);
app.get('/posts/:id/like', posts.like);
//app.get('/posts/:id/comment', posts.likeComments);
app.post('/posts/:id/comment', posts.postcomment);
app.delete('/posts/:id/comments', posts.deleteComment);
app.get('/posts/:id/comments', posts.listComments);
app.get('/posts/:id/reactions', posts.reactions);
app.get('/posts/:id/deletecomments', posts.deleteCommentGet);



http.createServer(app).listen(app.get('port'), function () {
    console.log("REST server listening on port " + app.get('port'));
});