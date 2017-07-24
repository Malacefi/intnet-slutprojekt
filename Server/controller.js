/* jslint node: true */
"use strict";
var protocol = require('./protocol.json');
var express = require('express');
var crypto = require('crypto');
var router = express.Router();
var model = require("./model.js");
var async = require('async');
//var connectionController = require('./connectionController.js');

//model.initQueue();

/*
	GET Users/:username
*/

router.post('/login', function(req, res){
	//console.log(res);
	model.getUser(req.body.username).then(function(foundUser){
		if(typeof foundUser !== 'undefined' && foundUser){
			//user finns 
			if(checkPassword(foundUser.password,foundUser.salt,foundUser.iterations,req.body.password)){
				console.log("login succesfull");
				res.send(protocol.SUCCESS);
			}
			else{
				console.log('wrong password');
				res.send(protocol.WRONG_PASSWORD);
			}
		}
		else{
			console.log("no such username");
			res.send(protocol.USERNAME_DOES_NOT_EXIST);
		}
	})
}) 

/*router.post('/removesong', function(req,res){
	model.removeSong(req.body.playlistkey, req.body.song, req.body.songname).then(function(response){
		res.send(protocol.SUCCESS);
	})
})*/

/*
	POST Users/
*/

router.post('/createacc', function(req,res){
	//Användarnamn kommer i body.username, password i body.password
	var username = req.body.username;
	var password = req.body.password;
	//Kolla om det finns en användare med detta username
	model.getUser(username).then(function(foundUser){
		if(typeof foundUser !== 'undefined' && foundUser){
			res.send(protocol.FAIL);
		} else {
			var passwordJson = hashPassword(password);
			model.addUser(username,passwordJson).then(function(ret){
				console.log(ret);
				res.send(protocol.SUCCESS);
			})
		}
	})
}) 

/*router.get('/playSong', function(req,res){
	model.getSong(res.query.keytoplaylist, res.query.songname, res.query.songlink).then(function(result){
		//Pipe song async to all in res.
		async.forEach(Object.keys(model.connectedClients[req.query.keytoplaylist]), function(socket, callback){
			socket.send(protocol.PLAY_SONG)
				youtubestream(result.link).pipe(socket).then(function(){
					callback();
				});

		}, function(err){
			console.log("stream done");
		})
		youtubestream(result.link).pipe(res);
	
	})
})*/

/*
	GET Playlists/:username
	GET Users/:username/playlists
*/

router.get('/playlists', function(req,res){
	model.getAccessRights(req.query.username).then(function(result){
		var accessRightsList = [];
		for(var i = 0; i<result.length;i++){
			accessRightsList.push(result[i].dataValues.keyToPlaylist);
		}
		model.getPlaylists(accessRightsList).then(function(playlists){
			var userplaylists = [];
			for(var j = 0; j<playlists.length;j++){
				userplaylists.push(playlists[j].dataValues);
			}
			console.log(userplaylists);
			res.send(userplaylists);
		})
	})
})

/*
* DELETE Playlists/:keytoplaylist
* 
*
*/

router.post('/removeplaylist', function(req,res){
	model.removePlaylist(req.body.keytoplaylist).then(function(response){
		model.removeAccessRights(req.body.keytoplaylist).then(function(aresponse){
			model.removeSongs(req.body.keytoplaylist).then(function(songresponse){
				res.send(protocol.SUCCESS);
			})
			
		})
	})
})

/*
* UPDATE playlists/:keytoplaylist 
*
*/
router.post('/addusertoplaylist', function(req,res){
	model.getUser(req.body.username).then(function(user){
		if(typeof user !== 'undefined' && user){
			model.addAccessRights(req.body.username, req.body.playlistkey, "user").then(function(result){
				res.send(protocol.SUCCESS);
			})
		}
		else{
			res.send(protocol.USERNAME_DOES_NOT_EXIST);
		}


	})
	
})

/*
	POST playlists/
*/
router.post('/createplaylist', function(req,res){
	console.log(req.body.playlistname);
	model.checkPlaylists(req.body.admin, req.body.playlistname).then(function(alreadyExists){
		console.log(alreadyExists.length);
		if(alreadyExists.length != 0){
			res.send(protocol.PLAYLIST_ALREADY_EXISTS)
		}
		else{
			model.addPlaylist(req.body.playlistname).then(function(response){
				//console.log(response);
				//connectionController.addPlayList(response);
				model.addAccessRights(req.body.admin, response, "admin").then(function(accessRightsResponse){
					//model.initQueue();
					model.updatePlaylists();
					res.send(protocol.SUCCESS);
				})
			})
		}
	})
	
})

/*router.get('/songs', function(req,res){
	model.getSongs(req.query.playlist).then(function(response){
		var songList = [];
		for(var i = 0; i<response.length; i++){
			songList.push(response[i].dataValues);
		}
		//console.log(songList);
		res.send(songList);
	}) 
})*/

/*router.post('/queuesong', function(req,res){
	//songname, link, keytoplaylist
	model.playlistQueue[req.body.keytoplaylist].push({link:req.body.link,songname:req.body.songname});
	res.send(protocol.SUCCESS);
})*/

/*router.get('/nextsong', function(req,res){
	//keytoplaylist, latestplayed
	//check if latestplayed matches queue[0], if it does check the next one etc.
	if(!model.playlistQueue[req.query.keytoplaylist] || model.playlistQueue[req.query.keytoplaylist].length==0){
		res.send(protocol.NO_QUEUED_SONGS);
	}
	else{
		for(var i = 0;i<model.playlistQueue[req.query.keytoplaylist][i].length; i++){
			if(req.query.latestplayed != model.playlistQueue[req.query.keytoplaylist][i]){
				res.send(model.playlistQueue[req.query.keytoplaylist][i]);
				model.playlistQueue[req.query.keytoplaylist].splice(0,i);
				break;
			}
		}	
	}
	

})*/


/*router.post('/addsong', function(req,res){
	model.addSong(req.body.playlistkey, req.body.song, req.body.songname).then(function(result){
		res.send(protocol.SUCCESS);
	})
})*/

router.get('/', function(req,res){
	res.json(protocol.SUCCESS);
})

function hashPassword(password){
	var salt = crypto.randomBytes(32).toString("hex");
	var iterations = 10000;
	var hash = crypto.pbkdf2Sync(password,salt,iterations,64, 'sha512').toString("hex");
	return{
		salt:salt,
		iterations:iterations,
		password:hash
	}
}

function checkPassword(savedHash,savedSalt,savedIterations,passwordAttempt){
	var hash = crypto.pbkdf2Sync(passwordAttempt,savedSalt.toString("hex"),parseInt(savedIterations),64, 'sha512').toString("hex");
	return savedHash == hash;
}


module.exports = router;
