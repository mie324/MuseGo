package com.ece1778.musego.Manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager extends BaseActivity {
    private static final String TAG = "FIREBASEMANAGER";

    private Context context;

    private final FirebaseApp app;
    private final CollectionReference pathsRef;

    // Names of the nodes used in the Firebase Database
    public static final String COLLECTION_PATHS = "paths";

    // Some common keys and values used when writing to the Firebase Database.
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_FLOOR_NUM = "floor";
    public static final String KEY_ESTIMATED_TIME = "estimated_time";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_PRIVACY = "privacy";
    public static final String KEY_START_NODE = "start_node";
    public static final String KEY_END_NODE = "end_node";
    public static final String KEY_NODES_LIST = "nodes";


   public FirebaseManager(Context context) {
       this.context = context;
       app = FirebaseApp.initializeApp(context);
       if (app != null) {
           FirebaseFirestore db = FirebaseFirestore.getInstance();
           pathsRef = db.collection(COLLECTION_PATHS);
       } else {
           Log.d(TAG, "Could not connect to Firebase Firestore!");
           pathsRef = null;
       }
   }


   public CollectionReference getRef(){
       return pathsRef;
   }


   // Add the Path object to collection
    public void addPath(Path path, final Class nextActivity){
        pathsRef.add(path)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Path written with ID: " + documentReference.getId());
                        Intent intent = new Intent(context, nextActivity);
                        context.startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

   }

   // Get the path by pid

    public Path getPath(String pid){

       final Path[] path = {null};
       pathsRef.document(pid)
               .get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       path[0] = documentSnapshot.toObject(Path.class);

                   }
               });

       return path[0];
    }

//    public Translation getPathT(String pid){
//
//        final Translation[] t = {null};
//        final Task<DocumentSnapshot> documentSnapshotTask = pathsRef.document(pid)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        t[0] = documentSnapshot.toObject(Translation.class);
//                        Log.d("!!!!!!!!!!!hhhhhh",t[0].getTx()+"");
//
//                    }
//                });
//
//
//        return t[0];
//    }







}
