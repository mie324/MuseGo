package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.Loading;


public class CreateInstructionActivity extends BaseActivity implements View.OnClickListener {


    private Button createArBtn;


    private Loading loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_instruction);

        initView();


    }

    private void initView() {


        loading = (Loading) findViewById(R.id.loading);
        loading.hideLoading();

        createArBtn = (Button) findViewById(R.id.createTourBtn);
        createArBtn.setOnClickListener(this);


    }





    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.createTourBtn){

           loading.showLoading();
            startActivity(new Intent(CreateInstructionActivity.this, ArCreateTourActivity.class));


        }


    }

}
