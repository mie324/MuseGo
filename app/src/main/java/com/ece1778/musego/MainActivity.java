package com.ece1778.musego;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.UI.Auth.SigninActivity;
import com.ece1778.musego.UI.Tour.TourListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       db = FirebaseFirestore.getInstance();

       List<String> tagList = new ArrayList<>();
       tagList.add("ee");
       tagList.add("zz");
       tagList.add("121");
       tagList.add("121");

       for(String tag:tagList){

           Log.d("!!!this term",tag);

           db.collection("tags")
                   .whereEqualTo("tagName",tag)
                   .get()
                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               for (QueryDocumentSnapshot document : task.getResult()) {
                                   Log.d("!!!!!exist",tag);
                               }
                           } else {

                               Log.d("!!!!!not exist", tag);

                           }
                       }
                   });



       }




    }
}
