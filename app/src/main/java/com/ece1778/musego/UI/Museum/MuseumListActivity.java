package com.ece1778.musego.UI.Museum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

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
import com.ece1778.musego.UI.User.UserProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

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

    private List<String> museumList = new ArrayList<>(Arrays.asList("osc", "rom"));

    private ArrayList<String> mTitles = new ArrayList<>();

    private ViewHolder mViewHolder;
    private MenuAdapter mMenuAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseManager firebasemanager;

    private User user;


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


        // Initialize the views
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle user info
        handleUserInfo();


        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleUserInfo() {


        firebasemanager.getUserRef()
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);

                        RequestOptions options = new RequestOptions();
                        options.centerCrop();
                        options.circleCrop();


                        Glide.with(MuseumListActivity.this)
                                .load(user.getAvatar())
                                .apply(options)
                                .into(mViewHolder.avatar);

                        mViewHolder.username.setText(user.getUsername());
                        mViewHolder.bio.setText(roleFormat(user.getRole()));

                        // set adapter

                        adapter = new MuseumListAdapter(MuseumListActivity.this, museumList, new User(user.getUsername(), user.getAvatar(), user.getBio(), user.getRole()));
                        recyclerView.setAdapter(adapter);


                    }
                });


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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out of MuseGo?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                if (currentUser != null) {

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

            case 0:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("user", new Gson().toJson(user));
                startActivity(intent);
                break;

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
        private ImageView avatar;
        private TextView username;
        private TextView bio;


        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            avatar = (ImageView) mDuoMenuView.findViewById(R.id.userAvatar);
            username = (TextView) mDuoMenuView.findViewById(R.id.username);
            bio = (TextView) mDuoMenuView.findViewById(R.id.userbio);
        }
    }
}
