package com.example.sapidiner.Classes;

import java.util.ArrayList;

public class Orders {

    private User user;
    private ArrayList<String> orders;
    private double price;

    public Orders(){}

    public Orders(User user, ArrayList<String> orders, double price) {
        this.user = user;
        this.orders = orders;
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<String> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<String> orders) {
        this.orders = orders;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}
