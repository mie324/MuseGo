package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ece1778.musego.R;

public class CreateInstructionActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_instruction);

        initView();
    }

    private void initView() {

        findViewById(R.id.createTourBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.createTourBtn){
            startActivity(new Intent(CreateInstructionActivity.this, ArCreateTourActivity.class));
        }

    }
}
