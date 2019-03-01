
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

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Step One", "Stand in front of the image and click the button towards the image.", R.drawable.instruction1);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Step Two", "Adjust camera until detect successfully.", R.drawable.introduction2);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Step Three", "Place indicator by clicking on screen and place End Node at the end of tour.", R.drawable.introduction3);

        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);
        ahoyOnboarderCard3.setBackgroundColor(R.color.white);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);

        pages.get(0).setIconLayoutParams(950, 800, 300, 0, 0, 20);
        pages.get(1).setIconLayoutParams(1200, 1000, 200, 0, 0, 20);
        pages.get(2).setIconLayoutParams(1200, 1000, 200, 0, 0, 20);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.black);
            page.setDescriptionColor(R.color.grey_600);
            //page.setTitleTextSize(dpToPixels(12, this));
            //page.setDescriptionTextSize(dpToPixels(8, this));
        }

        setFinishButtonTitle("Let's begin");

        showNavigationControls(true);
        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.green);
        colorList.add(R.color.blue);
        colorList.add(R.color.grey_600);
        setColorBackground(colorList);

        setOnboardPages(pages);
    }


    @Override
    public void onFinishButtonPressed() {
        setContentView(R.layout.activity_create_instruction);
        startActivity(new Intent(CreateInstructionActivity.this, ArCreateTourActivity.class));
    }


}
