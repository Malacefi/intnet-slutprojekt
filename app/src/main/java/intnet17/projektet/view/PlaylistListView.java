package intnet17.projektet.view;

import android.app.ListActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import intnet17.projektet.R;
import intnet17.projektet.control.PlaylistListControl;
import intnet17.projektet.model.Playlist;

public class PlaylistListView extends ListActivity {

    PlaylistListViewAdapter adapter;
    private PlaylistListObserver observer;

    public interface PlaylistListObserver{
        ArrayList<Playlist> returnPlaylists();
        void onCreatePlaylistClick(Context context);
        void getPlaylistsFromDatabase(Context context, PlaylistListView.AdapterCallback adapter);
        void onPlaylistClick(Context context, Playlist playlist);
        void onRemovePlaylistClick(Context context, Playlist playlist);
    }

    public interface AdapterCallback{
        void adapterFinished(ArrayList<Playlist> playlists);
    }

    public class PlaylistListViewAdapter extends ArrayAdapter<Playlist> implements AdapterCallback{
        private ArrayList<Playlist> playlists;

        public PlaylistListViewAdapter(Context context, int resourceId, ArrayList<Playlist> playlistList){
            super(context, resourceId, playlistList);
            playlists = playlistList;
        }

        public void adapterFinished(ArrayList<Playlist> newPlaylists){
            this.playlists.clear();
            this.playlists.addAll(newPlaylists);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if(view == null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.playlist_cell, null);
            }

            Playlist currentPlaylist = playlists.get(position);

            if(currentPlaylist != null){
                TextView playlistNameLabel = (TextView) view.findViewById(R.id.playlist_name_label);
                TextView nrOfSongsLabel = (TextView) view.findViewById(R.id.nr_of_songs_label);
                TextView removePlaylistButton = (TextView) view.findViewById(R.id.remove_playlist_label);

                playlistNameLabel.setText(currentPlaylist.getName());
                nrOfSongsLabel.setText("Antalet låtar: " + currentPlaylist.getNrOfSongs());
                removePlaylistButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        observer.onRemovePlaylistClick(PlaylistListView.this, playlists.get(position));
                    }
                });
            }
            return view;
        }
    }

    private TextView createPlaylistButton;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_list_layout);

        observer = new PlaylistListControl();
        ArrayList<Playlist> playlists = observer.returnPlaylists();
        adapter = new PlaylistListViewAdapter(this, R.layout.playlist_cell, playlists);

        createPlaylistButton = (TextView)findViewById(R.id.create_playlist_label);
        createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observer.onCreatePlaylistClick(PlaylistListView.this);
            }
        });

        listView = (ListView)this.findViewById(android.R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    observer.onPlaylistClick(PlaylistListView.this, (Playlist)listView.getItemAtPosition(position));
                }
            }
        );

        setListAdapter(adapter);
    }

    public void refresh(){
        observer.getPlaylistsFromDatabase(this, adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }
}
