package com.ece1778.musego.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Comment;
import com.ece1778.musego.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int COMMENT_SIMPLE = 0;
    private static final int COMMENT_COMPLEX = 1;

    private Context context;
    private List<Comment> commentList;
    private FirebaseManager firebaseManager;

    public CommentListAdapter(Context context, List<Comment> commentList){
        this.context = context;
        this.commentList = commentList;

        firebaseManager = new FirebaseManager(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = View.inflate(context, R.layout.cardview_comment_simple, null);
        RecyclerView.ViewHolder viewHolder = new ViewHolder_Comment(view);

        return viewHolder;



    }

//

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        Comment comment = commentList.get(i);


        ((CommentListAdapter.ViewHolder_Comment) viewHolder).username.setText(comment.getUsername());
        ((CommentListAdapter.ViewHolder_Comment) viewHolder).content.setText(comment.getContent());
        ((ViewHolder_Comment) viewHolder).timestamp.setText(timeFormat(comment.getTimestamp()));

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        Glide.with(context)
                .load(comment.getProfilePhoto())
                .apply(options)
                .into(((ViewHolder_Comment) viewHolder).avatar);



    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    //评论成功之后插入一条新的数据
    public void addTheCommentData(Comment comment){
        if(comment != null){

            commentList.add(comment);

            firebaseManager.getCommentRef()
                    .add(comment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            notifyDataSetChanged();
                        }
                    });

        }
        else{
            throw new IllegalArgumentException("Comment is null!");
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



    public class ViewHolder_Comment extends RecyclerView.ViewHolder{

        public TextView username, content, timestamp;
        public ImageView avatar;



        public ViewHolder_Comment(View view){
            super(view);


            username = (TextView) view.findViewById(R.id.comment_item_username);
            content = (TextView) view.findViewById(R.id.comment_item_content);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            avatar = (ImageView) view.findViewById(R.id.comment_item_avatar);

        }

    }
}
