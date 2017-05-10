package com.infodart.instaproject.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
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
import com.infodart.instaproject.fragments.FeedsFragment;
import com.infodart.instaproject.fragments.UsersFragment;
import com.infodart.instaproject.interfaces.EndlessRecyclerViewScrollListener;
import com.infodart.instaproject.interfaces.OnLoadMoreListener;
import com.infodart.instaproject.model.Post;
import com.infodart.instaproject.model.Student;
import com.infodart.instaproject.model.Users;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.NPLayoutManager;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class InstaHome extends BaseActivity implements UsersFragment.OnListFragmentInteractionListener,FeedsFragment.OnFragmentInteractionListener {
    private FirebaseAuth auth;
    private EndlessRecyclerViewScrollListener scrollListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    findViewById(R.id.fragfeeds).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragusers).setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    findViewById(R.id.fragfeeds).setVisibility(View.GONE);
                    findViewById(R.id.fragusers).setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_notifications:
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

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(InstaHome.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Fresco.initialize(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public static HashMap<String, Integer> followersMap;
    public static HashMap<String, Integer> likesMap;

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

    @Override
    public void onListFragmentInteraction(Users item) {
        Intent intent = new Intent(this,WallActivity.class);
        intent.putExtra("userid",item.getUid());
        intent.putExtra("name",item.getName());
        startActivity(intent);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
