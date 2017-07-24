package intnet17.projektet;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import intnet17.projektet.model.Playlist;
import intnet17.projektet.model.Song;
import intnet17.projektet.view.PlaylistView;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Emelie on 2017-03-16.
 */

public class SocketThread{
    private static Socket socket;
    private static Playlist playlist;
    private static ArrayList<Song> songs;
    private Queue<Song> queue;
    private static Handler handler;
    private MediaPlayer mediaPlayer;

    public SocketThread(final Playlist playlist, final PlaylistView.PlaylistViewAdapter adapter){
        this.playlist = playlist;
        queue = new LinkedList<Song>();
        songs = new ArrayList<Song>();
        handler = new Handler(Looper.getMainLooper());
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.err.println("Inne i onCompletion!");
                mp.stop();
                mp.reset();
                Song nextSong;
                if((nextSong = queue.peek()) != null && !mp.isPlaying()){
                    nextSong = queue.remove();
                    playSong(nextSong);
                }
            }
        });
        try {
            socket = IO.socket(ServerRequest.BASE_URL);

            socket.on("songs", new Emitter.Listener(){
                @Override
                public void call(Object... args){
                    System.err.println("Fick en songs!");
                    System.err.println("args[0]: " + args[0]);
                    ArrayList<Song> newSongs = new ArrayList<Song>();
                    if(args != null){
                        try {
                            JSONArray json = (JSONArray)args[0];
                            System.err.println("json: " + json.toString());
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                newSongs.add(new Song(
                                        jsonObject.getString("songName"),
                                        jsonObject.getString("link"),
                                        jsonObject.getInt("keyToPlaylist"),
                                        jsonObject.getInt("nrOfTimesPlayed")));
                            }
                            songs = newSongs;
                            notifyAdapter(adapter);
                        }catch (JSONException e){
                            System.err.println("JSONException när du försökte hämta låtarna!");
                            e.printStackTrace();
                        }
                    }
                }
            }).on("queue", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.err.println("Fick en queue!");
                    System.err.println("args[0] i queue: " + args[0]);
                    Queue newQueue = new LinkedList<Song>();
                    if(args[0] != null){
                        try {
                            JSONArray json = (JSONArray)args[0];
                            System.err.println("json i queue: " + json.toString());
                            for (int i = 0; i < json.length(); i++) {
                                if(json.getJSONObject(i) != null) {
                                    JSONObject jsonObject = json.getJSONObject(i);
                                    newQueue.add(new Song(
                                            jsonObject.getString("songName"),
                                            jsonObject.getString("link"),
                                            jsonObject.getInt("keyToPlaylist"),
                                            jsonObject.getInt("nrOfTimesPlayed")));
                                }
                            }
                            queue = newQueue;
                            if(queue.size() > 0){
                                playSong(queue.remove());
                            }
                            System.err.println("queue: " + queue);
                        }catch (JSONException e){
                            System.err.println("JSONException när du försökte hämta queuen!");
                            e.printStackTrace();
                        }
                    }
                }
            }).on("getqueue", new Emitter.Listener(){
                @Override
                public void call(Object... args){
                    System.err.println("Inne i getqueue!");
                    try {
                        JSONArray json = new JSONArray();
                        JSONObject response = new JSONObject();
                        response.put("socket", args[0]);
                        json.put(response);
                        for(Song song: queue){
                            JSONObject queuedSong = new JSONObject();
                            queuedSong.put("keyToPlaylist", song.getKeyToPlaylist());
                            queuedSong.put("songName", song.getName());
                            queuedSong.put("link", song.getLink());
                            queuedSong.put("nrOfTimesPlayed", song.getNrOfTimesPlayed());
                            json.put(queuedSong);
                        }
                        socket.emit("getqueueresponse", json);
                    }catch (JSONException e){
                        System.err.println("JSONException när servern försökte hämta queuen!");
                    }
                }
            }).on("addtoqueue", new Emitter.Listener(){
                @Override
                public void call(Object... args) {
                    // Håller koll på om kön va tom innan man lade till låten
                    // för då vill man börja spela låtar
                    boolean wasQueueEmpty = false;
                    if(queue.size() == 0){
                        wasQueueEmpty = true;
                    }
                    System.err.println("inne i add queue");
                    try {
                        JSONObject songToAdd = (JSONObject)args[0];
                        queue.add(new Song(songToAdd.getString("songName"),
                                songToAdd.getString("link"),
                                songToAdd.getInt("keyToPlaylist"),
                                songToAdd.getInt("nrOfTimesPlayed")));
                        if(wasQueueEmpty && !mediaPlayer.isPlaying()){
                            System.err.println("Inne i kö är tom och spelaren spelar inte!");
                            playSong(queue.remove());
                        }
                    }catch (JSONException e){
                        System.err.println("JSONException när servern försökte lägga till i queuen!");
                        e.printStackTrace();
                    }
                }
            });

            socket.connect();

            JSONObject keyToPlaylist = new JSONObject();
            keyToPlaylist.put("keytoplaylist", playlist.getId());
            socket.emit("join", keyToPlaylist);

            // Börja spela första låten om kön inte är tom
            if(queue.size() > 0){
                playSong(queue.remove());
            }
        }catch (URISyntaxException e){
            System.err.println("URISyntaxException när du försökte ansluta till socket!");
        }catch (JSONException e){
            System.err.println("JSONException när du försökte ansluta till socket!");
        }
    }

    public static void addSong(String name, String link, final Context context){
        try {
            JSONObject songToAdd = new JSONObject();
            songToAdd.put("keytoplaylist", playlist.getId());
            songToAdd.put("songlink", link);
            songToAdd.put("songname", name);
            socket.emit("addsong", songToAdd, new Ack() {
                @Override
                public void call(Object... args) {
                    if(Integer.parseInt((String)args[0]) == ServerRequest.SUCCESS){
                        toast("Låten lades till!", context);
                        ((Activity)context).finish();
                    }else{
                        toast("Låten kunde inte läggas till.", context);
                    }
                }
            });
        }catch (JSONException e){
            System.err.println("JSONException när servern försökte lägga till en song i serven!");
        }
    }

    public static void removeSong(Song song, final Context context){
        try {
            JSONObject songToRemove = new JSONObject();
            songToRemove.put("keytoplaylist", playlist.getId());
            songToRemove.put("songlink", song.getLink());
            songToRemove.put("songname", song.getName());
            socket.emit("removesong", songToRemove, new Ack() {
                @Override
                public void call(Object... args) {
                    if(Integer.parseInt((String)args[0]) == ServerRequest.SUCCESS){
                        toast("Låten togs bort!", context);
                    }else{
                        toast("Låten kunde inte tas bort!", context);
                    }
                }
            });
        }catch (JSONException e){
            System.err.println("JSONException när servern försökte ta bort en låt från servern!");
        }
    }

    public static void queueSong(Song song, final Context context){
        try {
            JSONObject songToQueue = new JSONObject();
            songToQueue.put("keytoplaylist", playlist.getId());
            songToQueue.put("songlink", song.getLink());
            songToQueue.put("songname", song.getName());
            socket.emit("queuesong", songToQueue);
        }catch (JSONException e){
            System.err.println("JSONException när servern försökte köa en låt!");
        }
    }

    public static ArrayList<Song> returnSongs(){
        return songs;
    }

    public static void toast(final String message, final Context context){
        Runnable runOnMainThread = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        };
        handler.post(runOnMainThread);
    }

    public static void notifyAdapter(final PlaylistView.PlaylistViewAdapter adapter){
        Runnable runOnMainThread = new Runnable() {
            @Override
            public void run() {
                adapter.adapterFinished(songs);
            }
        };
        handler.post(runOnMainThread);
    }

    public void leave(){
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        socket.emit("leave", playlist.getId());
        socket.disconnect();
    }

    public void playSong(Song song){
        try{
            System.err.println("Inne i spela låt");
            System.err.println("låt länk: " + song.getLink());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(song.getLink());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mp){
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        }catch (IOException e){
            System.err.println("IOException när du försökte spela en låt!");
            e.printStackTrace();
        }
    }
}
