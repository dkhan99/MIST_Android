package com.mistapp.mistandroid.model;

/**
 * Created by direwolf on 1/2/17.
 */

public class Notification {

    private String title;
    private String body;
    private long time;
    private boolean seen;

    public Notification(){

    }
    public Notification(String title, String body, long time, boolean seen){
        this.title = title;
        this.body = body;
        this.time = time;
        this.seen = seen;
    }

    public String getTitle(){
        return title;
    }
    public String getBody(){
        return body;
    }
    public long getTime(){
        return time;
    }
    public boolean getSeen(){
        return seen;
    }

    public void setSeen(boolean seen){
        this.seen = seen;
    }

}
