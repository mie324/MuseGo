package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;



public class ShowInstructionActivity extends AhoyOnboarderActivity implements View.OnClickListener{

    private NodeList nodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        String nodeListJson = getIntent().getStringExtra("nodeList");
        nodeList = new Gson().fromJson(nodeListJson, NodeList.class);
    }

    private void initView() {

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Scan the Dino", "Find a starter image, adjust your camera to scan the starter image.", R.drawable.instruction1);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Follow AR Objects", "Follow the arrow indicators when walking around.", R.drawable.instruction2);
        ahoyOnboarderCard1.setBackgroundColor(R.color.purewhite);
        ahoyOnboarderCard2.setBackgroundColor(R.color.purewhite);

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);

        pages.get(0).setIconLayoutParams(800, 800, 200, 0, 0, 0);
        pages.get(1).setIconLayoutParams(800, 750, 250, 0, 0, 0);


        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.darkGreen);
            page.setDescriptionColor(R.color.black);
            //page.setTitleTextSize(dpToPixels(12, this));
            page.setDescriptionTextSize(dpToPixels(6, this));
        }

        setFinishButtonTitle("Let's begin");
        showNavigationControls(true);
        setImageBackground(R.drawable.bg2);
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.button));


        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        setContentView(R.layout.activity_create_instruction);

        Intent intent = new Intent(ShowInstructionActivity.this, ArShowTourActivity.class);
        intent.putExtra("nodeList",new Gson().toJson(nodeList));
        startActivity(intent);
        finish();

    }
}
