package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Comment;
import com.ece1778.musego.Model.CommentList;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.like.LikeButton;

import java.util.ArrayList;
import java.util.List;

public class TourDetailActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, View.OnClickListener {

    private static final String TAG = "TOURDETAIL";
    private static final int GET_COMMENT = 1;

    private TextView title;
    private ImageView avatar;
    private SliderLayout img;
    private TagView tagView;
    private TextView username;
    private TextView timestamp;
    private TextView desc;
    private TextView floor;
    private LikeButton likeBt;
    private TextView likeCount;
    private List<String> imageList = new ArrayList<>();

    private Path path;
    private User user;
    private String instName;
    private NodeList nodeList;
    private TextView tourTime;

    private Toolbar toolbar;
    private ImageView iv_comment;
    private TextView viewComment;

    private FirebaseManager firebaseManager;
    private String uid;

    private List<Comment> commentList;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);


        initView();
        initFirebase();
        initData();

    }




    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = findViewById(R.id.detail_title);
        tagView = findViewById(R.id.detail_tags);
        username = findViewById(R.id.detail_user);
        timestamp = findViewById(R.id.detail_timestamp);
        desc = findViewById(R.id.detail_desc);
        avatar = findViewById(R.id.detail_avatar);
        floor = findViewById(R.id.floor);
        likeBt = findViewById(R.id.like);
        likeCount = findViewById(R.id.likeCount);


        tourTime = findViewById(R.id.tourTime);

        findViewById(R.id.startArBtn).setOnClickListener(this);

        img = (SliderLayout)findViewById(R.id.detail_img);
        iv_comment = (ImageView) findViewById(R.id.detail_page_do_comment);
        iv_comment.setOnClickListener(this);

        viewComment = (TextView) findViewById(R.id.viewComment);
        viewComment.setOnClickListener(this);

    }

    private void initFirebase(){
        firebaseManager = new FirebaseManager(this);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void initData() {

        String pathJson = getIntent().getStringExtra("path");
        path = new Gson().fromJson(pathJson,Path.class);

        String userJson = getIntent().getStringExtra("user");
        user = new Gson().fromJson(userJson, User.class);

        instName = getIntent().getStringExtra("instName");

        imageList = path.getImgList();
        title.setText(path.getTitle());
        username.setText("By ".concat(path.getUsername()));
        timestamp.setText("Last Updated "+timeFormat(path.getTimestamp()));
        desc.setText(path.getDescription());
        floor.setText(path.getFloor());

        String[] times = path.getEstimated_time().split("/");
        String estimatedTime = "";
        if(!times[0].equals("0")){
            estimatedTime += times[0] + "h";
        }
        estimatedTime += times[1] + "min";

        tourTime.setText(estimatedTime);


        initLike();
        initAvatar();
        initTagData();
        initCommentData();
        initImgView();


        nodeList = new NodeList(path.getStart_node(),path.getEnd_node(),path.getNodes());

    }

    private void initLike() {



        likeCount.setText(path.getLikeList().size()+"");
        if(path.getLikeList().contains(uid)){
           likeBt.setLiked(true);
        }


//        likeBt.setOnLikeListener(new OnLikeListener() {
//            @Override
//            public void liked(LikeButton likeButton) {
//
//                if (!path.getLikeList().contains(uid)) {
//
//
//                    path.getLikeList().add(uid);
//
//                   likeCount.setText("" + path.getLikeList().size());
//
//                    firebaseManager.getInstRef()
//                            .document(instName)
//                            .collection("paths")
//                            .document(path.getpId())
//                            .update("likeList", path.getLikeList())
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//
//
//
//                                }
//                            });
//                }
//            }


//            @Override
//            public void unLiked(LikeButton likeButton) {
//
//
//                if(path.getLikeList().contains(uid)) {
//
//                    path.getLikeList().remove(uid);
//
//                  likeCount.setText(""+path.getLikeList().size());
//
//                    firebaseManager.getInstRef()
//                            .document(instName)
//                            .collection("paths")
//                            .document(path.getpId())
//                            .update("likeList", path.getLikeList())
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                    // ((ViewHolder_Path) viewHolder).likeCount.setText(""+path.getLikeList().size());
//
//                                }
//                            });
//                }
//            }
//        });

    }

    private void initTagData(){

        for(String content: path.getTags()){
            Tag tag = new Tag(content);
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#73AD01");
            tag.deleteIndicatorColor = Color.parseColor("#FFFFFF");
            tagView.addTag(tag);
        }

        for(String content: path.getSensorList()){
            Tag tag = new Tag(content);
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#73AD01");
            tag.deleteIndicatorColor = Color.parseColor("#FFFFFF");
            tagView.addTag(tag);
        }



    }

    private void initAvatar(){

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        Glide.with(this)
                .load(Uri.parse(path.getUserAvatar()))
                .apply(options)
                .into(avatar);
    }

    private void initCommentData() {
        commentList = new ArrayList<>();

        firebaseManager.getCommentRef()
                .whereEqualTo("postId",path.getpId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            if(task.getResult() != null && task.getResult().size() > 0) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Comment comment = document.toObject(Comment.class);

                                    commentList.add(comment);

                                }

                                viewComment.setText("View all "+commentList.size()+" comments");
                                viewComment.setVisibility(View.VISIBLE);

                            }

                            iv_comment.setVisibility(View.VISIBLE);

                        }
                    }
                });

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
        else if( i == R.id.detail_page_do_comment){


            Intent intent = new Intent(TourDetailActivity.this, CommentDetailActivity.class);
            intent.putExtra("isExpand", true);
            intent.putExtra("postId", path.getpId());
            intent.putExtra("userId", uid);
            intent.putExtra("username", user.getUsername());
            intent.putExtra("userAvatar", user.getAvatar());
            intent.putExtra("commentList",new Gson().toJson(new CommentList(commentList)));
            intent.putExtra("pathUsername", path.getUsername());
            intent.putExtra("pathUserAvatar", path.getUserAvatar());
            intent.putExtra("pathContent", path.getDescription());
            startActivityForResult(intent,GET_COMMENT);


        }

        else if( i == R.id.viewComment){

            Intent intent = new Intent(TourDetailActivity.this, CommentDetailActivity.class);
            intent.putExtra("isExpand", false);
            intent.putExtra("postId", path.getpId());
            intent.putExtra("userId", uid);
            intent.putExtra("username", user.getUsername());
            intent.putExtra("userAvatar", user.getAvatar());
            intent.putExtra("commentList",new Gson().toJson(new CommentList(commentList)));
            intent.putExtra("pathUsername", path.getUsername());
            intent.putExtra("pathUserAvatar", path.getUserAvatar());
            intent.putExtra("pathContent", path.getDescription());
            startActivityForResult(intent, GET_COMMENT);



        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case GET_COMMENT:
                if(resultCode == RESULT_OK) {

                    String commentJson = data.getStringExtra("commentList");
                    CommentList cl = new Gson().fromJson(commentJson, CommentList.class);
                    commentList = cl.getCommentList();

                    viewComment.setText("View all "+commentList.size()+" comments");
                    viewComment.setVisibility(View.VISIBLE);

                }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
