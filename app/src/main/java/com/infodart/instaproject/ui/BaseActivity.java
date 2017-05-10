package com.infodart.instaproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infodart.instaproject.R;


public class BaseActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    protected FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    public String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        } else {
            mUserId = mFirebaseUser.getUid();

            Log.e("Firebase",mUserId);

        }

    }
}
