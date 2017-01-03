package com.mistapp.mistandroid.model;

/**
 * Created by direwolf on 1/2/17.
 */

public class CompetitorRegistration {
    private String art;
    private String birthDate;
    private String brackets;
    private String city;
    private String email;
    private String gender;
    private String groupProject;
    private String writing;
    private String team;
    private int phoneNumber;
    private String basketball;
    private String knowledge;
    private String mistId;
    private String name;

    public CompetitorRegistration(String art, String birthDate, String brackets, String city, String email, String gender, String groupProject, String writing, String team, int phoneNumber, String basketball, String knowledge, String mistId, String name) {
        this.art = art;
        this.birthDate = birthDate;
        this.brackets = brackets;
        this.city = city;
        this.email = email;
        this.gender = gender;
        this.groupProject = groupProject;
        this.writing = writing;
        this.team = team;
        this.phoneNumber = phoneNumber;
        this.basketball = basketball;
        this.knowledge = knowledge;
        this.mistId = mistId;
        this.name = name;
    }

    public String getArt() {
        return art;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getBrackets() {
        return brackets;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getGroupProject() {
        return groupProject;
    }

    public String getWriting() {
        return writing;
    }

    public String getTeam() {
        return team;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getBasketball() {
        return basketball;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public String getMistId() {
        return mistId;
    }

    public String getName() {
        return name;
    }
}
