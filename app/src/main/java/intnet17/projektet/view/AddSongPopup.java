package intnet17.projektet.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import intnet17.projektet.R;
import intnet17.projektet.control.AddSongControl;
import intnet17.projektet.model.Playlist;

public class AddSongPopup extends AppCompatActivity {

    public interface AddSongObserver{
        void onSongNameChange(String name);
        void onSongLinkChange(String link);
        void onAddSongButtonClick(Context context);
        void onAbortAddSongClick(Context context);
    }

    private AddSongObserver observer;
    private EditText songNameField;
    private EditText songLinkField;
    private Button addSongButton;
    private Button abortAddSongButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_song_popup_layout);

        observer = new AddSongControl();

        // Gör popupfönstret mindre än orginalfönstret
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int windowWidth = displayMetrics.widthPixels;
        getWindow().setLayout((int)(windowWidth*0.7), RelativeLayout.LayoutParams.WRAP_CONTENT);

        songNameField = (EditText)findViewById(R.id.add_song_name_input);
        songNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onSongNameChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        songLinkField = (EditText)findViewById(R.id.add_song_link_input);
        songLinkField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onSongLinkChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addSongButton = (Button)findViewById(R.id.add_song_button);
        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(observer != null){
                    observer.onAddSongButtonClick(AddSongPopup.this);
                }
            }
        });
        abortAddSongButton = (Button)findViewById(R.id.abort_add_song_button);
        abortAddSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(observer != null){
                    observer.onAbortAddSongClick(AddSongPopup.this);
                }
            }
        });
    }
}
