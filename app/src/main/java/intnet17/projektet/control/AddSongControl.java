package intnet17.projektet.control;

import android.app.Activity;
import android.content.Context;

import intnet17.projektet.ServerRequest;
import intnet17.projektet.SocketThread;
import intnet17.projektet.model.Account;
import intnet17.projektet.model.Playlist;
import intnet17.projektet.view.AddSongPopup;

/**
 * Created by Emelie on 2017-03-14.
 */

public class AddSongControl implements AddSongPopup.AddSongObserver {

    private String songName;
    private String songLink;

    public AddSongControl(){}

    @Override
    public void onSongNameChange(String name){
        songName = name;
    }

    @Override
    public void onSongLinkChange(String link){
        songLink = link;
    }

    @Override
    public void onAddSongButtonClick(Context context){
        SocketThread.addSong(songName, songLink, context);
    }

    @Override
    public void onAbortAddSongClick(Context context){
        ((Activity)context).finish();
    }
}
