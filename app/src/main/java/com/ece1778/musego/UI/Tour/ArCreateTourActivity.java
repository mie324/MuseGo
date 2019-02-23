package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;

public class ArCreateTourActivity extends BaseActivity {

    private Button finishArBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_create_tour);


        initView();


    }

    private void initView() {

        finishArBtn = (Button) findViewById(R.id.finishArBtn);

        finishArBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArCreateTourActivity.this, UploadTourActivity.class));
            }
        });
    }
}


