package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.Loading;
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

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Step One", "Stand in front of the image and click the button.", R.drawable.instruction1);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Step Two", "Adjust camera until detect the image successfully.", R.drawable.instruction4);
        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);

        pages.get(0).setIconLayoutParams(950, 800, 300, 0, 0, 20);
        pages.get(1).setIconLayoutParams(1200, 1000, 200, 0, 0, 20);


        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.black);
            page.setDescriptionColor(R.color.grey_600);
            //page.setTitleTextSize(dpToPixels(12, this));
            //page.setDescriptionTextSize(dpToPixels(8, this));
        }

        setFinishButtonTitle("Let's begin");
        showNavigationControls(true);
        setColorBackground(R.color.green);

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        setContentView(R.layout.activity_create_instruction);

        Intent intent = new Intent(ShowInstructionActivity.this, ArShowTourActivity.class);
        intent.putExtra("nodeList",new Gson().toJson(nodeList));
        startActivity(intent);

    }
}
