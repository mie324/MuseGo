package com.ece1778.musego.UI.Tour;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.collection.ArrayMap;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.ece1778.musego.UI.User.UserProfileActivity;
import com.ece1778.musego.Utils.Loading;
import com.ece1778.musego.Utils.PopularCompareUtil;
import com.ece1778.musego.Utils.ShortToLongCompareUtil;
import com.ece1778.musego.Utils.TimeCompareUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class TourListActivity extends BaseActivity implements View.OnClickListener, AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    private static final String TAG = "TourList";
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private TourListAdapter adapter;
    private List<Path> pathList = new ArrayList<>();
    private List<Path> allPath = new ArrayList<>();
    public List<String> tagsList = new ArrayList<>();
    public List<String> sensorList = new ArrayList<>();
    private String instName;

    private FloatingActionButton createPathBtn, fab2;
    private SearchFabFragment dialogFrag;

    private AppCompatSpinner sorting;

    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Toolbar toolbar;


    private User user;
    private static final int VISITOR = 0;
    private static final int PUBLIC_USER = 1;
    private static final int PROFESSION_USER = 2;
    private int role = VISITOR;

    private Loading loading;
    private FloatingActionButton createTourBtn;








    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_list);

        initFirebase();
        initView();
        fetchUserInfo();
        fetchDataAndRenderView();


    }

    private void initFirebase() {
        firebaseManager = new FirebaseManager(TourListActivity.this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }

    @SuppressLint("RestrictedApi")
    private void initView() {

        toolbar = findViewById(R.id.toolbar);

        instName = getIntent().getStringExtra("instName");
        String userJson = getIntent().getStringExtra("currentUser");
        user = new Gson().fromJson(userJson,User.class);


        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        layoutManager = new GridLayoutManager(TourListActivity.this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TourListAdapter(TourListActivity.this, pathList, instName,user);
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

        sorting = (AppCompatSpinner) findViewById(R.id.sorting);
        sorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                switch(position){
                    case 0:
                        Collections.sort(pathList,new TimeCompareUtil());
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        Collections.sort(pathList, new PopularCompareUtil());
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        Collections.sort(pathList, new ShortToLongCompareUtil());
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        Collections.sort(pathList, new ShortToLongCompareUtil());
                        Collections.reverse(pathList);
                        adapter.notifyDataSetChanged();
                        break;

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        handleToolbar();


    }

    private void handleToolbar() {
        setSupportActionBar(toolbar);
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
                                    (String) document.get("avatar"),
                                    (String) document.get("bio"),
                                    (int) (long) document.get("role")
                            );
                            role = (int) (long) document.get("role");


                            handleDrawer();
                        }

                    }
                });
    }

    private void handleDrawer(){

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext())
                        .load(uri)
                        .apply(options
                                .placeholder(R.drawable.avatar))
                        .into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.with(imageView.getContext()).clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });


        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.darkGreen)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getUsername()).withEmail(roleFormat(user.getRole())).withIcon(user.getAvatar())
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem institute = new PrimaryDrawerItem().withIdentifier(1).withName("Institutions").withIcon(R.drawable.ic_account_balance);
        PrimaryDrawerItem help = new PrimaryDrawerItem().withIdentifier(2).withName("Help").withIcon(R.drawable.ic_info);
        PrimaryDrawerItem myProfile = new PrimaryDrawerItem().withIdentifier(3).withName("My Profile").withIcon(R.drawable.ic_account_circle);
        // PrimaryDrawerItem logoutHere = new PrimaryDrawerItem().withIdentifier(3).withName("Log Out");

        PrimaryDrawerItem logout = new PrimaryDrawerItem().withIdentifier(4).withName("Log Out").withIcon(R.drawable.ic_account_circle);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        institute,
                        help,
                        myProfile,
                        new DividerDrawerItem(),
                        logout


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                            case 1:
                                startActivity(new Intent(TourListActivity.this, MuseumListActivity.class));
                                break;

                            case 2:
                                startActivity(new Intent(TourListActivity.this, HelperActivity.class));
                                break;
                            case 3:
                                Intent intent = new Intent(TourListActivity.this, UserProfileActivity.class);
                                intent.putExtra("user", new Gson().toJson(user));
                                startActivity(intent);
                                break;
                            default:
                                logoutHere();
                                break;
                        }

                        return false;
                    }
                })
                .build();

        //result.addStickyFooterItem(logout);
    }

    private void logoutHere(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out of MuseGo?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                if(currentUser != null){

                    mAuth.signOut();
                    Intent intent = new Intent(TourListActivity.this, SigninActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
            }


        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                                        path.getLikeList(),
                                        path.getSensorList(),
                                        path.getImgList(),
                                        path.getStart_node(),
                                        path.getEnd_node(),
                                        path.getNodes());

                                pathList.add(pathModel);
                                allPath.add(pathModel);
                                adapter.notifyDataSetChanged();
                            }


                            if(role == 2) {
                                createTourBtn.setVisibility(View.VISIBLE);
                            }
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
                        sensorList = (List<String>)documentSnapshot.get("sensorList");

                        Log.d("!!!!!!TAG",""+sensorList.size());

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


                    for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {

                        switch (entry.getKey()) {
                            case "TAG":

                                filteredList = getTagFilteredPath(entry.getValue(), filteredList);
                                break;

                            case "ACCESSIBILITY":
                                filteredList = getSensorTagFilteredPath(entry.getValue(), filteredList);
                                break;


                            case "FLOOR":
                                filteredList = getFloorFilteredPath(entry.getValue(), filteredList);
                                break;

                            case "TIME":
                                filteredList = getTimeFilteredPath(entry.getValue(), filteredList);

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

    private List<Path> getTimeFilteredPath(List<String> lengths, List<Path> filteredList){
        List<Path> tempList = new ArrayList<>();

        for(String length: lengths) {
            String h1 = length.charAt(0) + "";
            tempList = new ArrayList<>();
            for (Path path : filteredList) {

                String h2 = path.getEstimated_time().split("/")[0];
                if (h2.equals(h1)) {
                    tempList.add(path);
                }
            }
            filteredList = tempList;
        }

        return tempList;


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

    private List<Path> getSensorTagFilteredPath(List<String> tags, List<Path> newPathList){
        List<Path> tempList = new ArrayList<>();



        for(String tag: tags){

            tempList = new ArrayList<>();
            for(Path path: newPathList){


                if(path.getSensorList() != null){
                    if(path.getSensorList().contains(tag)) {
                        if (!tempList.contains(path)) {
                            tempList.add(path);
                        }
                    }

                }
            }
            newPathList = tempList;


        }

        return tempList;

    }

    private List<Path> getTagFilteredPath(List<String> tags, List<Path> newPathList){
        List<Path> tempList = new ArrayList<>();



        for(String tag: tags){

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





    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createTourBtn) {

            if(role == PROFESSION_USER){
                Intent intent = new Intent(TourListActivity.this, CreateInstructionActivity.class);
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





    private String roleFormat(int role) {
        if(role == 1){
            return "Public User";
        }else if(role == 2){
            return  "Professional User";
        }else{
            return "Visitor";
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
