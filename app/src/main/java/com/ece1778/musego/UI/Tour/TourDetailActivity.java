package com.ece1778.musego.UI.Tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Model.NodeList;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.R;
import com.google.gson.Gson;

public class TourDetailActivity extends BaseActivity implements View.OnClickListener{

    private TextView title;
    private TextView tags;
    private TextView username;
    private TextView timestamp;
    private TextView description;
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

        title.setText(path.getDescription());

        for(String tag: path.getTags()){
            tags.append("#"+tag+" ");
        }

        timestamp.setText(path.getTimestamp());
        description.setText(path.getDescription());


        nodeList = new NodeList(path.getStart_node(),path.getEnd_node(),path.getNodes());



    }

    private void initView() {

        title = (TextView) findViewById(R.id.title);
        tags = (TextView) findViewById(R.id.tags);
        username = (TextView) findViewById(R.id.username);
        timestamp = (TextView) findViewById(R.id.timestamp);
        description = (TextView) findViewById(R.id.description);


        findViewById(R.id.startArBtn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.startArBtn) {
            Intent intent = new Intent(TourDetailActivity.this, ArShowTourActivity.class);
            intent.putExtra("nodeList", new Gson().toJson(nodeList));
            startActivity(intent);
        }

    }
}
