var express = require('express');
var bodyParser = require('body-parser');
var http = require('http');
var fs = require('fs');

var expressSession = require('express-session');
var sharedsession = require('express-socket.io-session');

var port = 8080;

var app = express();
//app.use(express.static(__dirname + '/../public'));
app.use(bodyParser.urlencoded({
  extended: true
}));
app.use(bodyParser.json());
var session = expressSession({
    secret: "MoveFromHereOrTheSecretWillBeOnGit",
    resave: true,
    saveUninitialized: true,
  });
app.use(session);

var httpServer = http.Server(app);
var io = require('socket.io')({'timeout':500}).listen(httpServer);
io.use(sharedsession(session));

var router = require('./controller.js');
app.use('', router);

/*
var socketController = require('./socketController.js');
io.on('connection', function (socket) {
  socketController(socket, io);
});*/

var model = require('./model.js');
var connectionController = require('./connectionController.js');
io.on('connection', function(socket){
	connectionController(socket, io);
})


httpServer.listen(port, function () {
  console.log("server listening on port", port);
});
 