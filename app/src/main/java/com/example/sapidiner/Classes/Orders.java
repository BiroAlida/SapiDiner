package com.example.sapidiner.Classes;

import java.util.ArrayList;

public class Orders {
    private String clientid;
    private ArrayList<String> orders;
    private double price;

    public Orders(){}

    public Orders(String clientid, ArrayList<String> orders, double price) {
        this.clientid = clientid;
        this.orders = orders;
        this.price = price;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
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
