package com.infodart.instaproject.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.infodart.instaproject.R;
import com.infodart.instaproject.adapters.PostAdapter;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.model.Post;
import com.infodart.instaproject.model.Profile;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WallActivity extends BaseActivity implements View.OnClickListener {
    String userId ;
    String name ;
    TextView txtName;
    TextView txtFollow;

    SimpleDraweeView imguser;

    RecyclerView recyclerView;
    ArrayList<Post> userPosts;
    String[] followers;
    private boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrolling);

        txtName = (TextView) findViewById(R.id.lblName);
        txtFollow = (TextView) findViewById(R.id.follow);
        imguser = (SimpleDraweeView) findViewById(R.id.imgUser);
        userId = getIntent().getStringExtra("userid");
        name = getIntent().getStringExtra("name");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        txtName.setText(name);
        txtFollow.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle(name);
        int isFollow = 0;

        Integer mapPos= InstaHome.followersMap.get(userId);
        if(mapPos!=null && mapPos==1)
            isFollow = 1;

        if(isFollow==1) {
            isFollowing = true;
            txtFollow.setText("Following");
        }
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.drawable.user1))
                .build();

        imguser.setImageURI(uri);

        loadProfile();
        //startActivity(new Intent(this,ScrollingActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void loadFollowers() {
            String profile = Utils.ReadFromFile(this,Constants.FILE_PROFILE);
            Profile userProfile = Profile.ParseJSON(profile);
            try {
                JSONArray jsonArray = new JSONArray(userProfile.getFollowing());
                followers = new String[jsonArray.length()];

                for(int i=0;i<jsonArray.length();i++) {
                    followers[i] = jsonArray.getString(i);
                    if(followers[i].equalsIgnoreCase(userId))
                        isFollowing = true;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

    }
    ProgressDialog progressDialog;
    private void followUser() {
        String url = String.format(Constants.GetServerURL() + Constants.URL_USERS + Constants.URL_USER_FOLLOW,userId);
        Logger.d(url);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid",mUserId);
        Map<String, String> headers = new HashMap<>();
        headers.put("uid",mUserId);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        Request request = VolleySingleton.getInstance(this).getStringRequest(Request.Method.GET,
                url,followResponseListener,followErrorListener,headers,params);
        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }
    private void loadProfile() {
        String url = String.format(Constants.GetServerURL() + Constants.URL_USERS + Constants.URL_USER_POSTS,userId);
        Logger.d(url);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid",mUserId);
        Map<String, String> headers = new HashMap<>();

        Request request = VolleySingleton.getInstance(this).getStringRequest(Request.Method.GET,
                                url,profileResponseListener,profileErrorListener,headers,params);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    Response.Listener<String> followResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("status")){
                    if(jsonObject.getString("status").equalsIgnoreCase("200"))
                        txtFollow.setText("Following");
                        InstaHome.followersMap.put(userId,1);
                        Utils.WriteObjectToFile("followers",InstaHome.followersMap,WallActivity.this);
                    }
                    else {
                        txtFollow.setText("Could not follow user");
                }


            }
            catch (Exception e){
                e.printStackTrace();
            }

            Logger.d(response);
        }
    };

    Response.ErrorListener followErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            Toast.makeText(WallActivity.this,"Could not follow user!!",Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }
    };


    Response.Listener<String> unfollowResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("status")){
                    if(jsonObject.getString("status").equalsIgnoreCase("200"))
                        txtFollow.setText("Follow");
                    InstaHome.followersMap.remove(userId);
                    Utils.WriteObjectToFile("followers",InstaHome.followersMap,WallActivity.this);
                }
                else {
                    txtFollow.setText("Could not unfollow user");
                }


            }
            catch (Exception e){
                e.printStackTrace();
            }

            Logger.d(response);
        }
    };

    Response.ErrorListener unfollowErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            Toast.makeText(WallActivity.this,"Could not unfollow user!!",Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }
    };

    Response.Listener<String> profileResponseListener= new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Logger.d(response);
            userPosts = Post.ParsePostList(response);
            adapter = new PostAdapter(WallActivity.this, userPosts,recyclerView,null);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(WallActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            recyclerView.setAdapter(adapter);
        }
    };

    Response.ErrorListener profileErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private void unfollowUser () {
        String url = String.format(Constants.GetServerURL() + Constants.URL_USERS + Constants.URL_USER_UNFOLLOW,userId);
        Logger.d(url);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid",mUserId);
        Map<String, String> headers = new HashMap<>();
        headers.put("uid",mUserId);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        Request request = VolleySingleton.getInstance(this).getStringRequest(Request.Method.GET,
                url,unfollowResponseListener,unfollowErrorListener,headers,params);
        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }
    PostAdapter adapter;

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.follow) {
            if(txtFollow.getText().toString().equalsIgnoreCase("Follow"))
                followUser();
            else
                unfollowUser();
        }
    }
}
