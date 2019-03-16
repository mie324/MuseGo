package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ece1778.musego.Adapter.CommentListAdapter;
import com.ece1778.musego.Model.Comment;
import com.ece1778.musego.Model.CommentList;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.R;
import com.google.gson.Gson;

import java.util.List;

public class CommentDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private BottomSheetDialog dialog;
    private TextView bt_comment;

    private String pid;
    private String userId;
    private String username;
    private String userAvatar;
    private boolean isExpand;
    private List<Comment> commentList;

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private CommentListAdapter adapter;



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
        String commentJson = getIntent().getStringExtra("commentList");
        CommentList cl = new Gson().fromJson(commentJson, CommentList.class);
        commentList = cl.getCommentList();


        if(isExpand){
            showCommentDialog();
        }

        initAdapter(commentList);



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

                    Comment comment = new Comment(pid,userId,commentContent,userAvatar,username,"now");
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
