package com.infodart.instaproject.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.infodart.instaproject.R;
import com.infodart.instaproject.adapters.CommentAdapter;
import com.infodart.instaproject.adapters.PostAdapter;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.model.Comment;
import com.infodart.instaproject.model.Post;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends BaseActivity implements View.OnClickListener{
    ImageButton cmdSendComment;
    RecyclerView recyclerView;
    EditText txtComment;
    String _id;
    ArrayList<Comment> comments;
    CommentAdapter adapter ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        _id = getIntent().getStringExtra("id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Comments");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
    }

    private void init() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cmdSendComment = (ImageButton) findViewById(R.id.cmdSendComment);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        txtComment = (EditText) findViewById(R.id.txtComment);
        cmdSendComment.setOnClickListener(this);

        /*comments = Comment.ParseComments(getIntent().getStringExtra("comments"));
        adapter = new CommentAdapter(this,comments,_id);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        Logger.e("Comments Count"+ comments.size());*/
        loadComments();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.cmdSendComment) {
            if(txtComment.getText().toString().length()>0)
                postComment();
        }
    }

    private void postComment() {
        String url = String.format(Constants.GetServerURL() + Constants.URL_COMMENT,_id);
        Logger.d(url);
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid",mUserId);
        params.put("comment",txtComment.getText().toString());
        params.put("name",mFirebaseUser.getDisplayName());
        if(mFirebaseUser.getPhotoUrl()!=null)
            params.put("image",mFirebaseUser.getPhotoUrl().toString());
        else
            params.put("image","");
        Map<String, String> headers = new HashMap<>();

        queue.add(VolleySingleton.getInstance(this).
                getStringRequest(Request.Method.POST, url,postCommentListener,postCommentErrorListener,headers,params));
    }

    Response.Listener<String> postCommentListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Utils.ShowToast(CommentsActivity.this,"Comment posted");
            Comment newComment = new Comment();
            newComment.setUid(mUserId);
            newComment.setText(txtComment.getText().toString());
            newComment.setName(mFirebaseUser.getDisplayName());
            comments.add(newComment);
            adapter.notifyDataSetChanged();
            txtComment.setText("");
            Logger.d(response);
        }
    };
    Response.ErrorListener postCommentErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Utils.ShowToast(CommentsActivity.this,"Error");
        }
    };
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadComments() {
        String url = String.format(Constants.GetServerURL() + Constants.URL_REACTIONS,_id);

        Logger.v("URL-"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        Logger.d("Got Response - "+response);

                        //mSwipeRefreshLayout.setRefreshing(false);
                        try {

                            comments = Comment.ParseComments(response);
                            adapter = new CommentAdapter(CommentsActivity.this, comments,_id);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(CommentsActivity.this);
                            mLayoutManager.setStackFromEnd(true);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setHasFixedSize(true);

                            recyclerView.setAdapter(adapter);


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

        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).
                getRequestQueue();


        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        //queue.add(stringRequest);
    }
}
