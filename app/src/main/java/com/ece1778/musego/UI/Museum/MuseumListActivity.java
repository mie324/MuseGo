package com.ece1778.musego.UI.Museum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ece1778.musego.Adapter.MenuAdapter;
import com.ece1778.musego.Adapter.MuseumListAdapter;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Auth.SigninActivity;
import com.ece1778.musego.UI.Tour.HelperActivity;
import com.ece1778.musego.UI.User.UserProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;


public class MuseumListActivity extends BaseActivity implements View.OnClickListener {
    //recyclerview here, this is just an exampl

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MuseumListAdapter adapter;

    private List<String> museumList = new ArrayList<>(Arrays.asList("osc", "rom"));


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseManager firebasemanager;

    private User user;

    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_list);

        initFirebase();

        initView();


    }

    private void initFirebase() {

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        firebasemanager = new FirebaseManager(this);
    }


    private void initView() {


        recyclerView = (RecyclerView) findViewById(R.id.recycleViewId);
        layoutManager = new GridLayoutManager(MuseumListActivity.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        toolbar = findViewById(R.id.toolbar);




        // Handle toolbar actions
        handleToolbar();

        // Handle user info
        handleUserInfo();

        //



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

       // PrimaryDrawerItem institute = new PrimaryDrawerItem().withIdentifier(1).withName("Institutions").withIcon(R.drawable.ic_account_balance);
        PrimaryDrawerItem help = new PrimaryDrawerItem().withIdentifier(1).withName("Help").withIcon(R.drawable.ic_info);
        PrimaryDrawerItem myProfile = new PrimaryDrawerItem().withIdentifier(2).withName("My Profile").withIcon(R.drawable.ic_account_circle);
       // PrimaryDrawerItem logoutHere = new PrimaryDrawerItem().withIdentifier(3).withName("Log Out");

        PrimaryDrawerItem logout = new PrimaryDrawerItem().withIdentifier(3).withName("Log Out").withIcon(R.drawable.ic_account_circle);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
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
                                Log.d("!!!!","1111");
                                startActivity(new Intent(MuseumListActivity.this, HelperActivity.class));

                                break;
                            case 2:
                                Intent intent = new Intent(MuseumListActivity.this, UserProfileActivity.class);
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
                    Intent intent = new Intent(MuseumListActivity.this, SigninActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void handleToolbar() {
        setSupportActionBar(toolbar);
    }

    private void handleUserInfo() {


        firebasemanager.getUserRef()
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);

//



                        // set adapter

                        adapter = new MuseumListAdapter(MuseumListActivity.this, museumList, new User(user.getUsername(), user.getAvatar(), user.getBio(), user.getRole()));
                        recyclerView.setAdapter(adapter);

                        handleDrawer();


                    }
                });


    }

    private String roleFormat(int role) {
        if (role == 1) {
            return "Public User";
        } else if (role == 2) {
            return "Professional User";
        } else {
            return "Visitor";
        }
    }






    @Override
    public void onClick(View v) {

    }



}
