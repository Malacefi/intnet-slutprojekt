/* jslint node: true */
"use strict";

/**
 * A module that contains the main system object!
 * @module roomSystem
 */
var Sequelize = require('sequelize');
var servername = "mysql-vt2016.csc.kth.se";
var username = "pbrink_admin";
var password = "Ua2GZyC9";
var dbname = "pbrink";
var sequelize = new Sequelize(dbname, username, password, {
  host: servername,
  dialect: 'mysql',
});

var connectedClients = [[]];

//var playlistQueue = [[]];
//var latestPlayedSong = [-1];


var Users = sequelize.define('Users', {
  username: {type: Sequelize.STRING, primaryKey: true, allowNull: false},
  nrSongsPlayed: Sequelize.INTEGER,
  password: {type: Sequelize.STRING, allowNull:false},
  salt: {type: Sequelize.STRING, allowNull:false},
  iterations: {type: Sequelize.STRING, allowNull:false}
},{
    timestamps:false
  
})


var Playlists = sequelize.define('Playlists', {
  keyToPlaylist: {type: Sequelize.STRING, allowNull:false, autoIncrement:true, primaryKey:true},
  playlistName:{type: Sequelize.STRING, allowNull:false},
}, {
  timestamps:false
})


var AccessRights = sequelize.define('AccessRights', {
  username: {type: Sequelize.STRING, references: {model:Users, key:'username'}, primaryKey:true},
  keyToPlaylist: {type:Sequelize.STRING, references:{model:Playlists, key:'keyToPlaylist'}, primaryKey:true},
  rights: {type: Sequelize.STRING, allowNull:false}
},{
  timestamps:false
})

var Songs = sequelize.define('Songs', {
  songName: {type:Sequelize.STRING, allowNull:false, primaryKey:true},
  keyToPlaylist: {type:Sequelize.STRING, references:{model:Playlists, key:'keyToPlaylist'}, primaryKey:true},
  link: {type:Sequelize.STRING, allowNull:false},
  nrOfTimesPlayed: Sequelize.INTEGER

},{
  timestamps:false
})

var Messages = sequelize.define('Messages', {
  keyToPlaylist:{type:Sequelize.INTEGER, references: {model:Playlists, key:'keyToPlaylist'}},
  username:{type:Sequelize.STRING, references: {model:Users, key:'username'}},
  message : Sequelize.STRING
}, {
  timestamps:false
})


//åäö


 /*exports.initQueue = function (){
  Playlists.findAll().then(function(result){
    for(var i = 0; result && i<result.length;i++){
      //playlistQueue[result[i].dataValues.keyToPlaylist] = (playlistQueue[result[i].dataValues.keyToPlaylist]) ? playlistQueue[result[i].dataValues.keyToPlaylist] : [];
      //latestPlayedSong[result[i].dataValues.keyToPlaylist] = (latestPlayedSong[result[i].dataValues.keyToPlaylist]) ? latestPlayedSong[result[i].dataValues.keyToPlaylist] : -1;
    }
    console.log("Updated playlistqueues");
    console.log(playlistQueue);
    console.log("latestPlayedSong: ")
    console.log(latestPlayedSong);
  })
 }*/

exports.updatePlaylists = function(){
  getAllPlaylists().then(function(result){
    for(var i = 0; i<result.length;i++){
      connectedClients[result[i].dataValues.keyToPlaylist] = [];
    }
  })
}

exports.getUser = function(username){
  return Users.findOne({where:{username:username}}).then(function(result){
  	if(result)
    	return result.dataValues;
    return result;
  })
}

exports.addUser = function(username, passwordJson){
	return Users.create({username:username, 
					nrSongsPlayed:0, 
					password:passwordJson.password, 
					salt:passwordJson.salt, 
					iterations:passwordJson.iterations}).then(function(){
		return passwordJson.password;
	})
}

exports.getSongs = function(keyToPlaylist){
  return Songs.findAll({where:{keyToPlaylist:keyToPlaylist}}).then(function(result){
    return result;
  })
}

exports.getSong = function(keyToPlaylist, songLink, songName){
  return Songs.findOne({where:{keyToPlaylist:keyToPlaylist, link:songLink, songName:songName}}).then(function(result){
    result.increment('nrOfTimesPlayed', {by:1 });
    return result.dataValues;
   
      
  })
}

exports.addSong = function(keyToPlaylist, songLink, songName){
  return Songs.create({keyToPlaylist:keyToPlaylist, link:songLink, songName:songName, nrOfTimesPlayed:0}).then(function(result){
    return result;
  })
}

exports.removeSong = function(keyToPlaylist, songLink, songName){
  return Songs.findOne({where:{keyToPlaylist:keyToPlaylist, link:songLink, songName:songName}}).then(function(result){
    return result.destroy();
  })
}

exports.removeSongs = function(keyToPlaylist){
  return Songs.destroy({where:{keyToPlaylist:keyToPlaylist}}).then(function(result){
    return result;
  })
}

exports.checkPlaylists = function(username, playlistName){
  return sequelize.query('SELECT AccessRights.username FROM Playlists INNER JOIN AccessRights ON Playlists.keyToPlaylist = AccessRights.keyToPlaylist WHERE AccessRights.username = :username AND Playlists.playlistName = :playlistName',
    {replacements: {username:username, playlistName:playlistName}, type:sequelize.QueryTypes.SELECT}).then(function(result){
      return result;
    })
}

exports.getPlaylists = function(keyToPlaylist){
  return Playlists.findAll({where:{keyToPlaylist:{$in: keyToPlaylist}}}).then(function(result){
    return result;
  })
}

exports.getAllPlaylists = function(){
  return Playlists.findAll().then(function(result){
    return result;
  })
}

 
exports.removePlaylist = function(keyToPlaylist){
  return Playlists.findOne({where:{keyToPlaylist:keyToPlaylist}}).then(function(result){
    return result;
  })
}

exports.getAccessRights = function(username){
  return AccessRights.findAll({where:{username:username}}).then(function(result){
    return result;
  })
}

exports.addPlaylist = function(playlistName, username){
  return Playlists.create({playlistName:playlistName}).then(function(user){
    return user.dataValues.keyToPlaylist;
  })
}

exports.addAccessRights = function(username, keyToPlaylist, rights){
  return AccessRights.create({username:username, keyToPlaylist:keyToPlaylist, rights:rights}).then(function(result){
    return result.dataValues;
  })
}

exports.removeAccessRights = function(keyToPlaylist){
  return AccessRights.destroy({where:{keyToPlaylist:keyToPlaylist}}).then(function(result){
    return result;
  })
}

function getAllPlaylists(){
  return Playlists.findAll().then(function(result){
    return result;
  })
}
//exports.playlistQueue = playlistQueue;
//exports.latestPlayedSong = latestPlayedSong;
exports.connectedClients = connectedClients;

