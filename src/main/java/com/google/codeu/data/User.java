package com.google.codeu.data;

public class User {
    private static final String ANONYMOUS_AVATAR = "/images/avatar-placeholder.gif";
    private String email;
    private String aboutMe;
    private String displayedName;
    private String avatarUrl;

    public User(String email, String aboutMe, String displayedName, String avatarUrl) {
        this.email = email;
        this.aboutMe = aboutMe;
        this.displayedName = displayedName;
        this.avatarUrl = avatarUrl;
    }

    public String getEmail(){
        return email;
    }

    public String getAboutMe() {
        return (aboutMe == null)? "" : aboutMe;
    }

    public String getDisplayedName() {return displayedName;}

    public String getAvatarUrl() {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ANONYMOUS_AVATAR;
        }
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
