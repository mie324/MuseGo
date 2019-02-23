package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;

public class TourDetailActivity extends BaseActivity {

    private Button startArBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        initView();

    }

    private void initView() {

        startArBtn = (Button) findViewById(R.id.startArBtn);

        startArBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(TourDetailActivity.this, ArShowTourActivity.class));
            }
        });
    }
}
