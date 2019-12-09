package com.example.sapidiner.Database;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sapidiner.Classes.Food;
import com.example.sapidiner.Classes.Orders;
import com.example.sapidiner.Classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseDatabaseManager {

    public FirebaseDatabaseManager() {
    }

    public static class Instance {
        static FirebaseDatabase database = FirebaseDatabase.getInstance();
        static DatabaseReference usersReference = database.getReference("Users");
        static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        static DatabaseReference foodsReference = database.getReference("Foods");
        static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        static DatabaseReference ordersReference = database.getReference("Orders");


        public static void addNewOrder(String key, Orders order) {
            ordersReference.child(key).setValue(order);
        }


        public static void addNewUser(String key,User user) {
            usersReference.child(key).setValue(user);
        }

        public static FirebaseAuth getFirebaseAuth() {
            return firebaseAuth;
        }

        public static StorageReference getStorageReference() {
            return storageReference;
        }

        public static FirebaseDatabase getDatabase() {
            return database;
        }

        public static DatabaseReference getUsersReference() {
            return usersReference;
        }
        public static DatabaseReference getFoodsReference() {
            return foodsReference;
        }

        public static void addFood(Food food){
            String key = foodsReference.push().getKey();
            foodsReference.child(key).setValue(food);
        }

        public static void uploadImage(String name, Uri imagePath){
            storageReference.child(name).putFile(imagePath);
        }

        public static void deleteFoodsFromDatabase(final ArrayList<Food> foodList){
            foodsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot foodData : dataSnapshot.getChildren()){
                        Food food = foodData.getValue(Food.class);
                        if (foodList.contains(food)){
                            Log.d("foodD",food.getName());
                            foodsReference.child(foodData.getKey()).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
