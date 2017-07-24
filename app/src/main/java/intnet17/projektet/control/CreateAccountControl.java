package intnet17.projektet.control;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import intnet17.projektet.ServerRequest;
import intnet17.projektet.view.CreateAccountView;

/**
 * Created by Emelie on 2017-02-23.
 */

public class CreateAccountControl implements CreateAccountView.CreateAccountObserver{

    private String username;
    private String password;
    private String confirmPassword;


    public CreateAccountControl(){}

    @Override
    public void onNewUsernameChange(String username){
        this.username = username;
    }

    @Override
    public void onNewPasswordChange(String password){
        this.password = password;
    }

    @Override
    public void onNewConfirmPasswordChange(String password){
        confirmPassword = password;
    }

    @Override
    public void onCreateAccountClick(Context context){
        if(username == null || username.length() < 3){
            Toast.makeText(context, "Användarnamnet är för kort.", Toast.LENGTH_SHORT).show();
        }else if(password == null || password.length() < 4){
            Toast.makeText(context, "Lösenordet är för kort.", Toast.LENGTH_SHORT).show();
        }else if(confirmPassword == null || !password.equals(confirmPassword)){
            Toast.makeText(context, "Lösenordet är inte bekräftat.", Toast.LENGTH_SHORT).show();
        }
        else{
            ServerRequest.getInstance().new CreateAccountRequest(context).execute(username, password);
        }
    }

    @Override
    public void onAbortClick(Context context){
        ((Activity)context).finish();
    }
}
