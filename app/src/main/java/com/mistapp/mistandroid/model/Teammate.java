package com.mistapp.mistandroid.model;


import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Created by aadil on 1/14/17.
 *
 * This class is used to model a teammate in the my-team page (both teammates and coaches are modeled)
 */

public class Teammate implements Comparable<Teammate>{

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

    public void setName(String name){
        this.name = name;
    }
    public void setPhoneNumber(long phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    //returns 1 if current event is before event to be compared
    public int compareTo(Teammate compareTeam) {

        int compare = getName().compareTo(compareTeam.getName());

        //current is greater
        if (compare > 0){
            return -1;
        }
        else{
            return 1;
        }

    }

    public static Comparator<Teammate> TeammateNameComparator = new Comparator<Teammate>() {

        public int compare(Teammate teammate1, Teammate teammate2) {

            return teammate2.compareTo(teammate1);
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
