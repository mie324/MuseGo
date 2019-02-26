package com.ece1778.musego.UI.Tour;

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
import com.ece1778.musego.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TourListActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private TourListAdapter adapter;
    private List<Path> pathList;

    private FloatingActionButton createPathBtn;

    private FirebaseManager firebaseManager;



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

        pathList = new ArrayList<>();

        firebaseManager.getRef()
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Path path = document.toObject(Path.class);

                                        Path pathModel = new Path(
                                                document.getId(),
                                                path.getUserId(),
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

                                        pathList.add(pathModel);

                                    }


                                    adapter = new TourListAdapter(TourListActivity.this, pathList);
                                    recyclerView.setAdapter(adapter);
                                }

                            }
                        });

    }



    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        layoutManager = new GridLayoutManager(TourListActivity.this,1);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.createTourBtn).setOnClickListener(this);


    }




    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createTourBtn) {
            Log.d("！！！！！！CLick","dfdfd");
            startActivity(new Intent(TourListActivity.this, ArCreateTourActivity.class));
        }

    }
}
