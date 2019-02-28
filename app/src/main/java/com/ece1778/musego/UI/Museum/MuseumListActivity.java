package com.ece1778.musego.UI.Museum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Tour.TourListActivity;


public class MuseumListActivity extends BaseActivity {
    //recyclerview here, this is just an example

    private Button chooseMuseumBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_list);

        initView();


    }

    private void initView() {

        chooseMuseumBtn = (Button) findViewById(R.id.chooseMuseumBtn);

        chooseMuseumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                startActivity(new Intent(MuseumListActivity.this, TourListActivity.class));


            }
        });
    }


}
