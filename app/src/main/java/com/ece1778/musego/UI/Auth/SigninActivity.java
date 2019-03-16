package com.ece1778.musego.UI.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ece1778.musego.BaseActivity;
import com.ece1778.musego.MainActivity;
import com.ece1778.musego.Manager.FirebaseManager;
import com.ece1778.musego.Model.User;
import com.ece1778.musego.R;
import com.ece1778.musego.UI.Museum.MuseumListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class SigninActivity extends BaseActivity {

    private static final String TAG = "SIGNIN";
    private FirebaseAuth mAuth;
    private FirebaseManager firebaseManager;

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mText;
    private Button signinBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseManager = new FirebaseManager(this);

        initView();

    }

    private void initView() {

        mEmailField = findViewById(R.id.signin_email);
        mPasswordField = findViewById(R.id.signin_password);

        signinBtn = (Button) findViewById(R.id.signinBtn);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                if(checkInput(email, password)){
                    signIn(email, password);
                }
            }
        });

        SpannableStringBuilder spanText = createSpannable();
        mText = findViewById(R.id.signin_text);
        mText.setMovementMethod(LinkMovementMethod.getInstance());
        mText.setText(spanText, TextView.BufferType.SPANNABLE);

    }

    private SpannableStringBuilder createSpannable() {

        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("Don’t have an account? Sign up or Skip");
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));

            }
            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(getColor(R.color.darkGreen));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        },23,30,0 );

        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SigninActivity.this, MuseumListActivity.class));

            }
            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(getColor(R.color.darkGreen));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        },34,38,0 );

        return spanText;
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(SigninActivity.this, "Authentication Success",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SigninActivity.this, MuseumListActivity.class));


                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkInput(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            return false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            return false;
        } else {
            mPasswordField.setError(null);
        }

        return true;

    }
}
