package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;

public class ArShowTourActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ArShowTourActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_show_tour);

        initView();
    }

    private void initView() {


    }

    @Override
    public void onClick(View v) {

    }
}
