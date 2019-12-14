package com.example.sapidiner.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapidiner.Activities.MenuActivity;
import com.example.sapidiner.Adapters.MenuListAdapter;
import com.example.sapidiner.Classes.Food;
import com.example.sapidiner.Classes.Order;
import com.example.sapidiner.Classes.User;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.example.sapidiner.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private Button sendOrder;
    private User currentUser;


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

        adapter = new MenuListAdapter(getContext(), foodList);

        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.show();

        sendOrder = getView().findViewById(R.id.sendOrder);
        sendOrder.setOnClickListener(this);
        cartImage = getView().findViewById(R.id.imageViewCart);
        cartImage.setOnClickListener(this);

        getCurrentUser();
        readDataFromFirebase(() -> {
            initializeMenuView();
            loadingDialog.dismiss();
        });
    }

    private void getCurrentUser() {
        Bundle arguments = getArguments();
        currentUser = (User) arguments.getSerializable("currentUser");
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
        switch (v.getId()){
            case R.id.imageViewCart:
                Fragment fragment = new ViewMyOrdersFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("currentUser",currentUser);
                fragment.setArguments(bundle);
                ((MenuActivity) getActivity()).replaceFragment(fragment);
                break;
            case R.id.sendOrder:
                if (MenuListAdapter.selectedFoodItems.size() == 0){
                    Utilities.displayErrorSnackbar(getView(),getString(R.string.emptyOrderError));
                } else {
                    FirebaseDatabaseManager.Instance.addNewOrder(currentUser.getName(),new Order(currentUser, MenuListAdapter.selectedFoodItems, MenuListAdapter.totalPrice));
                }
                break;
        }
    }

    private interface FirebaseCallback{
        void onCallback();
    }

}
