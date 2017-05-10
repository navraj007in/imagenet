package com.infodart.instaproject.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageException;
import com.infodart.instaproject.R;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.AGE_TOO_LOW;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORDS_DO_NOT_MATCH;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORD_EMPTY;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORD_NOT_STRONG;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORD_SHORT;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.SEX_NOT_SELECTED;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.EMAIL_EMPTY;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.INVALID_EMAIL;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORD_SHORT;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private static final int INSTA_PERMISSIONS_REQUEST = 101;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, InstaHome.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String uid = user.getUid();
                    Logger.d("onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Logger.d("onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUser())
                    performLogin();
                else
                    showErrorToast();
            }
        });
    }

    private void performLogin() {
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            mFirebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            loadProfile(mFirebaseUser.getUid());
                            Intent intent = new Intent(LoginActivity.this, InstaHome.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }
    Response.Listener<String> loginResponseListener= new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

        }
    };

    Response.ErrorListener loginErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private FirebaseAuth mAuth;
    SignupActivity.ERROR_CODES error_codes;

    private void showErrorToast() {
        switch (error_codes) {
            case  EMAIL_EMPTY:
                Utils.ShowToast(this,"Email can not be empty!!");
                return;
            case  INVALID_EMAIL:
                Utils.ShowToast(this,"Email is invalid!!");
                return;
            case  PASSWORD_EMPTY:
                Utils.ShowToast(this,"Password can not be empty!!");
                return;
            case  PASSWORD_SHORT:
                Utils.ShowToast(this,"Password is too short!!");
                return;
            case  PASSWORD_NOT_STRONG:
                Utils.ShowToast(this,"Password is not strong!!");
                return;
        }
    }
    String email ;
    String password;

    private boolean validateUser() {
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            error_codes = SignupActivity.ERROR_CODES.EMAIL_EMPTY;
            return false;
        }
        if(!Utils.emailValidator(email)){
            error_codes = SignupActivity.ERROR_CODES.INVALID_EMAIL;
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            error_codes =PASSWORD_EMPTY;
            return false;
        }

        if (password.length() < 6) {
            error_codes = PASSWORD_SHORT;
            return false;
        }


        return true;
    }

    private void loadProfile(String _id) {
        String url = String.format(Constants.GetServerURL() + Constants.URL_USERS +
                Constants.URL_USER_PROFILE,_id);
        InstaHome.followersMap = new HashMap<>();

        Logger.v("URL-"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        Utils.WriteToFile(Constants.FILE_PROFILE,response,LoginActivity.this);
                        Logger.d("Got Response - "+response+ "-Wrote To File");

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonFollowing = new JSONArray("following");
                            for(int i=0;i<jsonFollowing.length();i++) {
                                InstaHome.followersMap.put(jsonFollowing.getString(i),1);
                            }
                            Utils.WriteObjectToFile("followers",InstaHome.followersMap,LoginActivity.this);
                            //String followers = jsonObject.getString("");
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        error.printStackTrace();
                    }
                });

        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).
                getRequestQueue();

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        //queue.add(stringRequest);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INSTA_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

