package com.ece1778.musego.UI.Museum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ece1778.musego.Adapter.MuseumListAdapter;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Tour.TourListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MuseumListActivity extends BaseActivity implements View.OnClickListener {
    //recyclerview here, this is just an example

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MuseumListAdapter adapter;

    private List<String> museumList = new ArrayList<>(Arrays.asList("osc","rom"));


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_list);

        initView();


    }


    private void initView() {

        Log.d("!!!!!Recycler","dfd");

        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        layoutManager = new GridLayoutManager(MuseumListActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MuseumListAdapter(MuseumListActivity.this, museumList);
        recyclerView.setAdapter(adapter);


    }


    @Override
    public void onClick(View v) {

    }
}
