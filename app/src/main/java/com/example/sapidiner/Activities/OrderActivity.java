package com.example.sapidiner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.sapidiner.Adapters.OrdersAdapter;
import com.example.sapidiner.Classes.Orders;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private RecyclerView rw;
    private ArrayList<Orders> list;
    private OrdersAdapter adapter;
    private FirebaseAuth mAuth;
    private String o1 = "csulok",o2 = "leves",o3 = "koret";
    private ArrayList<String> myorder = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

       /* myorder.add(o1);
        myorder.add(o2);
        myorder.add(o3);
        Orders order = new Orders ("ghgrhger", myorder, 14.0);

        FirebaseDatabaseManager.Instance.addNewOrder("123",order);*/

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        rw = findViewById(R.id.recview);
        rw.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Orders>();

        adapter = new OrdersAdapter(OrderActivity.this, list);
        rw.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().child("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Orders orders = dataSnapshot1.getValue(Orders.class);
                    list.add(orders);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

}
