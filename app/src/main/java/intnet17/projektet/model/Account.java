package intnet17.projektet.model;

/**
 * Created by Emelie on 2017-02-23.
 */

public class Account {

    private static Account account = null;

    private static String username;
    private static int nrOfSongsPlayed;

    protected Account(String username){
        this.username = username;
    }

    public static Account getAccount(){
        return account;
    }

    public static void setAccount(String username){
        if(account != null){
            return;
        }else {
            account = new Account(username);
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNrOfSongsPlayed(int nrOfSongsPlayed){
        this.nrOfSongsPlayed = nrOfSongsPlayed;
    }

    public int getNrOfSongsPlayed(){
        return  nrOfSongsPlayed;
    }

    public String getUsername(){
        return username;
    }
}
