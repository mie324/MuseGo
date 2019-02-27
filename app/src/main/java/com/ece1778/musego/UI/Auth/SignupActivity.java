package com.ece1778.musego.UI.Auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Museum.MuseumListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class SignupActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SIGNUP";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_TAKE_PHOTO = 1;


    private ImageView mAvatarField;
    private EditText mEmailField;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private EditText mConfirmField;
    private EditText mBioField;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseStorage mStorage;
    private String uID;
    private byte[] byteArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        initView();
    }

    private void initView() {

        mAvatarField = findViewById(R.id.signup_avatar);
        mEmailField = findViewById(R.id.signup_email);
        mUsernameField = findViewById(R.id.signup_username);
        mPasswordField = findViewById(R.id.signup_password);
        mConfirmField = findViewById(R.id.signup_confirm);
        mBioField = findViewById(R.id.signup_bio);

        findViewById(R.id.signup_camera).setOnClickListener(this);
        findViewById(R.id.signup_photo).setOnClickListener(this);
        findViewById(R.id.signupBtn).setOnClickListener(this);
        findViewById(R.id.signupCancelBtn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.signup_camera){
            launchCamera();

        }else if(i == R.id.signup_photo){
            openGallery();


        }else if(i == R.id.signupBtn){
            creatAccount();

        }else if(i == R.id.signupCancelBtn){
            startActivity(new Intent(SignupActivity.this, MuseumListActivity.class));

        }

    }

    private void creatAccount() {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uID = user.getUid();
                            uploadAvatar();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, task.getException().toString(),
                                    LENGTH_SHORT).show();
                        }
                    }


                });
    }

    private void uploadAvatar() {

        StorageReference storageRef = mStorage.getReference();
        final StorageReference avatarRef = storageRef.child(uID);
        avatarRef.putBytes(byteArray)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    uploadProfile(downloadUrl);

                                }
                            });

                        }else{
                            Toast.makeText(SignupActivity.this,"Avatar Upload Failed", LENGTH_SHORT) .show();
                        }
                    }
                });
    }

    private void uploadProfile(String downloadUrl) {
        // Save username and bio in Firestore
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
        mDatabase.setFirestoreSettings(settings);

        Map<String, Object> user = new HashMap<>();
        user.put("username", mUsernameField.getText().toString());
        user.put("bio", mBioField.getText().toString());
        user.put("avatar", downloadUrl);

        mDatabase.collection("users")
                .document(uID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        startActivity(new Intent(SignupActivity.this, MuseumListActivity.class));
                        Toast.makeText(SignupActivity.this, "Account Created", LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }

    private boolean validateForm() {
        if(byteArray == null){
            Toast.makeText(this, "Avatar required.",LENGTH_SHORT).show();
            return false;
        }

        String mEmail = mEmailField.getText().toString();
        if (TextUtils.isEmpty(mEmail)) {
            mEmailField.setError("Required.");
            return false;
        } else {
            mEmailField.setError(null);
        }

        String mPassword = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordField.setError("Required.");
            return false;
        } else {
            mPasswordField.setError(null);
        }

        String mComfirm = mConfirmField.getText().toString();
        if (!mPassword.equals(mComfirm)) {
            mConfirmField.setError("Not match.");
            return false;
        } else {
            mConfirmField.setError(null);
        }

        String mUsername = mUsernameField.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameField.setError("Required.");
            return false;
        } else {
            mUsernameField.setError(null);
        }

        String mBio = mBioField.getText().toString();
        if (TextUtils.isEmpty(mBio)) {
            mBioField.setError("Required.");
            return false;
        } else {
            mBioField.setError(null);
        }

        return true;

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.circleCrop();

        Bitmap imageBitmap = null;

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

        }else if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
            
            Uri photoUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Glide.with(this)
                .load(imageBitmap)
                .apply(options)
                .into(mAvatarField);

        //Convert to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();

    }


}
