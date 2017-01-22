package com.mistapp.mistandroid.model;

import java.lang.reflect.Field;

/**
 * Created by direwolf on 1/2/17.
 */

public class Competitor {

    private String userType = "competitor";
    private String art;
    private String birthDate;
    private String brackets;
    private String city;
    private String email;
    private String gender;
    private String groupProject;
    private String writing;
    private String team;
    private long phoneNumber;
    private String sports;
    private String knowledge;
    private String mistId;
    private String name;

    public Competitor(){

    }


    public Competitor(String art, String birthDate, String brackets, String city, String email, String gender, String groupProject, String writing, String team, long phoneNumber, String sports, String knowledge, String mistId, String name) {
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
        this.sports = sports;
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

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public String getSports() {
        return sports;
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
