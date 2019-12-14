package com.example.sapidiner.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sapidiner.Adapters.OrdersAdapter;
import com.example.sapidiner.Classes.Order;
import com.example.sapidiner.Classes.User;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ViewMyOrdersFragment extends Fragment {

    private View view;
    private RecyclerView rw;
    private ArrayList<Order> listOfOrders = new ArrayList<>();
    private OrdersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private User currentUser;
    private TextView cartIsEmpty;
    private Button goBackButton;

    public ViewMyOrdersFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_my_orders, container, false);

        cartIsEmpty = view.findViewById(R.id.cartIsEmptyText);
        cartIsEmpty.setVisibility(View.INVISIBLE);

        goBackButton = view.findViewById(R.id.buttonGoBack);
        goBackButton.setVisibility(View.INVISIBLE);

        Bundle arguments = getArguments();
        currentUser = (User) arguments.getSerializable("currentUser");

        readData(list -> listingUserOrders());

        return view;
    }

    public void listingUserOrders() {

        if (!listOfOrders.isEmpty()) {
            rw = view.findViewById(R.id.myOrdersRecView);
            layoutManager = new LinearLayoutManager(getContext());
            rw.setLayoutManager(layoutManager);
            rw.addItemDecoration(new DividerItemDecoration(rw.getContext(), DividerItemDecoration.VERTICAL));
            adapter = new OrdersAdapter(listOfOrders,null);
            rw.setAdapter(adapter);
        } else {
            cartIsEmpty.setVisibility(View.VISIBLE);
            goBackButton.setVisibility(View.VISIBLE);
            goBackButton.setOnClickListener(v -> {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            });
        }
    }

/* finds the current users order in the Order table
    which will be given to the recycler view
*  */
    void readData(final FirebaseCallback callback) {
        listOfOrders.clear();
        FirebaseDatabaseManager.Instance.getOrdersReference().child(currentUser.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Order orderObject = dataSnapshot.getValue(Order.class);
                listOfOrders.add(orderObject);
                callback.onCallback(listOfOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallback{
        void onCallback(ArrayList<Order> list);
    }

}
