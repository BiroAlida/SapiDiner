package com.example.sapidiner.Database;

import android.net.Uri;

import com.example.sapidiner.Classes.Orders;
import com.example.sapidiner.Classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        public static FirebaseDatabase getDatabase() {
            return database;
        }

        public static DatabaseReference getUsersReference() {
            return usersReference;
        }
        public static DatabaseReference getFoodsReference() {
            return foodsReference;
        }

        public static void addFood(String category, String name){
            String key = foodsReference.push().getKey();
            foodsReference.child(category).child(key).setValue(name);
        }

        public static void uploadImage(String name, Uri imagePath){
            storageReference.child(name).putFile(imagePath);
        }

    }
}