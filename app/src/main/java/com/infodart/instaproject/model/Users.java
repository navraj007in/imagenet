package com.infodart.instaproject.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by navraj.singh on 4/12/2017.
 */

public class Users {
    private String _id;
    private String name;
    private String uid;

    private String image;
    private String description;
    private String url;

    private String followers ;
    private String following;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public static ArrayList<Users> ParseJSONArray(String json) {
        ArrayList<Users> users = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                users.add(Users.ParseJSON(jsonObject.toString()));
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return users;
    }
    public static Users ParseJSON(String json) {
        Users users = new Users();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.has("_id"))
                users.set_id(jsonObject.getString("_id"));
            if(jsonObject.has("Name"))
                users.setName(jsonObject.getString("Name"));
            if(jsonObject.has("name"))
                users.setName(jsonObject.getString("name"));
            if(jsonObject.has("userId"))
                users.setUid(jsonObject.getString("userId"));
            if(jsonObject.has("description"))
                users.setDescription(jsonObject.getString("description"));
            if(jsonObject.has("image"))
                users.setImage(jsonObject.getString("image"));
            if(jsonObject.has("followersList"))
                users.setFollowers(jsonObject.getString("followersList"));
            if(jsonObject.has("following"))
                users.setFollowing(jsonObject.getString("following"));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return users;
    }
}
