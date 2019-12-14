package com.example.sapidiner.Classes;

import java.util.ArrayList;

public class Order {

    private User user;
    private ArrayList<Food> foodList;
    private double totalPrice;

    public Order(){}

    public Order(User user, ArrayList<Food> orders, double totalPrice) {
        this.user = user;
        this.foodList = orders;
        this.totalPrice = totalPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(ArrayList<Food> foodList) {
        this.foodList = foodList;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }


}
