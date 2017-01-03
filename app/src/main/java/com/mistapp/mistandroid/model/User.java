package com.mistapp.mistandroid.model;

/**
 * Created by Hasan Qadri
 */

/**
 * Models a user
 */
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String mistId;
    private String email;
    private String password;
    private String imageId;

    public User() {
    }

    public User(String firstName, String lastName, String mistId, String email, String password, String userId) {
        this.id = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mistId = mistId;
        this.password = password;
    }



    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }

        User other = (User) o;

        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMistId() {
        return mistId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

