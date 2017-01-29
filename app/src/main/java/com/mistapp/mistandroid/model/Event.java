package com.mistapp.mistandroid.model;

import java.util.Date;

/**
 * Created by aadil on 1/27/17.
 */

public class Event {

    private String name;
    private String location; //building name
    private String date;     // eg: '03/17'
    private String duration; // eg: '2hr30min'
    private int roomNumber;
    private String time;     // '11:20am'

    public Event(){
        this.name = "name";
        this.location = "location";
        this.date = "date";
        this.duration = "duration";
        this.roomNumber = 3;
        this.time = "time";
    }

    public Event(String name, String location, String date, String duration, int roomNumber, String time){
        this.name = name;
        this.location = location;
        this.date = date;
        this.duration = duration;
        this.roomNumber = roomNumber;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getTime() {
        return time;
    }

    public void setName(String name){
        this.name = name;
    }
}
