package com.infodart.instaproject.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.infodart.instaproject.R;
import com.infodart.instaproject.adapters.PostAdapter;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.model.Post;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;

public class InstaHome extends BaseActivity {
    int pageNum  = 1;
    private TextView mTextMessage;
    private FirebaseAuth auth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        auth = FirebaseAuth.getInstance();
        loadLikesMap();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(InstaHome.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        init();
        Fresco.initialize(this);
        readSavedFeeds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_camera:
                Intent intent = new Intent(this,PhotoActivity.class);
                startActivityForResult(intent,101);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<Post> posts ;
    PostAdapter adapter;
    RecyclerView recyclerView;
    private void readSavedFeeds() {
        String feeds = Utils.ReadFromFile(this);
        try {

            posts = Post.ParsePostList(feeds);

            adapter = new PostAdapter(InstaHome.this, posts,recyclerView,likesMap);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(InstaHome.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);

            recyclerView.setAdapter(adapter);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadPosts() {
        long timestamp = System.currentTimeMillis();
        String params = "/?timestamp=%d&page=%d";
        String url = String.format(Constants.GetServerURL() + Constants.URL_POSTS+ params,timestamp,pageNum);

        Logger.v("URL-"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        //Log.e("SexApp",response);
                        //mSwipeRefreshLayout.setRefreshing(false);
                        try {

                            posts = Post.ParsePostList(response);
                            adapter = new PostAdapter(InstaHome.this, posts,recyclerView,likesMap);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(InstaHome.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setHasFixedSize(true);

                            recyclerView.setAdapter(adapter);
                            Utils.WriteToFile(response,InstaHome.this);
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
                        //mSwipeRefreshLayout.setRefreshing(false);
                        error.printStackTrace();
                    }
                });

        RequestQueue queue = VolleySingleton.getInstance(InstaHome.this.getApplicationContext()).
                getRequestQueue();


        VolleySingleton.getInstance(InstaHome.this).addToRequestQueue(stringRequest);
        queue.add(stringRequest);
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
       // mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        loadPosts();
        /*mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosts();
            }
        });*/

    }
    public static HashMap<String, Integer> likesMap;
    private void loadLikesMap() {

        SharedPreferences pref = getSharedPreferences("likes", Context.MODE_PRIVATE);
        likesMap = (HashMap<String, Integer>) pref.getAll();
        for (String s : likesMap.keySet()) {
            Integer value=likesMap.get(s);
            //Use Value
        }
    }
    public class MapWrapper {
        private HashMap<Integer, String> myMap;

        public HashMap<Integer, String> getMyMap() {
            return myMap;
        }

        public void setMyMap(HashMap<Integer, String> myMap) {
            this.myMap = myMap;
        }

        // getter and setter for 'myMap'
    }


}
