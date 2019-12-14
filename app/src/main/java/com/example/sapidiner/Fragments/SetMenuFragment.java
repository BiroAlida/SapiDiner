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
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapidiner.Activities.OrderActivity;
import com.example.sapidiner.Adapters.MenuListAdapter;
import com.example.sapidiner.Classes.Food;
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
    private View dialogView;
    private Uri imagePath;
    private String name, category, price;
    private AlertDialog uploadImageDialog;
    private ArrayList<Food> foodList = new ArrayList<>();
    private ProgressDialog loadingDialog;
    private RecyclerView.Adapter adapter;

    public SetMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_set_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addFood = view.findViewById(R.id.addFood);
        addFood.setOnClickListener(this);
        Button deleteFood = view.findViewById(R.id.deleteFood);
        deleteFood.setOnClickListener(this);
        Button viewOrders = view.findViewById(R.id.viewOrders);
        viewOrders.setOnClickListener(this);
        adapter = new MenuListAdapter(getContext(), foodList);

        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.show();

        readDataFromFirebase(() -> {
            initializeMenuView();
            loadingDialog.dismiss();
        });
    }

    private void readDataFromFirebase(final FirebaseCallback callback) {
        FirebaseDatabaseManager.Instance.getFoodsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodList.clear();
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
        foodListView.setLayoutManager(new GridLayoutManager(getContext(),3));
        foodListView.setAdapter(adapter);
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
                    //if permission is already granted
                    loadImageFromCamera();
                }
                uploadImageDialog.dismiss();
                break;
            case R.id.uploadFromGallery:
                loadImageFromGallery();
                uploadImageDialog.dismiss();
                break;
            case R.id.deleteFood:
                deleteFoods(MenuListAdapter.selectedFoodItems);
                break;
            case R.id.viewOrders:
                startActivity(new Intent(getActivity(),OrderActivity.class));
                break;
        }
    }

    private void deleteFoods(ArrayList<Food> selectedFoodItems) {
        foodList.removeAll(selectedFoodItems);
        adapter.notifyDataSetChanged();
        FirebaseDatabaseManager.Instance.deleteFoodsFromDatabase(selectedFoodItems);
        FirebaseDatabaseManager.Instance.deleteFoodFromStorage(selectedFoodItems);
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
                //loading image from gallery
                if (resultCode == RESULT_OK && data != null && data.getData() != null){
                    imagePath = data.getData();

                    //gets name of image
                    Cursor cursor = getActivity().getContentResolver().query(imagePath, null, null, null, null);
                    cursor.moveToFirst();
                    imageName.setText(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
                break;
            case 1:
                //loading image from camera
                if (resultCode == RESULT_OK){
                    Cursor cursor = getActivity().getContentResolver().query(imagePath, new String[]{MediaStore.Images.Media.DATA}, null,null,null);
                    cursor.moveToFirst();
                    imageName.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
                break;
        }
    }

    private void showAddFoodDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_food_dialog, null);
        alertDialog.setView(dialogView);

        Button addPhoto = dialogView.findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(this);
        Spinner foodCategories = dialogView.findViewById(R.id.foodCategories);
        foodCategories.setOnItemSelectedListener(this);

        alertDialog.setTitle(getString(R.string.addFood));
        alertDialog.setPositiveButton(getString(R.string.send), (dialogInterface, i) -> {
            if (validateInputs()){
                FirebaseDatabaseManager.Instance.uploadImage(name,imagePath);
                FirebaseDatabaseManager.Instance.addFood(new Food(name, Integer.parseInt(price),category));
                foodList.clear();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    private boolean validateInputs() {
        TextView foodName = dialogView.findViewById(R.id.foodName);
        TextView foodPrice = dialogView.findViewById(R.id.price);
        TextView imageName = dialogView.findViewById(R.id.imageName);

        name = foodName.getText().toString().trim();
        price = foodPrice.getText().toString().trim();

        if (name.isEmpty()) {
            Utilities.displayErrorSnackbar(dialogView,getString(R.string.nameInputError));
            return false;
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
