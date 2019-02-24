package com.ece1778.musego.UI.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Museum.MuseumListActivity;

public class SiginupActivity extends BaseActivity {

    private Button signupBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();


    }

    private void initView() {

        signupBtn = (Button) findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SiginupActivity.this, MuseumListActivity.class));
            }
        });
    }
}
