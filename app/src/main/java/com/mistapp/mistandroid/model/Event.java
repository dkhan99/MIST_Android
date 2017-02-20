package com.mistapp.mistandroid.model;

import android.content.Intent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by aadil on 1/27/17.
 */

public class Event implements Comparable<Event>{

    private String name;
    private String location; //building name
    private String date;     // eg: '03/17'
    private String endTime; // eg: '12:00pm'
    private ArrayList<Long> roomNumbers;
    private String startTime; // '11:20am'
    private long isCompetition;

    public Event(){
    }

    public Event(String name, String location, String date, String endTime, ArrayList<Long> roomNumbers, String startTime, long isCompetition){
        this.name = name;
        this.location = location;
        this.date = date;
        this.endTime = endTime;
        this.roomNumbers = roomNumbers;
        this.startTime = startTime;
        this.isCompetition = isCompetition;
    }

    //gets the time in minutes after midnight eg(1:00am would return 60)
    private int getParsedTime(){
        String amPm = startTime.substring(startTime.length()-2, startTime.length());
        String [] t = startTime.substring(0,startTime.length()-2).split(":");
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

    public long getIsCompetition(){
        return isCompetition;
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

    public String getEndTime() {
        return endTime;
    }

    public ArrayList<Long> getRoomNumbers() {
        return roomNumbers;
    }

    public String getStartTime() {
        return startTime;
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

    /*
     * Prints all the fields of the object nicely
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}
