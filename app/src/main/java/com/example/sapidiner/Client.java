package com.example.sapidiner;

public class Client {

    String userName;
    String phoneNumber;
    String userPassword;
    Integer userType;

    public Client()
    {

    }

    public Client(String userName, String phoneNumber, String userPassword,Integer userType) {

        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.userPassword = userPassword;
        this.userType = userType;
    }


}
