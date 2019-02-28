package com.ece1778.musego.UI.Tour;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.Adapter.TourListAdapter;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TourListActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private TourListAdapter adapter;
    private List<Path> pathList = new ArrayList<>();

    private FloatingActionButton createPathBtn;

    private FirebaseManager firebaseManager;

    private Loading loading;
    private FloatingActionButton createTourBtn;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_list);

        initView();
        initFirebase();
        fetchDataAndRenderView();

    }

    private void initFirebase() {

        firebaseManager = new FirebaseManager(TourListActivity.this);

    }

    private void fetchDataAndRenderView() {

        firebaseManager.getRef()
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Path path = document.toObject(Path.class);

                                        String pid = document.getId();

                                        firebaseManager.getUserRef().document(path.getUserId())
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @SuppressLint("RestrictedApi")
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        User user = documentSnapshot.toObject(User.class);

                                                        Path pathModel = new Path(
                                                                pid,
                                                                path.getUserId(),
                                                                user.getUsername(),
                                                                user.getAvatar(),
                                                                user.getBio(),
                                                                path.getTimestamp(),
                                                                path.getTitle(),
                                                                path.getDescription(),
                                                                path.getFloor(),
                                                                path.getEstimated_time(),
                                                                path.getTags(),
                                                                path.getPrivacy(),
                                                                path.getStart_node(),
                                                                path.getEnd_node(),
                                                                path.getNodes());

                                                        loading.hideLoading();
                                                        createTourBtn.setVisibility(View.VISIBLE);
                                                        adapter.addPath(pathModel);



                                                    }
                                                });
                                                }
                                        }
                            }
                        });

    }



    @SuppressLint("RestrictedApi")
    private void initView() {


        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        layoutManager = new GridLayoutManager(TourListActivity.this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TourListAdapter(TourListActivity.this,pathList);
        recyclerView.setAdapter(adapter);

        loading = (Loading) findViewById(R.id.loading);
        loading.setLoadingText("I want data...");

        createTourBtn = (FloatingActionButton) findViewById(R.id.createTourBtn);
        createTourBtn.setOnClickListener(this);
        createTourBtn.setVisibility(View.GONE);




    }




    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createTourBtn) {

            startActivity(new Intent(TourListActivity.this, CreateInstructionActivity.class));
        }

    }
}
