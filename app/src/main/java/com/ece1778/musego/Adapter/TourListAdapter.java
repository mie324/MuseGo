package com.ece1778.musego.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Tour.TourDetailActivity;
import com.ece1778.musego.UI.Tour.TourListActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

public class TourListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // anime
    private int lastAnimatedPosition=-1;
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    private Context mContext;

    //private Path path;
    private List<Path> pathList;
    private String instName;
    private FirebaseUser currentUser;
    private FirebaseManager firebaseManager;

    public TourListAdapter(Context context, List<Path> pathList, String instName){
        this.mContext = context;
        this.pathList = pathList;
        this.instName = instName;

        firebaseManager = new FirebaseManager(context);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view_path = LayoutInflater.from(mContext).inflate(R.layout.cardview_tour, viewGroup, false);
                //View.inflate(mContext, R.layout.cardview_tour, null);

        RecyclerView.ViewHolder viewHolder = new ViewHolder_Path(view_path);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        runEnterAnimation(viewHolder.itemView, i);

        Path path = pathList.get(i);


        ((ViewHolder_Path) viewHolder).title.setText(path.getTitle());

        if (path.getDescription().length() > 0) {
            ((ViewHolder_Path) viewHolder).description.setText(path.getDescription());
        } else {
            ((ViewHolder_Path) viewHolder).description.setVisibility(View.GONE);

        }

        ((ViewHolder_Path) viewHolder).username.setText(path.getUsername());
        ((ViewHolder_Path) viewHolder).likeCount.setText("" + path.getLikeList().size());

        //avatar
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        Glide.with(mContext)
                .load(path.getUserAvatar())
                .apply(options)
                .into(((ViewHolder_Path) viewHolder).avatar);


        ((ViewHolder_Path) viewHolder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, TourDetailActivity.class);
                //intent.putExtra("path", path);
                intent.putExtra("path", new Gson().toJson(path));
                mContext.startActivity(intent);
            }
        });

        if(path.getLikeList().contains(currentUser.getUid())){
            ((ViewHolder_Path) viewHolder).like.setLiked(true);
        }

        ((ViewHolder_Path) viewHolder).like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {



                if (!path.getLikeList().contains(currentUser.getUid())) {


                    path.getLikeList().add(currentUser.getUid());

                    ((ViewHolder_Path) viewHolder).likeCount.setText("" + path.getLikeList().size());

                    firebaseManager.getInstRef()
                            .document(instName)
                            .collection("paths")
                            .document(path.getpId())
                            .update("likeList", path.getLikeList())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {




                                }
                            });
                }
        }


            @Override
            public void unLiked(LikeButton likeButton) {

                if(path.getLikeList().contains(currentUser.getUid())) {

                    path.getLikeList().remove(currentUser.getUid());

                    ((ViewHolder_Path) viewHolder).likeCount.setText(""+path.getLikeList().size());

                    firebaseManager.getInstRef()
                            .document(instName)
                            .collection("paths")
                            .document(path.getpId())
                            .update("likeList", path.getLikeList())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                   // ((ViewHolder_Path) viewHolder).likeCount.setText(""+path.getLikeList().size());

                                }
                            });
                }
            }
        });

    }

    private void runEnterAnimation(View view, int position) {

        if (animationsLocked) return;//animationsLocked是布尔类型变量，一开始为false，确保仅屏幕一开始能够显示的item项才开启动画


        if (position > lastAnimatedPosition) {//lastAnimatedPosition是int类型变量，一开始为-1，这两行代码确保了recycleview滚动式回收利用视图时不会出现不连续的效果
            lastAnimatedPosition = position;
            view.setTranslationY(100);//相对于原始位置下方400
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



    public void addPath(Path path){

        if(path != null) {
            pathList.add(path);
            notifyDataSetChanged();


        }

        else{
            throw new IllegalArgumentException("Path is null!");
        }

    }



    @Override
    public int getItemCount() {

        return pathList.size();
    }


    public class ViewHolder_Path extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView title;
        public TextView description;
        public TextView username;
        public ImageView avatar;
        public LikeButton like;
        public TextView likeCount;


        public ViewHolder_Path(View view){
            super(view);

            title = (TextView) view.findViewById(R.id.tourTitle);
            description = (TextView) view.findViewById(R.id.tourDescription);
            username = (TextView) view.findViewById(R.id.username);
            avatar = (ImageView) view.findViewById(R.id.userAvatar);
            like = (LikeButton) view.findViewById(R.id.like);
            likeCount = (TextView) view.findViewById(R.id.likeCount);
            cardView = (CardView) view.findViewById(R.id.cardview_id);


        }
    }
}
