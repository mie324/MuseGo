package com.ece1778.musego.Utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ece1778.musego.R;

public class Loading extends RelativeLayout {


    private RelativeLayout relativeLayoutLoading;


    public Loading(Context context) {
        super(context);
        initView(context);
    }


    public Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Loading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.loading,this);
        relativeLayoutLoading = view.findViewById(R.id.rl_progress_bar);


    }

    public void showLoading(){
        relativeLayoutLoading.setVisibility(VISIBLE);

    }

    public void hideLoading(){
        relativeLayoutLoading.setVisibility(GONE);
    }











}
