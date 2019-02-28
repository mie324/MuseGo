package com.ece1778.musego.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Tour.TourDetailActivity;
import com.ece1778.musego.UI.Tour.TourListActivity;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

public class TourListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    //private Path path;
    private List<Path> pathList;

    public TourListAdapter(Context context, List<Path> pathList){
        this.mContext = context;
        this.pathList = pathList;


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

        Path path = pathList.get(i);

        ((ViewHolder_Path) viewHolder).title.setText(path.getTitle());

        if(path.getDescription().length() > 0) {
            ((ViewHolder_Path) viewHolder).description.setText(path.getDescription());
        }else{
            ((ViewHolder_Path) viewHolder).description.setVisibility(View.GONE);

        }

        ((ViewHolder_Path) viewHolder).like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {



            }

            @Override
            public void unLiked(LikeButton likeButton) {



            }
        });

        ((ViewHolder_Path) viewHolder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("!!!!",path.getpId()+"    "+i);
                Intent intent = new Intent(mContext, TourDetailActivity.class);
                //intent.putExtra("path", path);
                intent.putExtra("path", new Gson().toJson(path));
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {

        return pathList.size();
    }



    public class ViewHolder_Path extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView title;
        public TextView description;
        public LikeButton like;


        public ViewHolder_Path(View view){
            super(view);

            title = (TextView) view.findViewById(R.id.tourTitle);
            description = (TextView) view.findViewById(R.id.tourDescription);
            like = (LikeButton) view.findViewById(R.id.like);
            cardView = (CardView) view.findViewById(R.id.cardview_id);


        }
    }
}
