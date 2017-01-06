package com.mistapp.mistandroid.model;

/**
 * Created by direwolf on 1/3/17.
 */

public class Guest {
    private String name;
    private String guests;
    private String guests1;
    private String guests2;
    private String guests3;
    private String mistId;
    private String team;

    public Guest(String name, String guests, String guests1, String guests2, String guests3, String mistId, String team) {
        this.name = name;
        this.guests = guests;
        this.guests1 = guests1;
        this.guests2 = guests2;
        this.guests3 = guests3;
        this.mistId = mistId;
        this.team = team;
    }

    public String getName() {
        return name;
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

    public String getMistId() {
        return mistId;
    }

    public String getTeam() {
        return team;
    }
}
