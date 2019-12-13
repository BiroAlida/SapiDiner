package com.example.sapidiner.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sapidiner.Activities.MenuActivity;
import com.example.sapidiner.Activities.OrderActivity;
import com.example.sapidiner.Adapters.MenuListAdapter;
import com.example.sapidiner.Adapters.OrdersAdapter;
import com.example.sapidiner.Classes.Food;
import com.example.sapidiner.Classes.Orders;
import com.example.sapidiner.Classes.User;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewMenuFragment extends Fragment implements View.OnClickListener {
    private ArrayList<Food> foodList = new ArrayList<>();
    private ProgressDialog loadingDialog;
    private RecyclerView.Adapter adapter;
    private ImageView cartImage;
    private String currentUserId;


    public ViewMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_view_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUserId =  getArguments().getString("currentUserId");
        cartImage = getView().findViewById(R.id.imageViewCart);
        cartImage.setOnClickListener(this);


        adapter = new MenuListAdapter(getContext(), foodList);

        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.show();

        readDataFromFirebase(new ViewMenuFragment.FirebaseCallback() {
            @Override
            public void onCallback() {
                initializeMenuView();
                loadingDialog.dismiss();
            }
        });
    }

    private void readDataFromFirebase(final ViewMenuFragment.FirebaseCallback callback) {
        foodList.clear();
        FirebaseDatabaseManager.Instance.getFoodsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot foodData : dataSnapshot.getChildren()){
                    Food food = foodData.getValue(Food.class);
                    foodList.add(food);
                }
                adapter.notifyDataSetChanged();
                callback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeMenuView() {
        RecyclerView foodListView = getView().findViewById(R.id.foodList);
        foodListView.setNestedScrollingEnabled(false);
        foodListView.setLayoutManager(new GridLayoutManager(getContext(),3));
        foodListView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {

        Bundle bundle=new Bundle();
        bundle.putString("currentUserId", currentUserId);
        Fragment newFragment = new ViewMyOrdersFragment();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private interface FirebaseCallback{
        void onCallback();
    }

}
