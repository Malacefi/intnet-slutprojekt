package intnet17.projektet.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import intnet17.projektet.R;
import intnet17.projektet.control.CreateAccountControl;

public class CreateAccountView extends AppCompatActivity implements Observer {

    public interface CreateAccountObserver{
        void onNewUsernameChange(String username);
        void onNewPasswordChange(String password);
        void onNewConfirmPasswordChange(String password);
        void onCreateAccountClick(Context context);
        void onAbortClick(Context context);
    }

    private CreateAccountObserver observer;
    private EditText newUsernameField;
    private EditText newPasswordField;
    private EditText newConfirmPasswordField;
    private Button createAccountButton;
    private Button abortCreateAccountButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_layout);

        observer = new CreateAccountControl();

        newUsernameField = (EditText)findViewById(R.id.new_username_input);
        newUsernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onNewUsernameChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newPasswordField = (EditText)findViewById(R.id.new_password_input);
        newPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onNewPasswordChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newConfirmPasswordField = (EditText)findViewById(R.id.new_confirm_password_input);
        newConfirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(observer != null){
                    observer.onNewConfirmPasswordChange(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        createAccountButton = (Button)findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observer.onCreateAccountClick(CreateAccountView.this);
            }
        });
        abortCreateAccountButton = (Button)findViewById(R.id.abort_create_account_button);
        abortCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observer.onAbortClick(CreateAccountView.this);
            }
        });
    }

    @Override
    public void update(Observable o, Object arg){

    }
}
