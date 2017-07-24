package intnet17.projektet.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import intnet17.projektet.R;
import intnet17.projektet.control.StartpageControl;

public class StartpageView extends AppCompatActivity {

    public interface StartpageObserver{
        void onUsernameChange(String username);
        void onPasswordChange(String password);
        void onLoginClick(Context context);
        void onCreateAccountClick(Context context);
    }

    private StartpageObserver observer;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private TextView createAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage_layout);

        observer = new StartpageControl();

        usernameField = (EditText)findViewById(R.id.username_input);
        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onUsernameChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordField = (EditText)findViewById(R.id.password_input);
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onPasswordChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(observer != null){
                    observer.onLoginClick(StartpageView.this);
                }
            }
        });
        createAccountLink = (TextView)findViewById(R.id.create_account_link);
        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(observer != null){
                    observer.onCreateAccountClick(StartpageView.this);
                }
            }
        });
    }

}
