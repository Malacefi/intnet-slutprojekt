package intnet17.projektet.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Emelie on 2017-02-23.
 */

public class Playlist implements Serializable{
    private String name;
    private int id;
    private ArrayList<Song> queuedSongs;

    public Playlist(){
        queuedSongs = new ArrayList<Song>();
    }

    public Playlist(String name, int id){
        this.name = name;
        this.id = id;
        queuedSongs = new ArrayList<Song>();
    }

    public ArrayList<Song> getQueuedSongs(){
        return  queuedSongs;
    }

    public boolean addSong(Song song){
        if(!queuedSongs.contains(song)){
            queuedSongs.add(song);
            return true;
        }else{
            return false;
        }
    }

    public boolean removeSong(Song song){
        if(queuedSongs.contains(song)){
            queuedSongs.remove(song);
            return true;
        }else{
            return false;
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }

    public int getNrOfSongs(){
        return queuedSongs.size();
    }

    // För att kunna använda contains med arrayList
    @Override
    public boolean equals(Object object){
        if(object == null || object.getClass() != getClass()){
            return false;
        }else{
            Playlist playlist = (Playlist)object;
            if(playlist.getName().equals(this.name)){
                return true;
            }
        }
        return false;
    }
}
