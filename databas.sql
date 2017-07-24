use pbrink;

#Skapa databasen.....

drop table Users;
drop table Playlists;
drop table AccessRights;
drop table Songs;
drop table Messages;

create table Users (
       username varchar(64) NOT NULL UNIQUE,
       nrSongsPlayed int,
       password varchar(128) NOT NULL,
       salt varchar(64) NOT NULL,
       iterations varchar(64) NOT NULL,
       PRIMARY KEY (username)
)CHARACTER SET utf8 COLLATE utf8_unicode_ci;

create table Playlists (
       keyToPlaylist int NOT NULL AUTO_INCREMENT,
       playlistName varchar(64) NOT NULL,
       PRIMARY KEY (keyToPlaylist)
)CHARACTER SET utf8 COLLATE utf8_unicode_ci;

create table AccessRights (
       username varchar(64) REFERENCES Users(username),
       keyToPlaylist varchar(64) REFERENCES Playlists(keyToPlaylist),
       rights int NOT NULL,
       PRIMARY KEY (username, keyToPlaylist)
)CHARACTER SET utf8 COLLATE utf8_unicode_ci;

create table Songs (
       songName varchar(64) NOT NULL,
       keyToPlaylist varchar(64) REFERENCES Playlists(keyToPlaylist),
       link varchar(64) NOT NULL,
       nrOfTimesPlayed int,
       PRIMARY KEY (songName,keyToPlaylist)
)CHARACTER SET utf8 COLLATE utf8_unicode_ci;

create table Messages(
       keyToPlaylist int REFERENCES Playlists(keyToPlaylist),
       username varchar(64) REFERENCES Users(username),
       message varchar(64)
)CHARACTER SET utf8 COLLATE utf8_unicode_ci;