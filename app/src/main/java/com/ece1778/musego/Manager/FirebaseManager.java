package com.ece1778.musego.Manager;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
    private static final String TAG = 

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


   FirebaseManager(Context context){
       app = FirebaseApp.initializeApp(context);
       if(app != null){
           FirebaseFirestore db = FirebaseFirestore.getInstance();
           pathsRef = db.collection(COLLECTION_PATHS);
       }
       else{
           Log.d()
       }



   }



}
