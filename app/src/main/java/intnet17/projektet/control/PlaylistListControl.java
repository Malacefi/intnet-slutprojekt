package intnet17.projektet.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import intnet17.projektet.ServerRequest;
import intnet17.projektet.model.Account;
import intnet17.projektet.model.Playlist;
import intnet17.projektet.view.CreatePlaylistPopup;
import intnet17.projektet.view.PlaylistListView;
import intnet17.projektet.view.PlaylistView;

/**
 * Created by Emelie on 2017-02-23.
 */

public class PlaylistListControl implements PlaylistListView.PlaylistListObserver{

    private static ArrayList<Playlist> playlists;

    public PlaylistListControl(){
        playlists = new ArrayList<Playlist>();
    }

    @Override
    public void onPlaylistClick(Context context, Playlist playlist){
        Intent playlistIntention = new Intent(context, PlaylistView.class);
        playlistIntention.putExtra("playlist", playlist);
        context.startActivity(playlistIntention);
    }

    @Override
    public ArrayList<Playlist> returnPlaylists(){
        return playlists;
    }

    @Override
    public void onCreatePlaylistClick(Context context){
        Intent createPlaylistIntention = new Intent(context, CreatePlaylistPopup.class);
        context.startActivity(createPlaylistIntention);
    }

    @Override
    public void getPlaylistsFromDatabase(Context context, PlaylistListView.AdapterCallback adapter){
        ServerRequest.getInstance().new GetPlaylistsRequest(context, adapter).execute(Account.getAccount().getUsername());
    }

    @Override
    public void onRemovePlaylistClick(Context context, Playlist playlist){
        ServerRequest.getInstance().new RemovePlaylistRequest(context).execute(Integer.toString(playlist.getId()));
    }
}
