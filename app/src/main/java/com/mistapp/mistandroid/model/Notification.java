package com.mistapp.mistandroid.model;

/**
 * Created by direwolf on 1/2/17.
 */

public class Notification {

    private String text;
    private String time;
    private boolean seen;

    public Notification(){

    }
    public Notification(String text, String time, boolean seen){
        this.text = text;
        this.time = time;
        this.seen = seen;
    }

    public String getText(){
        return text;
    }
    public String getTime(){
        return time;
    }
    public boolean getSeen(){
        return seen;
    }

}
