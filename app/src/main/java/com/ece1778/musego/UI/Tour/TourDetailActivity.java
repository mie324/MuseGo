package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.R;
import com.google.gson.Gson;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TourDetailActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, View.OnClickListener {

    private static final String TAG = "TOURDETAIL";

    private TextView title;
    private ImageView avatar;
    private SliderLayout img;
    private TagView tagView;
    private TextView username;
    private TextView timestamp;
    private TextView desc;
    private List<String> imageList = new ArrayList<>();

    private Path path;
    private NodeList nodeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);


        initView();
        initData();

    }

    private void initData() {

        String pathJson = getIntent().getStringExtra("path");

        path = new Gson().fromJson(pathJson,Path.class);
        imageList = path.getImgList();
        title.setText(path.getTitle());
        username.setText("By ".concat(path.getUsername()));
        timestamp.setText("Last Updated "+timeFormat(path.getTimestamp()));
        desc.setText(path.getDescription());

        for(String content: path.getTags()){
            Tag tag = new Tag(content);
            tag.tagTextColor = Color.parseColor("#000000");
            tag.layoutColor = Color.parseColor("#C4C4C4");
            tagView.addTag(tag);
        }

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        Glide.with(this)
                .load(Uri.parse(path.getUserAvatar()))
                .apply(options)
                .into(avatar);

        nodeList = new NodeList(path.getStart_node(),path.getEnd_node(),path.getNodes());

        initImgView();
    }

    private void initView() {

        title = findViewById(R.id.detail_title);
        tagView = findViewById(R.id.detail_tags);
        username = findViewById(R.id.detail_user);
        timestamp = findViewById(R.id.detail_timestamp);
        desc = findViewById(R.id.detail_desc);
        avatar = findViewById(R.id.detail_avatar);

        findViewById(R.id.startArBtn).setOnClickListener(this);

        img = (SliderLayout)findViewById(R.id.detail_img);


    }

    private void initImgView() {



        if(imageList == null || imageList.size() == 0) {
            TextSliderView textSliderView = new TextSliderView(this);

            // initialize a SliderLayout
            textSliderView
                    .description("Indicator")
                    .image(R.drawable.scanme)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);


            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", "Indicator");
            img.addSlider(textSliderView);
        }
        else{
            for(int i = 0; i < imageList.size(); i++){
                TextSliderView textSliderView = new TextSliderView(this);

                // initialize a SliderLayout
                textSliderView
                        .description("Image "+i)
                        .image(imageList.get(i))
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(this);


                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", "Image "+i);
                img.addSlider(textSliderView);

            }
        }


        img.setPresetTransformer(SliderLayout.Transformer.Default);
        img.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        img.setCustomAnimation(new DescriptionAnimation());
        img.stopAutoCycle();
        img.addOnPageChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.startArBtn) {
            Intent intent = new Intent(TourDetailActivity.this, ShowInstructionActivity.class);
            intent.putExtra("nodeList", new Gson().toJson(nodeList));
            startActivity(intent);
        }

    }

    private String timeFormat(String description) {
        String year = description.substring(0,4);
        String month = description.substring(4,6);
        String day = description.substring(6,8);
        String hour = description.substring(9,11);
        String minute = description.substring(11,13);
        String second = description.substring(13,15);

        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
