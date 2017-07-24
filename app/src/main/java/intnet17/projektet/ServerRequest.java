package intnet17.projektet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Adapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import intnet17.projektet.control.PlaylistControl;
import intnet17.projektet.model.Account;
import intnet17.projektet.model.Playlist;
import intnet17.projektet.model.Song;
import intnet17.projektet.view.PlaylistListView;
import intnet17.projektet.view.PlaylistView;

/**
 * Created by Emelie on 2017-02-23.
 */

public class ServerRequest implements Serializable{
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final int FAIL = 0;
    public static final int SUCCESS = 1;
    public static final int USERNAME_DOES_NOT_EXIST = 2;
    public static final int WRONG_PASSWORD = 3;
    public static final int PLAYLIST_ALREADY_EXISTS = 4;

    public static final String BASE_URL = "http://130.237.223.175:8080/";

    private static ServerRequest instance = null;

    protected ServerRequest(){}

    public static ServerRequest getInstance(){
        if(instance == null){
            instance = new ServerRequest();
        }
        return instance;
    }

    public class CreateAccountRequest extends PostRequest{
        private Context context;

        public CreateAccountRequest(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... createAccountInfo){
            String requestBody = "username=" + createAccountInfo[0] + "&password=" + createAccountInfo[1];
            return super.doInBackground("/createacc", requestBody);
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == FAIL) {
                Toast.makeText(context, "Användarnamnet upptaget.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Konto skapades!", Toast.LENGTH_LONG).show();
                ((Activity)context).finish();
            }
        }
    }

    public class LoginRequest extends PostRequest{
        private Context context;
        private String username;

        public LoginRequest(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... loginInfo){
            username = loginInfo[0];
            String requestBody = "username=" + loginInfo[0] + "&password=" + loginInfo[1];
            return super.doInBackground("/login", requestBody);
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == USERNAME_DOES_NOT_EXIST) {
                Toast.makeText(context, "Användarnamnet finns inte.", Toast.LENGTH_SHORT).show();
            }else if(result == WRONG_PASSWORD){
                Toast.makeText(context, "Fel lösenord.", Toast.LENGTH_SHORT).show();
            }else if(result == SUCCESS){
                Toast.makeText(context, "Inloggning lyckades.", Toast.LENGTH_LONG).show();
                Account.setAccount(username);
                Intent playlistListIntention = new Intent(context, PlaylistListView.class);
                context.startActivity(playlistListIntention);
            }else{
                Toast.makeText(context, "Nånting gick fel! " + result, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class GetPlaylistsRequest extends GetRequest{
        Context context;
        PlaylistListView.AdapterCallback adapter;

        public GetPlaylistsRequest(Context context, PlaylistListView.AdapterCallback adapter){
            this.context = context;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... username){
            String requestString = "?username=" + username[0];
            return super.doInBackground("/playlists", requestString);
        }

        @Override
        protected void onPostExecute(String result){
            try {
                ArrayList<Playlist> playlists = new ArrayList<Playlist>();
                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    playlists.add(new Playlist(jsonObject.getString("playlistName"), jsonObject.getInt("keyToPlaylist")));
                }
                adapter.adapterFinished(playlists);
            }catch (JSONException e){
                System.err.println("Nånting gick snett med JSON!");
            }
        }
    }

    public class CreatePlaylistRequest extends PostRequest{
        Context context;

        public CreatePlaylistRequest(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... playlistInfo){
            String requestBody = "playlistname=" + playlistInfo[0] + "&admin=" + playlistInfo[1];
            return super.doInBackground("/createplaylist", requestBody);
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == SUCCESS){
                Toast.makeText(context, "Spellistan skapades!", Toast.LENGTH_LONG).show();
                ((Activity)context).finish();
            }else if(result == PLAYLIST_ALREADY_EXISTS){
                Toast.makeText(context, "Det finns redan en spellista med det namnet.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Spellistan kunde inte skapas.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RemovePlaylistRequest extends PostRequest{
        Context context;

        public RemovePlaylistRequest(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... playlistInfo){
            String requestString = "keytoplaylist=" + playlistInfo[0];
            return super.doInBackground("/removeplaylist", requestString);
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result == SUCCESS){
                Toast.makeText(context, "Spellistan togs bort.", Toast.LENGTH_LONG).show();
                PlaylistListView playlistListView = (PlaylistListView)context;
                playlistListView.refresh();
            }else{
                Toast.makeText(context, "Spellistan kunde inte tas bort.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
