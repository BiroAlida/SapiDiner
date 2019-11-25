package com.example.sapidiner.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sapidiner.Adapters.MenuListAdapter;
import com.example.sapidiner.Classes.FoodCategory;
import com.example.sapidiner.Database.FirebaseDatabaseManager;
import com.example.sapidiner.R;
import com.example.sapidiner.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetMenuFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private View view, dialogView;
    private Uri imagePath;
    private String name, category;
    private AlertDialog uploadImageDialog;
    private ArrayList<FoodCategory> categories = new ArrayList<>();
    private ProgressDialog loadingDialog;

    public SetMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_set_menu, container, false);
        Button addFood = view.findViewById(R.id.addFood);
        addFood.setOnClickListener(this);
        Button deleteFood = view.findViewById(R.id.deleteFood);
        deleteFood.setOnClickListener(this);

        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.show();

        readDataFromFirebase(new FirebaseCallback() {
            @Override
            public void onCallback() {
                initializeMenuView();
            }
        });

        return view;
    }

    private void readDataFromFirebase(final FirebaseCallback callback) {
        FirebaseDatabaseManager.Instance.getFoodsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot category : dataSnapshot.getChildren()){
                    FoodCategory foodCategory = new FoodCategory(category.getKey());
                    for (DataSnapshot food : category.getChildren()){
                        foodCategory.addFood(food.getValue(String.class));
                    }
                    categories.add(foodCategory);
                }
                loadingDialog.dismiss();
                callback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeMenuView() {
        RecyclerView soupListView = view.findViewById(R.id.soupList);
        RecyclerView meatListView = view.findViewById(R.id.meatList);
        RecyclerView garnishListView = view.findViewById(R.id.garnishList);
        RecyclerView dessertListView = view.findViewById(R.id.dessertList);
        RecyclerView saladListView = view.findViewById(R.id.saladList);

        soupListView.setLayoutManager(new GridLayoutManager(getContext(),4));
        meatListView.setLayoutManager(new GridLayoutManager(getContext(),4));
        garnishListView.setLayoutManager(new GridLayoutManager(getContext(),4));
        dessertListView.setLayoutManager(new GridLayoutManager(getContext(),4));
        saladListView.setLayoutManager(new GridLayoutManager(getContext(),4));

        for (FoodCategory category : categories){
            switch(category.getName()){
                case "Köret":
                    garnishListView.setAdapter(new MenuListAdapter(getContext(), category.getFoods()));
                    break;
                case "Leves":
                    soupListView.setAdapter(new MenuListAdapter(getContext(), category.getFoods()));
                    break;
                case "Sült":
                    meatListView.setAdapter(new MenuListAdapter(getContext(), category.getFoods()));
                    break;
                case "Saláta":
                    saladListView.setAdapter(new MenuListAdapter(getContext(), category.getFoods()));
                    break;
                case "Desszert":
                    dessertListView.setAdapter(new MenuListAdapter(getContext(), category.getFoods()));
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addFood:
                showAddFoodDialog();
                break;
            case R.id.addPhoto:
                showUploadImageDialog();
                break;
            case R.id.uploadFromCamera:
                //request permission to use camera and save image
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    loadImageFromCamera();
                }
                uploadImageDialog.dismiss();
                break;
            case R.id.uploadFromGallery:
                loadImageFromGallery();
                uploadImageDialog.dismiss();
                break;
            case R.id.deleteFood:
                FirebaseDatabaseManager.Instance.deleteFoodsFromDatabase(MenuListAdapter.selectedFoodItems);
                deleteFoods(MenuListAdapter.selectedFoodItems);
        }
    }

    private void deleteFoods(ArrayList<String> selectedFoodItems) {
        for (FoodCategory category : categories){
            for (String food : selectedFoodItems){
                if (category.getFoods().contains(food)){
                    category.removeFood(food);
                    initializeMenuView();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImageFromCamera();
            }
        }
    }

    private void loadImageFromCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        imagePath = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
        startActivityForResult(cameraIntent, 1);
    }

    private void loadImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);

    }

    private void showUploadImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.upload_picture_dialog, null);
        builder.setView(dialogView);
        LinearLayout camera = dialogView.findViewById(R.id.uploadFromCamera);
        camera.setOnClickListener(this);
        LinearLayout gallery = dialogView.findViewById(R.id.uploadFromGallery);
        gallery.setOnClickListener(this);

        uploadImageDialog = builder.create();
        uploadImageDialog.show();
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        TextView imageName = dialogView.findViewById(R.id.imageName);
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK && data != null && data.getData() != null){
                    imagePath = data.getData();

                    //gets name of image
                    Cursor cursor = getActivity().getContentResolver().query(imagePath, null, null, null, null);
                    cursor.moveToFirst();
                    imageName.setText(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
                break;
            case 1:
                if (resultCode == RESULT_OK){
                    Cursor cursor = getActivity().getContentResolver().query(imagePath, new String[]{MediaStore.Images.Media.DATA}, null,null,null);
                    cursor.moveToFirst();
                    imageName.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
                break;
        }
    }

    private void showAddFoodDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_food_dialog, null);
        alertDialog.setView(dialogView);

        Button addPhoto = dialogView.findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(this);
        Spinner foodCategories = dialogView.findViewById(R.id.foodCategories);
        foodCategories.setOnItemSelectedListener(this);

        alertDialog.setTitle(getString(R.string.addFood));
        alertDialog.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (validateInputs()){
                    FirebaseDatabaseManager.Instance.addFood(category, name);
                    FirebaseDatabaseManager.Instance.uploadImage(name,imagePath);
                }
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private boolean validateInputs() {
        TextView foodName = dialogView.findViewById(R.id.foodName);
        TextView imageName = dialogView.findViewById(R.id.imageName);

        //TODO
        if (foodName.getText().toString().isEmpty()) {
            return false;
        } else {
            name = foodName.getText().toString().trim();
        }

        if (category.equals(getString(R.string.chooseCategory))){
            Utilities.displayErrorSnackbar(dialogView,getString(R.string.incorrectCategory));
            return false;
        }

        if (imageName.getText().toString().trim().isEmpty()){
           Utilities.displayErrorSnackbar(dialogView,getString(R.string.photoError));
           return false;
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private interface FirebaseCallback{
        void onCallback();
    }
}
