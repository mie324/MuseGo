package com.ece1778.musego.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Museum.MuseumListActivity;
import com.ece1778.musego.UI.Tour.TourDetailActivity;
import com.ece1778.musego.UI.Tour.TourListActivity;
import com.google.gson.Gson;
import com.like.LikeButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuseumListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Map<String, String> instMap = new HashMap<>();


    // anime
    private int lastAnimatedPosition=-1;
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    private Context context;
    private List<String> instList;

    public MuseumListAdapter(Context context, List<String> instList){
        this.context = context;
        this.instList = instList;


        instMap.put("osc","Ontario Science Center");
        instMap.put("rom", "Royal Ontario Museum");

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.cardview_museum, viewGroup, false);

        RecyclerView.ViewHolder viewHolder = new ViewHolder_Inst(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        Log.d("!!!!!!",i+" "+instList.get(i));

        runEnterAnimation(viewHolder.itemView, i);



        String inst = instList.get(i);

        ((ViewHolder_Inst) viewHolder).title.setText(instMap.get(inst));


        if(inst.equals("osc")){
            Glide.with(context)
                    .load(R.drawable.osc)
                    .into(((ViewHolder_Inst) viewHolder).image);

        }
        else if(inst.equals("rom")){
            Glide.with(context)
                    .load(R.drawable.rom)
                    .into(((ViewHolder_Inst) viewHolder).image);
        }


        ((ViewHolder_Inst) viewHolder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TourListActivity.class);
                //intent.putExtra("path", path);
                intent.putExtra("instName",inst);

                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return instList.size();
    }

    private void runEnterAnimation(View view, int position) {

        if (animationsLocked) return;//animationsLocked是布尔类型变量，一开始为false，确保仅屏幕一开始能够显示的item项才开启动画


        if (position > lastAnimatedPosition) {//lastAnimatedPosition是int类型变量，一开始为-1，这两行代码确保了recycleview滚动式回收利用视图时不会出现不连续的效果
            lastAnimatedPosition = position;
            view.setTranslationY(500);//相对于原始位置下方400
            view.setAlpha(0.f);//完全透明
            //每个item项两个动画，从透明到不透明，从下方移动到原来的位置
            //并且根据item的位置设置延迟的时间，达到一个接着一个的效果
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)//根据item的位置设置延迟时间，达到依次动画一个接一个进行的效果
                    .setInterpolator(new DecelerateInterpolator(0.5f))//设置动画效果为在动画开始的地方快然后慢
                    .setDuration(700)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;//确保仅屏幕一开始能够显示的item项才开启动画，也就是说屏幕下方还没有显示的item项滑动时是没有动画效果
                        }
                    })
                    .start();
        }
    }


    public class ViewHolder_Inst extends RecyclerView.ViewHolder {

        public CardView cardView;

        public ImageView image;
        public TextView title;


        public ViewHolder_Inst(View view){
            super(view);

            image = (ImageView) view.findViewById(R.id.instImage);
            title = (TextView) view.findViewById(R.id.instName);
            cardView = (CardView) view.findViewById(R.id.cardview_id);


        }
    }


}
