package com.mistapp.mistandroid.model;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by aadil on 1/27/17.
 */

public class Event implements Comparable<Event>{

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

    //gets the time in minutes after midnight eg(1:00am would return 60)
    private int getParsedTime(){
        String amPm = time.substring(time.length()-2, time.length());
        String [] t = time.substring(0,time.length()-2).split(":");
        int hour = Integer.parseInt(t[0]);
        if (amPm.equals("pm")){
            hour += 12;
        }
        int minutes = Integer.parseInt(t[1]);
        int numMinutes = 60*hour + minutes;
        return numMinutes;
    }

    public int getMonth(){
        String[] dateArr = date.split("/");
        int month = Integer.parseInt(dateArr[0]);
        return month;
    }

    public int getDay(){
        String[] dateArr = date.split("/");
        int day = Integer.parseInt(dateArr[1]);
        return day;
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

    //returns 1 if current event is before event to be compared
    public int compareTo(Event compareEvent) {
        //same date - compare times
        if (this.getDate().equals(compareEvent.getDate())){
            int currentTimeInMinutes = this.getParsedTime();
            int eventTimeInMinutes = compareEvent.getParsedTime();
            //current time is before compareEvent
            if (currentTimeInMinutes <= eventTimeInMinutes){
                return 1;
            }else{
                return -1;
            }
        }
        //different dates
        else{
            int currentDay = this.getDay();
            int eventDay = compareEvent.getDay();
            //current day is before compareEvent day
            if (currentDay <= eventDay){
                return 1;
            }else{
                return -1;
            }
        }
    }

    public static Comparator<Event> EventTimeComparator
            = new Comparator<Event>() {

        public int compare(Event event1, Event event2) {

            return event2.compareTo(event1);
        }

    };

}
