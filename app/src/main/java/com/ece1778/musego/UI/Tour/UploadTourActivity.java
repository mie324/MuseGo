package com.ece1778.musego.UI.Tour;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.Utils.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class UploadTourActivity extends BaseActivity {

    private static final String TAG = "UploadTour";
    private static final int REQUEST_GALLERY = 1;

    private EditText title;
    private EditText desc;
    private TextView cnt;
    private Spinner hour;
    private Spinner minute;
    private RadioGroup floorRadioGroup;
    private EditText tagContent;
    private Button addTag;
    private GridLayout imgGroup;
    private Button addImg;
    private Button uploadTourBtn;

    private FirebaseManager firebaseManager;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;

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
    private List<String> imgList = new ArrayList<>();
    private List<String> imgUriList = new ArrayList<>();
    private ArrayList<String> sensorList = new ArrayList<>();

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

        String sensor = this.getIntent().getStringExtra("sensor");
        sensorList = new Gson().fromJson(sensor, ArrayList.class);

        initView();
        initFirebase();

    }

    private void initFirebase() {

        firebaseManager = new FirebaseManager(UploadTourActivity.this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
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

            int MAX_WORDS = 200;
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
        for (String sensor : sensorList) {
            Log.d(TAG, "sensor tag is "+ sensor);
            Tag tag = new Tag("#"+sensor);
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#FECB50");
            tagGroup.addTag(tag);
        }

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
                tags.remove(position-sensorList.size()
                );

            }
        });

        addTag = findViewById(R.id.uploadTour_addTag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "#".concat(tagContent.getText().toString());
                if(tags.contains(content)){
                    Toast.makeText(UploadTourActivity.this, "TAG already existed!", Toast.LENGTH_SHORT);

                }else{
                    Tag tag = new Tag(content);
                    tag.isDeletable = true;
                    tag.tagTextColor = Color.parseColor("#FFFFFF");
                    tag.layoutColor = Color.parseColor("#73AD01");
                    tag.deleteIndicatorColor = Color.parseColor("#FFFFFF");
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

        imgGroup = findViewById(R.id.uploadTour_imgGroup);
        addImg = findViewById(R.id.uploadTour_addImg);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadTourBtn = (Button) findViewById(R.id.uploadTourBtn);
        uploadTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoadImg();
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        layoutParams.width = size;
        layoutParams.height = size;
        layoutParams.leftMargin = margin;
        layoutParams.topMargin = margin;
        layoutParams.rightMargin = margin;
        layoutParams.bottomMargin = margin;

        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
            Uri photoUri = data.getData();
            imgList.add(photoUri.toString());
            ImageView img = new ImageView(this);
            img.setLayoutParams(layoutParams);
            imgGroup.addView(img);

            Glide.with(this)
                    .load(photoUri)
                    .apply(options)
                    .into(img);
        }

    }

    private void upLoadImg() {
        for (int i = 0; i < imgList.size(); i++) {
            Uri uri = Uri.parse(imgList.get(i));
            final StorageReference ref = mStorageRef.child("Photos").child(uri.getLastPathSegment());
            ref.putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();
                                        imgUriList.add(downloadUrl);
                                        if(imgUriList.size() == imgList.size()){
                                            uploadPath();
                                        }

                                    }
                                });

                            }else{
                                Toast.makeText(UploadTourActivity.this,"Image Upload Failed", LENGTH_SHORT) .show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
    }

    private void uploadPath() {
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
                                tags,
                                new ArrayList<String>(), //likeList
                                sensorList,
                                imgUriList,
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
}
