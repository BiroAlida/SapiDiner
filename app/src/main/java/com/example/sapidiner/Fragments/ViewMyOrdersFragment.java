package com.example.sapidiner.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sapidiner.Adapters.OrdersAdapter;
import com.example.sapidiner.Classes.Orders;
import com.example.sapidiner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ViewMyOrdersFragment extends Fragment {

    private View view;
    private Orders orderObject;
    private RecyclerView rw;
    private ArrayList<Orders> listOfOrders = new ArrayList<>();
    private OrdersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String currentUserId;
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

        currentUserId =  getArguments().getString("currentUserId");
        readData(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Orders> list) {

                listingUserOrders();
            }
        });

        return view;
    }

    public void listingUserOrders() {

        if (!listOfOrders.isEmpty()) {
            rw = view.findViewById(R.id.myOrdersRecView);
            layoutManager = new LinearLayoutManager(getContext());
            rw.setLayoutManager(layoutManager);
            rw.addItemDecoration(new DividerItemDecoration(rw.getContext(), DividerItemDecoration.VERTICAL));
            adapter = new OrdersAdapter(getContext(), listOfOrders);
            rw.setAdapter(adapter);
        } else {
            cartIsEmpty.setVisibility(View.VISIBLE);
            goBackButton.setVisibility(View.VISIBLE);
            goBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack();
                }
            });
        }
    }

/* finds the current users order in the Orders table
    which will be given to the recycler view
*  */
    void readData(final FirebaseCallback callback) {

        FirebaseDatabase.getInstance().getReference("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listOfOrders.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {

                    for(DataSnapshot users : postSnapshot.getChildren())
                    {
                        if(users.getKey().equals("user"))
                        {
                            for(DataSnapshot clientid : users.getChildren())
                            {
                                if(clientid.getKey().equals("userId"))
                                {
                                    if(clientid.getValue().equals(currentUserId))
                                    {
                                        orderObject = postSnapshot.getValue(Orders.class);
                                        listOfOrders.add(orderObject);
                                    }

                                }
                            }
                        }
                    }

                }

                callback.onCallback(listOfOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private interface FirebaseCallback{
        void onCallback(ArrayList<Orders> list);
    }

}
