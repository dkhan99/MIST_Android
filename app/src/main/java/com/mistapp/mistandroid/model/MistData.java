package com.mistapp.mistandroid.model;

/**
 * Created by Hasan Qadri
 */

/**
 * Models a MistData
 */
public class MistData {
    private String address;
    private String art;
    private float balance;
    private String birthDate;
    private String brackets;
    private String building;
    private float charges;
    private String city;
    private int coach;
    private String email;
    private String gender;
    private String groupProject;
    private String guests;
    private String guests1;
    private String guests2;
    private String guests3;
    private int isRegistered;
    private int isRegistrar;
    private String knowledge;
    private String mistId;
    private String name;
    private int payment;
    private int phoneNumber;
    private String basketball;
    private String state;
    private String team;
    private String tshirtSize;
    private String writing;
    private int zipCode;

    public MistData() {

    }

    public MistData(String address, String art, float balance, String birthDate, String brackets, String building, float charges, String city, int coach, String email, String gender, String groupProject, String guests, String guests1, String guests2, String guests3, int isRegistered, int isRegistrar, String knowledge, String mistId, String name, int payment, int phoneNumber, String basketball, String state, String team, String tshirtSize, String writing, int zipCode) {
        this.address = address;
        this.art = art;
        this.balance = balance;
        this.birthDate = birthDate;
        this.brackets = brackets;
        this.building = building;
        this.charges = charges;
        this.city = city;
        this.coach = coach;
        this.email = email;
        this.gender = gender;
        this.groupProject = groupProject;
        this.guests = guests;
        this.guests1 = guests1;
        this.guests2 = guests2;
        this.guests3 = guests3;
        this.isRegistered = isRegistered;
        this.isRegistrar = isRegistrar;
        this.knowledge = knowledge;
        this.mistId = mistId;
        this.name = name;
        this.payment = payment;
        this.phoneNumber = phoneNumber;
        this.basketball = basketball;
        this.state = state;
        this.team = team;
        this.tshirtSize = tshirtSize;
        this.writing = writing;
        this.zipCode = zipCode;
    }


    public String getAddress() {
        return address;
    }

    public String getArt() {
        return art;
    }

    public float getBalance() {
        return balance;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getBrackets() {
        return brackets;
    }

    public String getBuilding() {
        return building;
    }

    public float getCharges() {
        return charges;
    }

    public String getCity() {
        return city;
    }

    public int getCoach() {
        return coach;
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

    public String getGuests() {
        return guests;
    }

    public String getGuests1() {
        return guests1;
    }

    public String getGuests2() {
        return guests2;
    }

    public String getGuests3() {
        return guests3;
    }

    public int getIsRegistered() {
        return isRegistered;
    }

    public int getIsRegistrar() {
        return isRegistrar;
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

    public int getPayment() {
        return payment;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getBasketball() {
        return basketball;
    }

    public String getState() {
        return state;
    }

    public String getTeam() {
        return team;
    }

    public String getTshirtSize() {
        return tshirtSize;
    }

    public String getWriting() {
        return writing;
    }

    public int getZipCode() {
        return zipCode;
    }

}

