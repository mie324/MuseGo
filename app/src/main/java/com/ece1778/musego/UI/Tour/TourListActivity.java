package com.ece1778.musego.UI.Tour;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.ece1778.musego.Adapter.MenuAdapter;
import com.ece1778.musego.Adapter.TourListAdapter;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.Path;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Auth.SigninActivity;
import com.ece1778.musego.UI.Museum.MuseumListActivity;
import com.ece1778.musego.UI.Search.SearchFabFragment;
import com.ece1778.musego.Utils.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class TourListActivity extends BaseActivity implements View.OnClickListener, AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener, DuoMenuView.OnMenuClickListener {

    private static final String TAG = "TourList";
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private TourListAdapter adapter;
    private List<Path> pathList = new ArrayList<>();
    private List<Path> allPath = new ArrayList<>();
    public List<String> tagsList = new ArrayList<>();
    private String instName;

    private FloatingActionButton createPathBtn, fab2;
    private SearchFabFragment dialogFrag;

    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;

    private User user;
    private static final int VISITOR = 0;
    private static final int PUBLIC_USER = 1;
    private static final int PROFESSION_USER = 2;
    private int role = VISITOR;

    private Loading loading;
    private FloatingActionButton createTourBtn;

    private ArrayList<String> mTitles = new ArrayList<>();

    private ViewHolder mViewHolder;
    private MenuAdapter mMenuAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_list);

        initView();
        initFirebase();
        fetchUserInfo();
        fetchDataAndRenderView();

    }

    private void initFirebase() {
        firebaseManager = new FirebaseManager(TourListActivity.this);
        mAuth = FirebaseAuth.getInstance();

    }

    private void fetchUserInfo(){
        if(mAuth.getUid() == null){
            role = 0;
            return;
        }

        firebaseManager.getUserRef()
                .document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            user = new User(
                                    (String) document.get("username"),
                                    (String) document.get("bio"),
                                    (String) document.get("avatar"),
                                    (int) (long) document.get("role")
                            );
                            role = (int) (long) document.get("role");
                        }

                    }
                });
    }

    private void fetchDataAndRenderView() {

        firebaseManager.getInstRef()
                .document(instName)
                .collection("paths")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //loading.hideLoading();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Path path = document.toObject(Path.class);
                                String pid = document.getId();

                                Log.d("!!!!!!pid", pid);


                                Path pathModel = new Path(
                                        pid,
                                        path.getUserId(),
                                        path.getUsername(),
                                        path.getUserAvatar(),
                                        path.getUserBio(),
                                        path.getTimestamp(),
                                        path.getTitle(),
                                        path.getDescription(),
                                        path.getFloor(),
                                        path.getEstimated_time(),
                                        path.getTags(),
                                        path.getPrivacy(),
                                        path.getLikeList(),
                                        path.getImgList(),
                                        path.getStart_node(),
                                        path.getEnd_node(),
                                        path.getNodes());

                                pathList.add(pathModel);
                                allPath.add(pathModel);
                                adapter.notifyDataSetChanged();
                            }

                            createTourBtn.setVisibility(View.VISIBLE);
                            fab2.setVisibility(View.VISIBLE);
                        }
                    }
                });


        firebaseManager.getTagRef().document("tagList")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tagsList = (List<String>)documentSnapshot.get("tagList");

                    }
                });
    }

    @Override
    public void onResult(Object result) {

        if(result.toString().equalsIgnoreCase("swiped_down")){
            //
        }
        else{
            if(result != null) {
                ArrayMap<String, List<String>> applied_filters = (ArrayMap<String, List<String>>) result;
                if (applied_filters.size() != 0) {
                    List<Path> filteredList = allPath;
                    Log.d("!!!!!!!again!!!!","hhh");

                    for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {

                        switch (entry.getKey()) {
                            case "TAG":
                                Log.d("!!!!!!!", "tag");
                                filteredList = getTagFilteredPath(entry.getValue(), filteredList);
                                break;

                            case "FLOOR":
                                filteredList = getFloorFilteredPath(entry.getValue(), filteredList);

                        }
                    }

                    pathList.clear();
                    pathList.addAll(filteredList);
                    adapter.notifyDataSetChanged();

                } else {


                    pathList.clear();
                    pathList.addAll(allPath);
                    adapter.notifyDataSetChanged();
                }
            }

        }

    }

    private List<Path> getFloorFilteredPath(List<String> floors, List<Path> filteredList) {

        List<Path> tempList = new ArrayList<>();

        for(String floor: floors){
            tempList = new ArrayList<>();
            for(Path path: filteredList){

                if(path.getFloor().equals(floor)){
                    if(!tempList.contains(path)){
                        tempList.add(path);
                    }
                }
            }
            filteredList = tempList;


        }

        return tempList;




    }

    private List<Path> getTagFilteredPath(List<String> tags, List<Path> newPathList){
        List<Path> tempList = new ArrayList<>();



        for(String tag: tags){
                Log.d("!!newPathSizebefore", newPathList.size()+"");
                tempList = new ArrayList<>();
                for(Path path: newPathList){

                    if(path.getTags().contains(tag)){
                        if(!tempList.contains(path)){
                            tempList.add(path);
                        }
                    }
                }
                newPathList = tempList;


        }

        return tempList;

    }



    @SuppressLint("RestrictedApi")
    private void initView() {

        instName = getIntent().getStringExtra("instName");


        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        layoutManager = new GridLayoutManager(TourListActivity.this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TourListAdapter(TourListActivity.this, pathList, instName);
        recyclerView.setAdapter(adapter);
//
//        loading = (Loading) findViewById(R.id.loading);
//        loading.setLoadingText("I want data...");

        createTourBtn = (FloatingActionButton) findViewById(R.id.createTourBtn);
        createTourBtn.setOnClickListener(this);
        createTourBtn.setVisibility(View.GONE);

        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        dialogFrag = SearchFabFragment.newInstance();
        dialogFrag.setParentFab(fab2);
        fab2.setVisibility(View.GONE);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });


        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();


    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createTourBtn) {

            if(role == PROFESSION_USER){
                Intent intent = new Intent(TourListActivity.this, CreateInstructionActivity.class);
                //intent.putExtra("path", path);
                intent.putExtra("instName", instName);
                startActivity(intent);

            }else if(role == VISITOR){
                Intent intent = new Intent(TourListActivity.this, SigninActivity.class);
                startActivity(intent);

            }else {
                Toast.makeText(this, "Only Professionals can create path.",Toast.LENGTH_SHORT).show();
            }

        }

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
            default:
                //goToFragment(new MainFragment(), false);

                Log.d("!!!!!!!aaa",position+"");

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


    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");

    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");

    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");

    }


}
