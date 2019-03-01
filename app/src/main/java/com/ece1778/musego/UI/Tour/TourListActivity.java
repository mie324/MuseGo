package com.ece1778.musego.UI.Tour;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.ece1778.musego.Adapter.TourListAdapter;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Search.SearchFabFragment;
import com.ece1778.musego.Utils.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TourListActivity extends BaseActivity implements View.OnClickListener, AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private TourListAdapter adapter;
    private List<Path> pathList = new ArrayList<>();

    private FloatingActionButton createPathBtn, fab2;
    private SearchFabFragment dialogFrag;


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

    @Override
    public void onResult(Object result) {

    }

    private void initFirebase() {

        firebaseManager = new FirebaseManager(TourListActivity.this);

    }

    private void fetchDataAndRenderView() {

        firebaseManager.getRef()
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    //loading.hideLoading();


                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Path path = document.toObject(Path.class);
                                        String pid = document.getId();


                                        Path pathModel = new Path(
                                                pid,
                                                path.getUserId(),
                                                path.getUsername(),
                                                path.getUserAvatar(),
                                                path.getUserBio(),
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

                                        //pathList.add(path);
                                        adapter.addPath(pathModel);
                                    }

                                    createTourBtn.setVisibility(View.VISIBLE);
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
//
//        loading = (Loading) findViewById(R.id.loading);
//        loading.setLoadingText("I want data...");

        createTourBtn = (FloatingActionButton) findViewById(R.id.createTourBtn);
        createTourBtn.setOnClickListener(this);
        createTourBtn.setVisibility(View.GONE);

        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        dialogFrag = SearchFabFragment.newInstance();
        dialogFrag.setParentFab(fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createTourBtn) {

            startActivity(new Intent(TourListActivity.this, CreateInstructionActivity.class));
        }

    }

    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");

    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");

    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");

    }


}
