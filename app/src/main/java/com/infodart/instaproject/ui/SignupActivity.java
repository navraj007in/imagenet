package com.infodart.instaproject.ui;

import android.app.DownloadManager;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.infodart.instaproject.R;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.AGE_TOO_LOW;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORDS_DO_NOT_MATCH;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORD_EMPTY;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.PASSWORD_SHORT;
import static com.infodart.instaproject.ui.SignupActivity.ERROR_CODES.SEX_NOT_SELECTED;

public class SignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private EditText inputEmail, inputPassword,inputName,inputDOB,txtCountry,inputRePassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String email;
    private String password;
    private String repassword;
    RadioButton rdbMale;
    RadioButton rdbFemale;
    ERROR_CODES error_codes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputRePassword = (EditText) findViewById(R.id.repassword);
        inputName = (EditText) findViewById(R.id.name);
        inputDOB = (EditText) findViewById(R.id.dob);
        txtCountry = (EditText) findViewById(R.id.countryField);
        rdbFemale = (RadioButton) findViewById(R.id.rdbFemale);
        rdbMale = (RadioButton) findViewById(R.id.rdbMale);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    Calendar now = Calendar.getInstance();
                    String dob = inputDOB.getText().toString();
                    int year = now.get(Calendar.YEAR);
                    int month = now.get(Calendar.MONTH);
                    int day = now.get(Calendar.DAY_OF_MONTH);
                    if(inputDOB.getText().toString().length()>0)
                    {
                        year = Integer.parseInt(dob.substring(0,4));
                        month = Integer.parseInt(dob.substring(5,7))-1;
                        day = Integer.parseInt(dob.substring(8,10));
                    }
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            SignupActivity.this,
                            year,
                            month,
                            day
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            }
        });

        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CountryPicker picker = CountryPicker.newInstance("Select Country");
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        txtCountry.setText(name);
                        picker.dismiss();
                    }
                });
            }
        });
        txtCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    final CountryPicker picker = CountryPicker.newInstance("Select Country");
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                            // Implement your code here
                            txtCountry.setText(name);
                            picker.dismiss();
                        }
                    });

                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUser())
                    signupUser();
                else
                    showErrorToast();

            }
        });
    }

    private void showErrorToast() {
        switch (error_codes) {
            case NAME_EMPTY:
                Utils.ShowToast(this,"Name can not be empty!!");
                return;
            case  EMAIL_EMPTY:
                Utils.ShowToast(this,"Email can not be empty!!");
                return;
            case  INVALID_EMAIL:
                Utils.ShowToast(this,"Email is invalid!!");
                return;
            case  DOB_EMPTY:
                Utils.ShowToast(this,"DOB can not be empty!!");
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
            case  PASSWORDS_DO_NOT_MATCH:
                Utils.ShowToast(this,"Passwords do not match!!");
                return;
            case  SEX_NOT_SELECTED:
                Utils.ShowToast(this,"Sex is not selected!!");
                return;
            case  DOB_INVALID:
                Utils.ShowToast(this,"DOB is invalid!!");
                return;
            case  AGE_TOO_LOW:
                Utils.ShowToast(this,String.format("Age can not be less than %d.!!",Constants.MIN_SIGNUP_AGE));
                return;
        }
    }

    private boolean validateUser() {
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        if(TextUtils.isEmpty(inputName.getText().toString())) {
            error_codes = ERROR_CODES.NAME_EMPTY;
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            error_codes = ERROR_CODES.EMAIL_EMPTY;
            return false;
        }
        if(!Utils.emailValidator(email)){
            error_codes = ERROR_CODES.INVALID_EMAIL;
            return false;
        }
        if(TextUtils.isEmpty(inputDOB.getText().toString())) {
            error_codes = ERROR_CODES.DOB_EMPTY;
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
        if(!inputRePassword.getText().toString().contentEquals(inputPassword.getText().toString())){
            error_codes = PASSWORDS_DO_NOT_MATCH;
            return false;
        }
        if(!(rdbFemale.isChecked() || rdbMale.isChecked())){
            error_codes = SEX_NOT_SELECTED;
            return false;
        }


        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Date todayDate ;
        Date dobDate;
        try {
            dobDate = df.parse(inputDOB.getText().toString());
            todayDate = df.parse(Utils.TimestampTotDate(System.currentTimeMillis() ,"yyyy-mm-dd"));
            int years = Utils.getDiffYears(dobDate,todayDate);
            if(years<Constants.MIN_SIGNUP_AGE) {
                error_codes = AGE_TOO_LOW;
                return false;
            }

        } catch (ParseException e) {
            String msg = e.getMessage();
            Logger.e(msg);
            e.printStackTrace();
        }

        return true;
    }

    private void signupUser() {
        final String sex;
        if(rdbFemale.isChecked())
            sex = "F";
        else
            sex = "M";
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();
        String url = Constants.GetServerURL() + Constants.URL_USERS;
        RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email",inputEmail.getText().toString());
        params.put("name",inputName.getText().toString());
        params.put("dob",inputDOB.getText().toString());
        params.put("sex",sex);
        params.put("country",txtCountry.getText().toString());
        params.put("password",inputPassword.getText().toString());
        params.put("profileimg","");

        Map<String, String> headers = new HashMap<>();

        queue.add(VolleySingleton.getInstance(SignupActivity.this).
                getStringRequest(Request.Method.POST, url,loginResponseListener,loginErrorListener,headers,params));

    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String day = String.valueOf(dayOfMonth);
        String month = String.valueOf(monthOfYear+1);

        if(dayOfMonth<10)
            day = "0"+dayOfMonth;
        if(monthOfYear<9)
            month = "0" + (monthOfYear+1);
        else
            month = String.valueOf(monthOfYear+1);

        String date = year + "-" + month+"-" + day;

        inputDOB.setText(date);
    }

    Response.Listener<String> loginResponseListener= new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("status").equalsIgnoreCase("200")) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                            Toast.makeText(SignupActivity.this, getString(R.string.auth_failed),
                                                    Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(SignupActivity.this, InstaHome.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(SignupActivity.this, jsonObject.getString("message"),
                            Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            Logger.e(response);
        }
    };

    Response.ErrorListener loginErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressBar.setVisibility(View.GONE);

            error.printStackTrace();

        }
    };
    public enum ERROR_CODES {
        INVALID_EMAIL,
        NAME_EMPTY,
        EMAIL_EMPTY,
        PASSWORD_EMPTY,
        PASSWORD_NOT_STRONG,
        PASSWORD_SHORT,
        PASSWORDS_DO_NOT_MATCH,
        SEX_NOT_SELECTED,
        DOB_EMPTY,
        DOB_INVALID,
        AGE_TOO_LOW
    }
}
