package com.google.codeu.data;

public class User {

    private String email;
    private String aboutMe;
    private String displayedName;

    public User(String email, String aboutMe, String displayedName) {
        this.email = email;
        this.aboutMe = aboutMe;
        this.displayedName = displayedName;
    }

    public String getEmail(){
        return email;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getDisplayedName() {return displayedName;}
}
