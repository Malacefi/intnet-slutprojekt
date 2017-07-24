package intnet17.projektet.view;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import intnet17.projektet.R;
import intnet17.projektet.control.CreatePlaylistControl;
import intnet17.projektet.model.Playlist;

public class CreatePlaylistPopup extends AppCompatActivity {

    public interface CreatePlaylistObserver{
        void onPlaylistNameChange(String playlistName);
        void onCreatePlaylistClick(Context context);
        void onAbortCreatePlaylistClick(Context context);
    }

    private CreatePlaylistObserver observer;
    private EditText playlistNameField;
    private Button createPlaylistButton;
    private Button abortCreatePlaylistButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_playlist_popup_layout);

        observer = new CreatePlaylistControl();

        // Gör popupfönstret mindre än orginalfönstret
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int windowWidth = displayMetrics.widthPixels;
        int windowHeight = displayMetrics.heightPixels;
        getWindow().setLayout((int)(windowWidth*0.7), RelativeLayout.LayoutParams.WRAP_CONTENT);

        playlistNameField = (EditText)findViewById(R.id.create_playlist_name_input);
        playlistNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onPlaylistNameChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        createPlaylistButton = (Button)findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(observer != null){
                    observer.onCreatePlaylistClick(CreatePlaylistPopup.this);
                }
            }
        });
        abortCreatePlaylistButton = (Button)findViewById(R.id.abort_create_playlist_button);
        abortCreatePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(observer != null){
                    observer.onAbortCreatePlaylistClick(CreatePlaylistPopup.this);
                }
            }
        });

    }
}
