package com.ece1778.musego.UI.Tour;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Museum.MuseumListActivity;
import com.ece1778.musego.Utils.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.Config;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadTourActivity extends BaseActivity {

    private static final String TAG = "UploadTour";

    private EditText title;
    private EditText desc;
    private TextView cnt;
    private Spinner hour;
    private Spinner minute;
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
    private String instName;

    private Loading loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tour);

        String extra = this.getIntent().getStringExtra("nodeList");
        NodeList nodeList = new Gson().fromJson(extra, NodeList.class);
        startNode = nodeList.getStart_node();
        endNode = nodeList.getEnd_node();
        nodes = nodeList.getNodes();

        instName = this.getIntent().getStringExtra("instName");



        initView();
        initFirebase();

    }

    private void initFirebase() {

        firebaseManager = new FirebaseManager(UploadTourActivity.this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initView() {

        loading = (Loading) findViewById(R.id.loading);
        loading.hideLoading();

        title = findViewById(R.id.uploadTour_title);
        desc = findViewById(R.id.uploadTour_desc);
        tagContent = findViewById(R.id.uploadTour_addContent);
        cnt = findViewById(R.id.uploadTour_cnt);

        hour = findViewById(R.id.uploadTour_hour);
        minute = findViewById(R.id.uploadTour_minute);

        desc.addTextChangedListener(new TextWatcher() {

            int MAX_WORDS = 120;
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());
                if (count == 0 && wordsLength >= MAX_WORDS) {
                    setCharLimit(desc, desc.getText().length());
                } else {
                    removeFilter(desc);
                }
                cnt.setText(String.valueOf(wordsLength) + "/" + MAX_WORDS);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

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
                if(tags.contains(content)){
                    Toast.makeText(UploadTourActivity.this, "TAG already existed!", Toast.LENGTH_SHORT);

                }else{
                    Tag tag = new Tag(content);
                    tag.isDeletable = true;
                    tag.tagTextColor = Color.parseColor("#000000");
                    tag.layoutColor = Color.parseColor("#C4C4C4");
                    tag.deleteIndicatorColor = Color.parseColor("#000000");
                    tagGroup.addTag(tag);
                    tags.add(content);
                    tagContent.setText("");
                }
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
                loading.setLoadingText("Saving the path...");
                loading.showLoading();

                String estimatedTime = timeFormat();
                timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                firebaseManager = new FirebaseManager(UploadTourActivity.this);

                firebaseManager.getUserRef().document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                User user = documentSnapshot.toObject(User.class);

                                Path path = new Path(
                                        currentUser.getUid(),
                                        user.getUsername(),
                                        user.getAvatar(),
                                        user.getBio(),
                                        timestamp,
                                        title.getText().toString(),
                                        desc.getText().toString(),
                                        floor,
                                        estimatedTime,
                                        (List<String>) tags,
                                        privacy,
                                        startNode,
                                        endNode,
                                        nodes
                                );

                                // saving the map
                                firebaseManager.getInstRef()
                                                .document(instName)
                                                .collection("paths")
                                                .add(path)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {

                                                        new Handler().postDelayed(new Runnable(){
                                                    public void run() {
                                                        saveTags();
                                                    }
                                                        }, 2000);


                                                    }
                                                });

                            }
                        });
            }

        });
    }

    private String timeFormat() {

        String hr = hour.getSelectedItem().toString();
        String min = minute.getSelectedItem().toString();

        String h = hr.substring(0,1);
        String m = min.substring(0,2);
        return h.concat("/").concat(m);
    }

    //Save Tags
    private void saveTags(){
        //Save tags
        firebaseManager.getTagRef().document("tagList").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> tagList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            Log.d("！！！！！！", "Task get successfully");
                            tagList = (ArrayList<String>) task.getResult().get("tagList");
                            Log.d("！！！！！", "Taglist first item is"+ (String) tagList.get(0));
                        }

                        for (String tag : tags) {
                            if (!tagList.contains(tag)) {
                                tagList.add(tag);
                            }
                        }

                        Map<String, ArrayList> map = new HashMap<>();
                        map.put("tagList", tagList);

                        firebaseManager.getTagRef().document("tagList").set(map);

                        Intent intent = new Intent(UploadTourActivity.this, TourListActivity.class);
                        intent.putExtra("instName", instName);
                        startActivity(intent);
                        finish();

                    }
                });
    }

    // Words Counter helper
    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length;
    }

    private InputFilter filter;
    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[] { filter });
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }

    }

}
