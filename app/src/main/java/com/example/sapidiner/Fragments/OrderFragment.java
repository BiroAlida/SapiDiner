package com.example.sapidiner.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sapidiner.Adapters.MenuListAdapter;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class OrderFragment extends Fragment {
    private Uri imagePath;
    private String name, category;
    private AlertDialog uploadImageDialog;
    private ProgressDialog loadingDialog;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readDataFromFirebase(new FirebaseCallback() {
            @Override
            public void onCallback() {
                initializeMenuView();
            }
        });
    }

    private void readDataFromFirebase(final FirebaseCallback callback) {
        FirebaseDatabaseManager.Instance.getFoodsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot category : dataSnapshot.getChildren()){
                    //todo
                }
                callback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeMenuView() {
        //todo

    }

    private interface FirebaseCallback{
        void onCallback();
    }
}
