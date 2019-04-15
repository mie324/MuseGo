package com.ece1778.musego.Manager;

import android.content.Context;

import androidx.annotation.NonNull;
import android.util.Log;

import com.ece1778.musego.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager extends BaseActivity {
    private static final String TAG = "FIREBASEMANAGER";

    private Context context;

    private final FirebaseApp app;
    private final CollectionReference pathsRef;
    private final CollectionReference tagsRef;
    private final CollectionReference userRef;
    private final CollectionReference instRef;
    private final CollectionReference commentRef;

//    private final CollectionReference tagsTestRef;


    // Names of the nodes used in the Firebase Database
    public static final String COLLECTION_PATHS = "paths";
    public static final String COLLECTION_TAGS = "tags";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_INST = "institutions";
    public static final String COLLECTION_COMMENT = "comments";
//    public static final String COLLECTION_TAGSTEST = "testsearchtags";


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
           userRef = db.collection(COLLECTION_USERS);
           tagsRef = db.collection(COLLECTION_TAGS);
           instRef = db.collection(COLLECTION_INST);
           commentRef = db.collection(COLLECTION_COMMENT);

       } else {
           Log.d(TAG, "Could not connect to Firebase Firestore!");
           pathsRef = null;
           userRef = null;
           tagsRef = null;
           instRef = null;
           commentRef = null;
//           tagsTestRef = null;
       }
   }

   public CollectionReference getRef() { return pathsRef; }


    public CollectionReference getInstRef() {
        return instRef;
    }

   public CollectionReference getUserRef(){ return userRef; }

   public CollectionReference getTagRef() { return tagsRef; }

   public CollectionReference getCommentRef() { return commentRef; }


    // Add the Path object to collection
//    public void addPath(Path path, final Class nextActivity) {
//        pathsRef.add(path)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "Path written with ID: " + documentReference.getId());
//                        addTag(path.getTags());
//                        Intent intent = new Intent(context, nextActivity);
//                        context.startActivity(intent);
//                        finish();
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//    }

    //Add the Tag object to collection
    public void addTag(List<String> tags) {
        tagsRef.document("tagList").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> tagList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Task get successfully");
                            tagList = (ArrayList<String>) task.getResult().get("tagList");
                            Log.d(TAG, "Taglist first item is"+ (String) tagList.get(0));

                        }
                        for (String tag : tags) {
                            if (!tagList.contains(tag)) {
                                tagList.add(tag);
                            }
                        }

                        Map<String, ArrayList> map = new HashMap<>();
                        map.put("tagList", tagList);
                        tagsRef.document("tagList").set(map);

                    }
                });
    }


}
