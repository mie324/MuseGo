package com.ece1778.musego.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ece1778.musego.Model.Node;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.Rotation;
import com.ece1778.musego.Model.Translation;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Tour.TourListActivity;

import java.util.List;

public class TourListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private Path path;
    private List<Path> pathList;

    public TourListAdapter(Context context, List<Path> pathList){
        this.mContext = context;
        this.pathList = pathList;


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view_path = View.inflate(mContext, R.layout.cardview_tour, null);

        RecyclerView.ViewHolder viewHolder = new ViewHolder_Path(view_path);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        path = pathList.get(i);

        ((ViewHolder_Path) viewHolder).title.setText(path.getTitle());
        ((ViewHolder_Path) viewHolder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });





    }

    public class ViewHolder_Path extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView title;


        public ViewHolder_Path(View view){
            super(view);

            title = (TextView) view.findViewById(R.id.tourTitle);
            cardView = (CardView) view.findViewById(R.id.cardview_id);


        }
    }
}
