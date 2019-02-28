package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.Loading;
import com.google.gson.Gson;

public class ShowInstructionActivity extends BaseActivity implements View.OnClickListener{

    private Button createArBtn;


    private Loading loading;
    private NodeList nodeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_instruction);


        initData();
        initView();


    }

    private void initData() {

        String nodeListJson = getIntent().getStringExtra("nodeList");
        nodeList = new Gson().fromJson(nodeListJson, NodeList.class);
    }

    private void initView() {


        loading = (Loading) findViewById(R.id.loading);
        loading.hideLoading();

        createArBtn = (Button) findViewById(R.id.createTourBtn);
        createArBtn.setOnClickListener(ShowInstructionActivity.this);


    }





    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.createTourBtn){

            loading.showLoading();

            Intent intent = new Intent(ShowInstructionActivity.this, ArShowTourActivity.class);
            intent.putExtra("nodeList",new Gson().toJson(nodeList));
            startActivity(intent);


        }


    }
}
