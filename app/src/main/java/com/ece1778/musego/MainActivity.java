package com.ece1778.musego;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.UI.Auth.SigninActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Class a = SigninActivity.class;

       firebaseManager = new FirebaseManager(MainActivity.this);

        Translation t = firebaseManager.getPathT("BfVa8wZ3SfGwbeqXv7oQ");

        if(t != null){
            Log.d("!!!!!!!!!!", t.toString());
        }





    }
}
