package com.example.sapidiner.Classes;

import androidx.annotation.Nullable;

public class Food {
    private String name;
    private int price;
    private String category;

    public Food() {
    }

    public Food(String name, int price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Food))
            return false;
        Food food = (Food) obj;
        return food.getName() == this.getName()
                && food.getCategory() == this.getCategory();
    }
}
