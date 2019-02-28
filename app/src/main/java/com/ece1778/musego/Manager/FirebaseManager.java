package com.ece1778.musego.Manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.Path;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirebaseManager extends BaseActivity {
    private static final String TAG = "FIREBASEMANAGER";

    private Context context;

    private final FirebaseApp app;
    private final CollectionReference pathsRef;
    private final CollectionReference tagsRef;

    // Names of the nodes used in the Firebase Database
    public static final String COLLECTION_PATHS = "paths";
    public static final String COLLECTION_TAGS = "tags";

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
            tagsRef = db.collection(COLLECTION_TAGS);
        } else {
            Log.d(TAG, "Could not connect to Firebase Firestore!");
            pathsRef = null;
            tagsRef = null;
        }
    }


    public CollectionReference getRef() {
        return pathsRef;
    }


    // Add the Path object to collection
    public void addPath(Path path, final Class nextActivity) {
        pathsRef.add(path)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Path written with ID: " + documentReference.getId());
                        addTag(path.getTags(), documentReference.getId());
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

    //Add the Tag object to collection
    public void addTag(List<String> tags, String pID) {
        for (String tag : tags) {

        }
    }


}
