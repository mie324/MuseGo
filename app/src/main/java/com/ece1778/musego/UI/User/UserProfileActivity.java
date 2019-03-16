package com.ece1778.musego.UI.User;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;


    private RecyclerView recyclerView;
    UserProfileAdapter adapter;

    private FirebaseManager firebaseManager;
    private String uid;
    private User user;
    private ArrayList<ExpandableGroupEntity> expandableGroups = new ArrayList<>();

    private ImageView userAvatar;
    private TextView username;
    private TextView bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        initFirebase();
        initView();
        initData();
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


        firebaseManager.getInstRef()
                .document("osc")
                .collection("paths")
                .whereEqualTo("userId", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            ArrayList<Path> children = new ArrayList<>();

                            for(QueryDocumentSnapshot document: task.getResult()){
                                Path path = document.toObject(Path.class);

                                children.add(path);
                            }

                            expandableGroups.add(new ExpandableGroupEntity("Created By You", false, children));


                            firebaseManager.getInstRef()
                                    .document("osc")
                                    .collection("paths")
                                    .whereArrayContains("likeList",uid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                   if (task.isSuccessful()) {

                                                                       ArrayList<Path> children = new ArrayList<>();

                                                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                                                           Path path = document.toObject(Path.class);

                                                                           children.add(path);
                                                                       }

                                                                       expandableGroups.add(new ExpandableGroupEntity("Liked By You", false, children));

                                                                       setAdapter(expandableGroups);

                                                                   }
                                                               }
                                                           });




                        }
                    }
                });

    }

    private void setAdapter(ArrayList<ExpandableGroupEntity> expandableGroups) {

        adapter = new UserProfileAdapter(this, expandableGroups);
        adapter.setOnHeaderClickListener(new GroupedRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {

                UserProfileAdapter expandableAdapter = (UserProfileAdapter) adapter;
                if (expandableAdapter.isExpand(groupPosition)) {
                    expandableAdapter.collapseGroup(groupPosition);
                } else {
                    expandableAdapter.expandGroup(groupPosition);
                }
            }
        });

        adapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition, int childPosition) {

                Toast.makeText(UserProfileActivity.this, "子项：groupPosition = " + groupPosition
                                + ", childPosition = " + childPosition,
                        Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(adapter);

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
