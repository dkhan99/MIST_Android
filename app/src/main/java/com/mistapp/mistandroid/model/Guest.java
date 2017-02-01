package com.mistapp.mistandroid.model;

import java.lang.reflect.Field;

/**
 * Created by direwolf on 1/3/17.
 */

public class Guest {

    private String userType = "guest";
    private String name;
    private String guests;
    private String guests1;
    private String guests2;
    private String guests3;
    private String mistId;
    private String team;

    public Guest(){

    }

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

    public String getUserType() {
        return userType;
    }

    public String getTeam() {
        return team;
    }

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