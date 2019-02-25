package com.ece1778.musego.UI.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Museum.MuseumListActivity;

public class SigninActivity extends BaseActivity {


    private Button signinBtn;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);



        initView();




    }

    private void initView() {

        signinBtn = (Button) findViewById(R.id.signinBtn);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SigninActivity.this, MuseumListActivity.class));
            }
        });
    }
}
