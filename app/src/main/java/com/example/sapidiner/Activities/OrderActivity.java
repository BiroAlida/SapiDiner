package com.example.sapidiner.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sapidiner.Classes.Orders;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private String o1 = "csulok",o2 = "leves",o3 = "koret";
    private ArrayList<String> myorder = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);


        myorder.add(o1);
        myorder.add(o2);
        myorder.add(o3);
        Orders order = new Orders ("ghgrhger", myorder, 14.0);

        FirebaseDatabaseManager.Instance.addNewOrder("123",order);

    }
}
