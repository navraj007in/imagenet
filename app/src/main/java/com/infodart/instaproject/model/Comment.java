package com.infodart.instaproject.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by navraj.singh on 4/25/2017.
 */

public class Comment {
    private String _id;
    private String uid;
    private String posted;
    private String text;
    private String name;
    private String url;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static ArrayList<Comment> ParseComments(String response) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for(int i=0;i<jsonArray.length();i++)
                comments.add(ParseComment(jsonArray.getString(i)));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return comments;
    }
    public static Comment ParseComment(String response) {
        Comment comment = new Comment();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("name"))
                comment.setName(jsonObject.getString("name"));
            if(jsonObject.has("text"))
                comment.setText(jsonObject.getString("text"));
            if(jsonObject.has("posted"))
                comment.setPosted(jsonObject.getString("posted"));
            if(jsonObject.has("url"))
                comment.setUrl(jsonObject.getString("url"));
            if(jsonObject.has("id"))
                comment.setUid(jsonObject.getString("id"));

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return comment;
    }
}
