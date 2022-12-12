package com.ulbululstudios.finalpractice;

public class User {
    String email, password, contactNo, rollNo, gender;

    public User() {
        // empty constructor for firebase
    }

    public User(String email, String password, String contactNo, String rollNo, String gender) {
        this.email = email;
        this.password = password;
        this.contactNo = contactNo;
        this.rollNo = rollNo;
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getGender() {
        return gender;
    }
}
