package com.mistapp.mistandroid.model;

/**
 * Created by direwolf on 1/2/17.
 */

public class Coach {

    private String userType = "coach";
    private String birthDate;
    private String email;
    private String gender;
    private String mistId;
    private String name;
    private long phoneNumber;
    private String team;

    public Coach(){

    }


    public Coach(String birthDate, String email, String team, long phoneNumber, String name, String mistId, String gender) {
        this.birthDate = birthDate;
        this.email = email;
        this.team = team;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.mistId = mistId;
        this.gender = gender;
    }

    public String getTeam() {
        return team;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getMistId() {
        return mistId;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }


    public String getBirthDate() {
        return birthDate;
    }
}
