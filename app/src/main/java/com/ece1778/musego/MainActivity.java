package com.ece1778.musego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


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
