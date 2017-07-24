package intnet17.projektet.control;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import intnet17.projektet.ServerRequest;
import intnet17.projektet.model.Account;
import intnet17.projektet.view.CreateAccountView;
import intnet17.projektet.view.PlaylistListView;
import intnet17.projektet.view.StartpageView;

/**
 * Created by Emelie on 2017-02-23.
 */

public class StartpageControl implements StartpageView.StartpageObserver{

    private String username;
    private String password;

    @Override
    public void onUsernameChange(String username){
        this.username = username;
    }

    @Override
    public void onPasswordChange(String password){
        this.password = password;
    }

    @Override
    public void onLoginClick(Context context){
        if(username.length() <= 0 || password.length() <= 0){
            Toast.makeText(context, "Ingen av fälten får vara tomma.", Toast.LENGTH_SHORT).show();
        }else{
            ServerRequest.getInstance().new LoginRequest(context).execute(username, password);
        }
    }

    @Override
    public void onCreateAccountClick(Context context){
        Intent createAccountIntention = new Intent(context, CreateAccountView.class);
        context.startActivity(createAccountIntention);
    }
}
