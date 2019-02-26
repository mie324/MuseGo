package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.MainActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UploadTourActivity extends BaseActivity {

    private static final String TAG = "UploadTour";

    private Node startNode;
    private Node endNode;
    private List<Node> nodes;

    private EditText title;
    private EditText desc;
    private EditText time;
    private RadioGroup floorRadioGroup;
    private RadioGroup privacyRadioGroup;
    private Button uploadTourBtn;

    private FirebaseManager firebaseManager;


    private String pId;
    private String userId;
    private String timestamp;
    private String floor;
    private List<String> tags;
    private String privacy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tour);

        initView();

        String extra = this.getIntent().getStringExtra("nodeList");
        NodeList nodeList = new Gson().fromJson(extra,NodeList.class);
        startNode = nodeList.getStart_node();
        endNode  =nodeList.getEnd_node();
        nodes = nodeList.getNodes();

    }

    private void initView() {

        title = findViewById(R.id.uploadTour_title);
        desc = findViewById(R.id.uploadTour_desc);
        time = findViewById(R.id.uploadTour_time);

        floorRadioGroup = findViewById(R.id.uploadTour_floorGroup);
        floorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                floor = radbtn.getText().toString();
            }
        });

        privacyRadioGroup = findViewById(R.id.uploadTour_privacyGroup);
        privacyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                privacy = radbtn.getText().toString();
            }
        });

        uploadTourBtn = (Button) findViewById(R.id.uploadTourBtn);
        uploadTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                startActivity(new Intent(UploadTourActivity.this, TourDetailActivity.class));

                firebaseManager = new FirebaseManager(UploadTourActivity.this);

                Path path = new Path(
                        "userId",
                        timestamp,
                        title.getText().toString(),
                        desc.getText().toString(),
                        floor,
                        time.getText().toString(),
                        Arrays.asList("aa","bb"),
                        privacy,
                        startNode,
                        endNode,
                        nodes
                );

                firebaseManager.addPath(path, TourListActivity.class);
            }
        });
    }
}
