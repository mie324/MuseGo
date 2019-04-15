package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ece1778.musego.Adapter.CommentListAdapter;
import com.ece1778.musego.Model.Comment;
import com.ece1778.musego.Model.CommentList;
import com.ece1778.musego.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private BottomSheetDialog dialog;
    private TextView bt_comment;

    private String pid;
    private String userId;
    private String username;
    private String userAvatar;
    private String pathUserAvatar;
    private String pathUsername;
    private String pathContent;
    private boolean isExpand;
    private List<Comment> commentList;

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private CommentListAdapter adapter;

    private ImageView pathUserAvatarIV;
    private TextView pathUsernameTV;
    private TextView pathContentTV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

        initView();
        initData();
    }



    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pathUserAvatarIV = (ImageView) findViewById(R.id.userAvatar);
        pathUsernameTV = (TextView) findViewById(R.id.username);
        pathContentTV = (TextView) findViewById(R.id.content);

        bt_comment = (TextView)findViewById(R.id.detail_page_do_comment);
        bt_comment.setOnClickListener(this);

    }

    private void initData() {

        Intent intent = getIntent();
        isExpand = intent.getBooleanExtra("isExpand",false);
        pid = intent.getStringExtra("postId");
        userId = intent.getStringExtra("userId");
        username = intent.getStringExtra("username");
        userAvatar = intent.getStringExtra("userAvatar");
        pathUserAvatar = intent.getStringExtra("pathUserAvatar");
        pathUsername = intent.getStringExtra("pathUsername");
        pathContent = intent.getStringExtra("pathContent");
        String commentJson = getIntent().getStringExtra("commentList");
        CommentList cl = new Gson().fromJson(commentJson, CommentList.class);
        commentList = cl.getCommentList();


        if(isExpand){
            showCommentDialog();
        }

        initPathInfo();
        initAdapter(commentList);




    }

    private void initPathInfo() {

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        Glide.with(this)
                .load(pathUserAvatar)
                .apply(options)
                .into(pathUserAvatarIV);

        pathUsernameTV.setText(pathUsername);
        pathContentTV.setText(pathContent);


    }

    private void initAdapter(List<Comment> commentList) {

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewId);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentListAdapter(this,commentList);
        recyclerView.setAdapter(adapter);

    }


    private void showCommentDialog() {

        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.activity_comment_dialog, null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button sendComment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);


        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){

                    dialog.dismiss();

                    Comment comment = new Comment(pid,userId,commentContent,userAvatar,username, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                    //commentList.add(comment);
                    adapter.addTheCommentData(comment);
                    Toast.makeText(CommentDetailActivity.this,"Comment Successfully.",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(CommentDetailActivity.this,"Comment can't be empty.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    sendComment.setBackgroundColor(Color.parseColor("#FFD600"));
                }else {
                    sendComment.setBackgroundColor(Color.parseColor("#E0E0E0"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if(i == R.id.detail_page_do_comment){
            showCommentDialog();
        }

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){

            Intent in = new Intent();
            in.putExtra("commentList", new Gson().toJson(new CommentList(commentList)));
            setResult(RESULT_OK, in);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
