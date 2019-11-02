package com.example.sapidiner;

import com.example.sapidiner.Classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseManager {

    public FirebaseDatabaseManager() {
    }

    public static class Instance {
        static FirebaseDatabase database = FirebaseDatabase.getInstance();
        static DatabaseReference usersReference = database.getReference("Users");
        static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //TODO other references

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
    }
}
