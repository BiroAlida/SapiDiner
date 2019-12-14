package com.example.sapidiner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;

import com.example.sapidiner.Adapters.OrdersAdapter;
import com.example.sapidiner.Classes.Order;
import com.example.sapidiner.Classes.User;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.example.sapidiner.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity implements OrdersAdapter.MyViewHolder.OrderClickListener {
    private RecyclerView rw;
    private ArrayList<Order> list = new ArrayList<>();
    private OrdersAdapter adapter;
    private Order selectedOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        rw = findViewById(R.id.recview);
        rw.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrdersAdapter(list, this);
        rw.setAdapter(adapter);

        FirebaseDatabaseManager.Instance.getOrdersReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot orderData : dataSnapshot.getChildren()) {
                    Order order = orderData.getValue(Order.class);
                    list.add(order);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utilities.displayErrorSnackbar(findViewById(R.id.viewContainer), getString(R.string.errorText));
            }
        });
    }

    @Override
    public void onOrderClick(int position) {
        showDialog();
        selectedOrder = list.get(position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms(selectedOrder.getUser().getPhoneNumber());
                deleteOrder();
            }
        }
    }

    private void deleteOrder() {
        FirebaseDatabaseManager.Instance.getOrdersReference().child(selectedOrder.getUser().getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //refresh recyclerview
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showDialog() {
        CharSequence[] items = new String[]{getString(R.string.orderFinished), };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, which) -> {
            if (which == 0) {
                if (ContextCompat.checkSelfPermission(OrderActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 0);
                } else {
                    //if permission is already granted send sms and delete order
                    sendSms(selectedOrder.getUser().getPhoneNumber());
                    deleteOrder();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendSms(String phoneNum) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, getResources().getString(R.string.orderFinished), null, null);
    }
}
