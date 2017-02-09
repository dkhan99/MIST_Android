package com.mistapp.mistandroid.model;

import java.lang.reflect.Field;

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
    private double phoneNumber;
    private String team;

    public Coach(String birthDate, String email, String team, double phoneNumber, String name, String mistId, String gender) {
        this.birthDate = birthDate;
        this.email = email;
        this.team = team;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.mistId = mistId;
        this.gender = gender;
    }

    public Coach() {
    }

    public String getTeam() {
        return team;
    }

    public double getPhoneNumber() {
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

    public String getUserType() {
        return userType;
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
