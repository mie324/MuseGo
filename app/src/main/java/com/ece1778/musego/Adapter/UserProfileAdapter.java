package com.ece1778.musego.Adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.ece1778.musego.Model.ExpandableGroupEntity;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.R;

import java.util.ArrayList;
import java.util.List;

public class UserProfileAdapter extends GroupedRecyclerViewAdapter {

    private Context context;
    private ArrayList<ExpandableGroupEntity> groups;



    public UserProfileAdapter(Context context, ArrayList<ExpandableGroupEntity> groups) {
        super(context);

        this.context = context;

        this.groups = groups;

    }



    @Override
    public int getGroupCount() {
        return groups == null ? 0 : groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if(!isExpand(groupPosition)){
            return 0;
        }
        ArrayList<Path> children = groups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();

    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.expandable_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.cardview_tour;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {

        ExpandableGroupEntity entity = groups.get(groupPosition);
        holder.setText(R.id.tv_expandable_header,entity.getHeader());
        ImageView ivstate = holder.get(R.id.iv_state);
        if(entity.isExpand()) {
            ivstate.setRotation(90);
        }
        else{
            ivstate.setRotation(0);
        }

    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {

            Path path = groups.get(groupPosition).getChildren().get(childPosition);

            ImageView avatar = holder.get(R.id.userAvatar);
            ImageView firstImage = holder.get(R.id.firstImage);

            holder.setText(R.id.username,path.getUsername());
            holder.setText(R.id.tourTitle, path.getTitle());
            holder.setText(R.id.tourDescription, path.getDescription());
            holder.setText(R.id.likeCount, path.getLikeList().size()+"");

            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.circleCrop();



            Glide.with(context)
                .load(path.getUserAvatar())
                .apply(options)
                .into(avatar);

            if(path.getImgList() == null || path.getImgList().size() == 0){
                Glide.with(context)
                        .load(R.drawable.scanme)
                        .into(firstImage);

            }
            else{
                Glide.with(context)
                        .load(path.getImgList().get(0))
                        .into(firstImage);

            }


    }

    /**
     * 判断当前组是否展开
     *
     * @param groupPosition
     * @return
     */
    public boolean isExpand(int groupPosition) {
        ExpandableGroupEntity entity = groups.get(groupPosition);
        return entity.isExpand();

    }

    /**
     * 展开一个组
     *
     * @param groupPosition
     */
    public void expandGroup(int groupPosition) {
        expandGroup(groupPosition, false);
    }

    /**
     * 展开一个组
     *
     * @param groupPosition
     * @param animate
     */
    public void expandGroup(int groupPosition, boolean animate) {
        ExpandableGroupEntity entity = groups.get(groupPosition);
        entity.setExpand(true);
        if (animate) {
            notifyChildrenInserted(groupPosition);
        } else {
            notifyDataChanged();
        }
    }

    /**
     * 收起一个组
     *
     * @param groupPosition
     */
    public void collapseGroup(int groupPosition) {
        collapseGroup(groupPosition, false);
    }

    /**
     * 收起一个组
     *
     * @param groupPosition
     * @param animate
     */
    public void collapseGroup(int groupPosition, boolean animate) {
        ExpandableGroupEntity entity = groups.get(groupPosition);
        entity.setExpand(false);
        if (animate) {
            notifyChildrenRemoved(groupPosition);
        } else {
            notifyDataChanged();
        }
    }
}
