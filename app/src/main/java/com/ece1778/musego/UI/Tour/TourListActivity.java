package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;

public class TourListActivity extends BaseActivity {

    //recyclerview here, this is just an example

    private Button chooseTourBtn;
    private Button createTourBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_list);

        initView();


    }

    private void initView() {

        chooseTourBtn = (Button) findViewById(R.id.chooseTourBtn);
        createTourBtn = (Button) findViewById(R.id.createTourBtn);

        chooseTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TourListActivity.this, TourDetailActivity.class));
            }
        });

        createTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TourListActivity.this, ArCreateTourActivity.class));
            }
        });
    }
}
