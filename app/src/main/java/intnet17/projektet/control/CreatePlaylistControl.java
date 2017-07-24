package intnet17.projektet.control;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

import intnet17.projektet.ServerRequest;
import intnet17.projektet.model.Account;
import intnet17.projektet.model.Playlist;
import intnet17.projektet.view.CreatePlaylistPopup;

/**
 * Created by Emelie on 2017-02-23.
 */

public class CreatePlaylistControl implements CreatePlaylistPopup.CreatePlaylistObserver {
    public CreatePlaylistControl(){}

    private String playlistName;

    @Override
    public void onPlaylistNameChange(String playlistName){
        this.playlistName = playlistName;
    }

    @Override
    public void onCreatePlaylistClick(Context context){
        ServerRequest.getInstance().new CreatePlaylistRequest(context).execute(playlistName, Account.getAccount().getUsername());
    }

    @Override
    public void onAbortCreatePlaylistClick(Context context){
        ((Activity)context).finish();
    }
}
