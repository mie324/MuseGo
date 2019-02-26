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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       firebaseManager = new FirebaseManager(MainActivity.this);

       List<Node> nodes = new ArrayList<>();
       nodes.add(new Node(new Translation(1,1,1),new Rotation(1,1,1,1),1)
               );
       nodes.add(new Node(new Translation(1,1,1),new Rotation(1,1,1,1),1)
        );

       Path path = new Path(
               "userId",
               "timestamp",
               "title",
               "description",
               "floor",
               "time",
               Arrays.asList("aa","bb"),
               "private",
               new Node(new Translation(1,1,1),new Rotation(1,1,1,1),1),
               new Node(new Translation(1,1,1),new Rotation(1,1,1,1),1),
                nodes
               );

       firebaseManager.addPath(path, TourListActivity.class);

    }
}
