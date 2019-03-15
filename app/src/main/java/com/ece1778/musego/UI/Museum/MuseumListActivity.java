package com.ece1778.musego.UI.Museum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ece1778.musego.Adapter.MenuAdapter;
import com.ece1778.musego.Adapter.MuseumListAdapter;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Auth.SignupActivity;
import com.ece1778.musego.UI.Tour.TourListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;


public class MuseumListActivity extends BaseActivity implements View.OnClickListener, DuoMenuView.OnMenuClickListener {
    //recyclerview here, this is just an example

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MuseumListAdapter adapter;

    private List<String> museumList = new ArrayList<>(Arrays.asList("osc","rom"));

    private ArrayList<String> mTitles = new ArrayList<>();

    private ViewHolder mViewHolder;
    private MenuAdapter mMenuAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_list);


        initView();


    }


    private void initView() {


        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        layoutManager = new GridLayoutManager(MuseumListActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MuseumListAdapter(MuseumListActivity.this, museumList);
        recyclerView.setAdapter(adapter);


           // Initialize the views
    mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
    mViewHolder = new ViewHolder();

    // Handle toolbar actions
    handleToolbar();

    // Handle menu actions
    handleMenu();

    // Handle drawer actions
    handleDrawer();

}

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {


        setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {

            case 1:
                startActivity(new Intent(this, MuseumListActivity.class));
                break;
            default:
                //goToFragment(new MainFragment(), false);
                //startActivity(new Intent(MuseumListActivity.this, MuseumListActivity.class));
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();



    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }
}
