package com.example.xitipetugas;

public class UserModel {
    private String userId;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String profilePic;
    private String gender;
    private String role;

    public UserModel(String userId, String birthDate, String firstName, String lastName, String profilePic, String gender, String role) {
        this.userId = userId;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic = profilePic;
        this.gender = gender;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getGender() {
        return gender;
    }

    public String getRole() {
        return role;
    }
}
