
package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.ece1778.musego.R;

import java.util.ArrayList;
import java.util.List;

public class CreateInstructionActivity extends AhoyOnboarderActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Scan the Dino", "Find a starter image, adjust your camera to scan the starter image.", R.drawable.instruction1);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Place AR Objects", "As you walk around, click the screen to place directional arrows or accessibility icons.", R.drawable.instruction2);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Place the End Object to Finish", "To complete your tour, click Finish to place the EndNode.", R.drawable.instruction3);

        ahoyOnboarderCard1.setBackgroundColor(R.color.purewhite);
        ahoyOnboarderCard2.setBackgroundColor(R.color.purewhite);
        ahoyOnboarderCard3.setBackgroundColor(R.color.purewhite);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);

        pages.get(0).setIconLayoutParams(800, 800, 200, 0, 0, 0);
        pages.get(1).setIconLayoutParams(800, 750, 250, 0, 0, 0);
        pages.get(2).setIconLayoutParams(800, 750, 250, 0, 0, 0);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.darkGreen);
            page.setDescriptionColor(R.color.black);
            //page.setTitleTextSize(dpToPixels(12, this));
            page.setDescriptionTextSize(dpToPixels(4, this));
        }

        setFinishButtonTitle("Let's begin");
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.button));


        showNavigationControls(true);
        setImageBackground(R.drawable.bg2);

        setOnboardPages(pages);
    }


    @Override
    public void onFinishButtonPressed() {
        setContentView(R.layout.activity_create_instruction);

        Intent intent = new Intent(CreateInstructionActivity.this, ArCreateTourActivity.class);
        intent.putExtra("instName", getIntent().getStringExtra("instName"));
        startActivity(intent);
        finish();
    }


}
