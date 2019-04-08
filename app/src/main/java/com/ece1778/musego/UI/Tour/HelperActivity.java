package com.ece1778.musego.UI.Tour;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;

public class HelperActivity extends BaseActivity {

    Button infoDismiss;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview_helper);

        infoDismiss = findViewById(R.id.info_dismiss);
        infoDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}
