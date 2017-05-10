package com.infodart.instaproject.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.infodart.instaproject.R;
import com.infodart.instaproject.adapters.CommentAdapter;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.model.Comment;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.VolleySingleton;

public class CommentActivity extends AppCompatActivity {
    String _id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        _id = getIntent().getStringExtra("id");
        loadComments();
    }

    private void loadComments() {
        String url = String.format(Constants.GetServerURL() + Constants.URL_COMMENTS,_id);

        Logger.v("URL-"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        Logger.d("Got Response - "+response);

                        //mSwipeRefreshLayout.setRefreshing(false);
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

        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).
                getRequestQueue();


        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        queue.add(stringRequest);
    }
}
