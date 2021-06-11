package com.example.pic;

public class User {

    private String email;

    public User() {
        //empty constructor needed
    }

    public User(String email)
    {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
