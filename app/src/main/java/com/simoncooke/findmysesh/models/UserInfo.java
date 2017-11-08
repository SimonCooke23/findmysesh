package com.simoncooke.findmysesh.models;


public class UserInfo {

    public String firstName;
    public String lastName;
    public int partiesHosted;
    public int seshLevel;
    public String town;
    public String dateOfBirth;
    public String email;


    public UserInfo(){

    }

    public UserInfo(String firstName, String lastName, String email, int partiesHosted, int seshLevel, String town, String dateOfBirth){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.partiesHosted = partiesHosted;
        this.seshLevel = seshLevel;
        this.town = town;
        this.dateOfBirth = dateOfBirth;
    }

}
