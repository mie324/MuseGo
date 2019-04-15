package com.ece1778.musego.Utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ece1778.musego.R;

public class Loading extends RelativeLayout {


    private RelativeLayout relativeLayoutLoading;
    private TextView loadingText;


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
        loadingText = view.findViewById(R.id.loadingtext);


    }

    public void setLoadingText(String text){
        loadingText.setText(text);

    }

    public void showLoading(){
        relativeLayoutLoading.setVisibility(VISIBLE);

    }

    public void hideLoading(){

        relativeLayoutLoading.setVisibility(GONE);

    }











}
