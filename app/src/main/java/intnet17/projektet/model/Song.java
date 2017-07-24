package intnet17.projektet.model;

/**
 * Created by Emelie on 2017-02-23.
 */

public class Song {
    private String link;
    private String name;
    private int keyToPlaylist;
    private int nrOfTimesPlayed;

    public Song(String name, String link, int keyToPlaylist, int nrOfTimesPlayed){
        this.name = name;
        this.link = link;
        this.keyToPlaylist = keyToPlaylist;
        this.nrOfTimesPlayed = nrOfTimesPlayed;
    }

    public String getLink(){
        return link;
    }

    public String getName(){
        return name;
    }

    public int getKeyToPlaylist(){
        return keyToPlaylist;
    }

    public int getNrOfTimesPlayed(){
        return nrOfTimesPlayed;
    }
}
