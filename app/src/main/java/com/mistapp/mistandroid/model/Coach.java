package com.mistapp.mistandroid.model;

/**
 * Created by direwolf on 1/2/17.
 */

public class Coach {
    private String birthDate;
    private String email;
    private String gender;
    private String mistId;
    private String name;
    private int phoneNumber;
    private String team;

    public Coach(String birthDate, String email, String team, int phoneNumber, String name, String mistId, String gender) {
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

    public int getPhoneNumber() {
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

    public String getBirthDate() {
        return birthDate;
    }
}
