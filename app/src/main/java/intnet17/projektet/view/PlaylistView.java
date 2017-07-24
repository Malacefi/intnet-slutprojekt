package intnet17.projektet.view;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import intnet17.projektet.R;
import intnet17.projektet.SocketThread;
import intnet17.projektet.control.PlaylistControl;
import intnet17.projektet.model.Playlist;
import intnet17.projektet.model.Song;

public class PlaylistView extends ListActivity {
    Playlist currentPlaylist;
    PlaylistObserver observer;
    PlaylistViewAdapter adapter;
    private ListView listView;
    private SocketThread socketThread;

    public interface PlaylistObserver{
        ArrayList<Song> returnSongs();
        void onSongClick(Context context, Song song);
        void onRemoveSongClick(Context context, Song song);
        void onAddSongClick(Context context);
    }

    public class PlaylistViewAdapter extends ArrayAdapter<Song> {
        private ArrayList<Song> songs;

        public PlaylistViewAdapter(Context context, int resourceId, ArrayList<Song> songList){
            super(context, resourceId, songList);
            songs = songList;
        }

        public void adapterFinished(ArrayList<Song> newSongs){
            if(this.songs != null){
                this.songs.clear();
                this.songs.addAll(newSongs);
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            View view = convertView;

            if(view == null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.song_cell, null);
            }

            Song currentSong = songs.get(position);

            if(currentSong != null){
                TextView songNameLabel = (TextView) view.findViewById(R.id.song_name_label);
                TextView removeSongButton = (TextView) view.findViewById(R.id.remove_song_label);

                songNameLabel.setText(currentSong.getName());
                removeSongButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        observer.onRemoveSongClick(PlaylistView.this, songs.get(position));
                    }
                });
            }
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_layout);

        Intent intent = getIntent();
        currentPlaylist = (Playlist) intent.getExtras().getSerializable("playlist");

        observer = new PlaylistControl(currentPlaylist);

        ArrayList<Song> songs = observer.returnSongs();
        adapter = new PlaylistViewAdapter(this, R.layout.playlist_cell, songs);

        TextView playlistHeaderLabel = (TextView)findViewById(R.id.playlist_header_label);
        String playlistName = currentPlaylist.getName();
        playlistHeaderLabel.setText(playlistName);

        TextView addSongButton = (TextView)findViewById(R.id.add_song_label);
        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observer.onAddSongClick(PlaylistView.this);
            }
        });

        listView = (ListView)this.findViewById(android.R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    observer.onSongClick(PlaylistView.this, (Song)listView.getItemAtPosition(position));
                }
            }
        );

        setListAdapter(adapter);
        socketThread = new SocketThread(currentPlaylist, adapter);
        refresh();
    }

    public void refresh(){
        adapter.adapterFinished(SocketThread.returnSongs());
    }

    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        socketThread.leave();
    }
}
