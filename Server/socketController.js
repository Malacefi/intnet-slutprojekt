/* jslint node: true */
"use strict";

var model = require('./model.js');
var playqueue = [[]];
module.exports = function (socket, io) {

  // user joins playlist
  socket.on('/joinplaylist', function (req) {
    console.log(req);
    if(playqueue[req][0]==null){
    	//queue song from database.
    	model.getSongs(req).then(function(result){
    		var toPlay = randomInt(0,result.length);

    	})
    }
  });

  
};

function randomInt (low, high) {
    return Math.floor(Math.random() * (high - low) + low);
}

 