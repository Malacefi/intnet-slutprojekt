package intnet17.projektet.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import intnet17.projektet.SocketThread;
import intnet17.projektet.model.Playlist;
import intnet17.projektet.model.Song;
import intnet17.projektet.view.AddSongPopup;
import intnet17.projektet.view.PlaylistView;

/**
 * Created by Emelie on 2017-02-24.
 */

public class PlaylistControl implements PlaylistView.PlaylistObserver {


    private ArrayList<Song> songs;
    private Playlist currentPlaylist;

    public PlaylistControl(Playlist playlist){
        currentPlaylist = playlist;
        songs = new ArrayList<Song>();
    }

    @Override
    public ArrayList<Song> returnSongs(){
        return  songs;
    }

    @Override
    public void onAddSongClick(Context context) {
        Intent addSongIntention = new Intent(context, AddSongPopup.class);
        addSongIntention.putExtra("playlist", currentPlaylist);
        context.startActivity(addSongIntention);
    }

    @Override
    public void onSongClick(Context context, Song song){
        SocketThread.queueSong(song, context);
    }

    @Override
    public void onRemoveSongClick(Context context, Song song){
        SocketThread.removeSong(song, context);
    }
}
