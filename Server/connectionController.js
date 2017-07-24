var model = require("./model.js");
var protocol = require("./protocol.json")

//model.updatePlaylists();

module.exports = function(socket, io){
	console.log("user connected");
	//console.log(socket);
	

	socket.on('join', function(req){
		console.log("Connect request from client: ");
		console.log(socket.id);
		//If the room is not empty, get a queue from another person in the room
		if(typeof io.sockets.adapter.rooms[req.keytoplaylist]!=='undefined'){
			var clients_in_the_room = io.sockets.adapter.rooms[req.keytoplaylist].sockets; 
			for (var clientId in clients_in_the_room ) {
				console.log('client: %s', clientId);
				io.to(clientId).emit('getqueue', socket.id);
				break;
			}
		}
		socket.join(req.keytoplaylist);

		

		
		model.getSongs(req.keytoplaylist).then(function(response){
			var songList = [];
			for(var i = 0; i<response.length; i++){
				songList.push(response[i].dataValues);
			}
			//console.log(songList);
			socket.emit('songs',songList);
		})

	})

	socket.on('getsongs', function(req){
		model.getSongs(req.playlist).then(function(response){
			var songList = [];
			for(var i = 0; i<response.length; i++){
				songList.push(response[i].dataValues);
			}
			//console.log(songList);
			socket.emit('songs',songList);
		})
	})

	//Send queue recieved from user to recently joined user
	socket.on('getqueueresponse', function(req){
		var queue = [];
		console.log("Get queueresponse send to: ");
		console.log(req[0].socket);
		for(var i = 0; i<req.length-1;i++){
			console.log("i: " + i + " element " + req[i]);
			queue[i] = req[i+1];
		}
		console.log("queue: ");
		console.log(queue);
		console.log("queue spliced");
		io.to(req[0].socket).emit('queue', queue);

	})

	socket.on('queuesong', function(req){
		//Hämta låt ifrån databas
		model.getSong(req.keytoplaylist, req.songlink, req.songname).then(function(result){
			io.sockets.in(req.keytoplaylist).emit('addtoqueue', result);
		})
	})

	socket.on('removesong', function(req){
		model.removeSong(req.keytoplaylist.toString(), req.songlink.toString(), req.songname.toString()).then(function(result){
			model.getSongs(req.keytoplaylist).then(function(response){
				var songList = [];
				for(var i = 0; i<response.length; i++){
					songList.push(response[i].dataValues);
				}
				//console.log(songList);
				io.sockets.in(req.keytoplaylist).emit('songs',songList);
			})
		})
	})

	socket.on('addsong', function(req, callback){
		console.log('adding song');
		console.log(req.keytoplaylist);
		console.log(req.songlink);

		model.addSong(req.keytoplaylist.toString(),req.songlink.toString(),req.songname.toString()).then(function(result){
			model.getSongs(req.keytoplaylist).then(function(response){
				callback(protocol.SUCCESS);
				var songList = [];
				for(var i = 0; i<response.length; i++){
					songList.push(response[i].dataValues);
				}
				//console.log(songList);
				io.sockets.in(req.keytoplaylist).emit('songs', songList);
			}).catch(function(err){
				callback(protocol.FAIL);
			})
		})
	})
 
	socket.on('leave', function(req){
		
		//remove socket from the server it is in.
		//model.connectedClients[req.keytoplaylist].splice(model.connectedClients[req.keytoplaylist].indexOf(socket),1);
	})

	socket.on('disconnect', function(req){
		/*for(var i = 0; i<model.connectedClients.length; i++){
			if(model.connectedClients[i] !== 'undefined')
				model.connectedClients[i].splice(model.connectedClients[i].indexOf(socket),1);
		}*/
		console.log("user disconnected");
		socket.disconnect(); 
	})
};