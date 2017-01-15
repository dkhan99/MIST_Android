package com.mistapp.mistandroid.model;

/**
 * Created by aadil on 1/14/17.
 *
 * This class is used to model a teammate in the my-team page
 */

public class Teammate {

    private String name;
    private long phoneNumber;
    private String mistId;
    private long isCompetitor;

    public Teammate(){

    }

    public Teammate(String name, long phoneNumber, String mistId, long isCompetitor){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.mistId = mistId;
        this.isCompetitor = isCompetitor;
    }

    public String getName(){
        return this.name;
    }
    public long getPhoneNumber(){
        return this.phoneNumber;
    }
    public String getMistId(){
        return this.mistId;
    }
    public long getIsCompetitor() {
        return isCompetitor;
    }
}
