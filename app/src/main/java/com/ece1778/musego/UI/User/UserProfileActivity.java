package com.ece1778.musego.UI.User;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.ece1778.musego.Adapter.TourListAdapter;
import com.ece1778.musego.Adapter.UserProfileAdapter;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.ExpandableGroupEntity;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Tour.TourListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity{

    private static final int CREATED = 1;
    private static final int LIKED = 2;
    private static final String TAG = "PROFILE";

    private Toolbar toolbar;

    private RecyclerView recyclerView;
    TourListAdapter adapter;

    private FirebaseManager firebaseManager;
    private String uid;
    private User user;
    private ArrayList<ExpandableGroupEntity> expandableGroups = new ArrayList<>();

    private ImageView userAvatar;
    private TextView username;
    private TextView bio;
    private Button created;
    private Button liked;

    private ArrayList<Path> createdPath = new ArrayList<>();
    private ArrayList<Path> likedPath = new ArrayList<>();
    private int selected = CREATED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initFirebase();
        initView();
        initData();

        created = findViewById(R.id.profile_created);
        created.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.white));
        created.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = CREATED;
                created.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.white));
                liked.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.bg));
                initRecycler();
            }
        });

        liked = findViewById(R.id.profile_liked);
        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = LIKED;
                liked.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.white));
                created.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.bg));
                initRecycler();
            }
        });
    }

    private void initRecycler() {
        if(selected == CREATED){
            adapter = new TourListAdapter(UserProfileActivity.this, createdPath, "osc",user);

        }else if(selected == LIKED){
            adapter = new TourListAdapter(UserProfileActivity.this, likedPath, "osc",user);
        }
        recyclerView.setAdapter(adapter);
    }

    private void initFirebase() {

        firebaseManager = new FirebaseManager(this);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAvatar = (ImageView) findViewById(R.id.userAvatar);
        username = (TextView) findViewById(R.id.username);
        bio = (TextView) findViewById(R.id.bio);

    }


    private void initData() {

        String userJson = getIntent().getStringExtra("user");

        user = new Gson().fromJson(userJson,User.class);


        //avatar
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        Glide.with(this)
                .load(user.getAvatar())
                .apply(options)
                .into(userAvatar);

        username.setText(user.getUsername());
        bio.setText(user.getBio());

        fetchPath();

    }

    private void fetchPath() {
        firebaseManager.getInstRef()
                .document("osc")
                .collection("paths")
                .whereEqualTo("userId", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for(QueryDocumentSnapshot document: task.getResult()){
                                Path path = document.toObject(Path.class);

                                createdPath.add(path);
                            }

                            //init recyclerView
                            adapter = new TourListAdapter(UserProfileActivity.this, createdPath, "osc",user);
                            recyclerView.setAdapter(adapter);

                            firebaseManager.getInstRef()
                                    .document("osc")
                                    .collection("paths")
                                    .whereArrayContains("likeList",uid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Path path = document.toObject(Path.class);

                                                    likedPath.add(path);
                                                }

                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
