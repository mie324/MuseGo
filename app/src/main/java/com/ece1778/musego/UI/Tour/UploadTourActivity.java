package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;

public class UploadTourActivity extends BaseActivity {

    private Button uploadTourBtn;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tour);

        initView();
    }

    private void initView() {

        uploadTourBtn = (Button) findViewById(R.id.uploadTourBtn);

        uploadTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadTourActivity.this, TourDetailActivity.class));
            }
        });
    }
}
