package com.infodart.instaproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.infodart.instaproject.R;
import com.infodart.instaproject.model.Post;
import com.infodart.instaproject.utils.TimeAgo;

public class PostActivity extends AppCompatActivity {
    String postJson;
    Post post;

    public TextView name;
    public TextView text;
    public TextView timestamp;
    public LinearLayout layoutPost;
    public NetworkImageView imgStar;
    public NetworkImageView imgUrl;
    public SimpleDraweeView imgFresco;
    public ImageView imgFrescoStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postJson = getIntent().getStringExtra("post");
        post = new Gson().fromJson(postJson,Post.class);
        Fresco.initialize(this);

        init();
    }

    private void init() {
        this.imgFresco = (SimpleDraweeView) findViewById(R.id.sdvImage);
        this.imgFrescoStar = (ImageView) findViewById(R.id.sdvStar);

        this.name = (TextView) findViewById(R.id.lblStarName);
        this.timestamp = (TextView) findViewById(R.id.timestamp);

        this.text = (TextView) findViewById(R.id.text);

        text.setText(Html.fromHtml(post.getText()));
        //mainHolder.text.setText(Utils.TimestampTotDate(Long.parseLong(model.getTimestamp()),""));
        timestamp.setText(TimeAgo.getTimeAgo(Long.parseLong(post.getTimestamp()),this));

        Linkify.addLinks(text, Linkify.ALL);
        Uri imageUri = Uri.parse(post.getUrl());
        imgFresco.setImageURI(imageUri);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
