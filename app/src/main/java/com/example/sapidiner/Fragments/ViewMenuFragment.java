package com.example.sapidiner.Fragments;


import android.app.ProgressDialog;
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
import com.example.sapidiner.Classes.Food;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewMenuFragment extends Fragment {
    private ArrayList<Food> foodList = new ArrayList<>();
    private ProgressDialog loadingDialog;
    private RecyclerView.Adapter adapter;

    public ViewMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private interface FirebaseCallback{
        void onCallback();
    }
}
