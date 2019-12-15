package com.example.sapidiner;

import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.sapidiner.Classes.Food;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Utilities {
    public static int ADMIN = 1;
    public static void displayErrorSnackbar(View view, String text){
        Snackbar snackbar = Snackbar.make(view,text,Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.red));
        snackbar.show();
    }
    public static String concatFoodListItems(ArrayList<Food> foodList){
        String foods = "";
        for(Food foodItem : foodList)
        {
            foods = foods.concat(foodItem.getName()).concat(" ");
        }
        return foods;
    }
}
