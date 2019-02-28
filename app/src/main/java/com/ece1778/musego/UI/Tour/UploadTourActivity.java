package com.ece1778.musego.UI.Tour;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.MainActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UploadTourActivity extends BaseActivity {

    private static final String TAG = "UploadTour";

    private EditText title;
    private EditText desc;
    private EditText time;
    private RadioGroup floorRadioGroup;
    private RadioGroup privacyRadioGroup;
    private EditText tagContent;
    private Button addTag;
    private Button uploadTourBtn;

    private FirebaseManager firebaseManager;
    private FirebaseUser currentUser;

    private String pId;
    private String userId;
    private String timestamp;
    private String floor;
    private ArrayList<String> tags = new ArrayList<>();
    private String privacy;
    private Node startNode;
    private Node endNode;
    private List<Node> nodes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tour);



        initView();
        initFirebase();

        String extra = this.getIntent().getStringExtra("nodeList");
        NodeList nodeList = new Gson().fromJson(extra, NodeList.class);
        startNode = nodeList.getStart_node();
        endNode = nodeList.getEnd_node();
        nodes = nodeList.getNodes();

    }

    private void initFirebase() {

        firebaseManager = new FirebaseManager(UploadTourActivity.this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void initView() {

        title = findViewById(R.id.uploadTour_title);
        desc = findViewById(R.id.uploadTour_desc);
        time = findViewById(R.id.uploadTour_time);
        tagContent = findViewById(R.id.uploadTour_addContent);

        TagView tagGroup = (TagView) findViewById(R.id.tag_group);
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadTourActivity.this);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        Toast.makeText(UploadTourActivity.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                tags.remove(position);

            }
        });

        addTag = findViewById(R.id.uploadTour_addTag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = tagContent.getText().toString();
                Tag tag = new Tag(content);
                tag.isDeletable = true;
                tag.tagTextColor = Color.parseColor("#000000");
                tag.layoutColor = Color.parseColor("#C4C4C4");
                tag.deleteIndicatorColor = Color.parseColor("#000000");
                tagGroup.addTag(tag);
                tags.add(content);
                tagContent.setText("");
            }
        });

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
                firebaseManager = new FirebaseManager(UploadTourActivity.this);

                Path path = new Path(
                        currentUser.getUid(),
                        timestamp,
                        title.getText().toString(),
                        desc.getText().toString(),
                        floor,
                        time.getText().toString(),
                        (List<String>) tags,
                        privacy,
                        startNode,
                        endNode,
                        nodes
                );

                firebaseManager.addPath(path, TourListActivity.class);
                finish();
            }
        });
    }
}
