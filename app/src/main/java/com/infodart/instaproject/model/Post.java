package com.infodart.instaproject.model;

import com.infodart.instaproject.ui.InstaHome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by navraj.singh on 4/7/2017.
 */

public class Post {

    private String _id;
    private String text;
    private String type;
    private String url;
    private String timestamp;
    private boolean liked;
    private String comments;
    private String likes;
    private int likesCount;
    private int commentsCount;
    private String publisher;
    private String publisherName;
    private String publisherImage;
    public HashMap<String, Integer> likesMap;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherImage() {
        return publisherImage;
    }

    public void setPublisherImage(String publisherImage) {
        this.publisherImage = publisherImage;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public static Post ParsePost(JSONObject jsonObject) {
        Post post = new Post();
        String publisherData;

        try{
            if(jsonObject.has("text"))
                post.setText(jsonObject.getString("text"));
            if(jsonObject.has("likes"))
                post.setLikes(jsonObject.getString("likes"));
            if(jsonObject.has("_id"))
                post.set_id(jsonObject.getString("_id"));
            if(jsonObject.has("type"))
                post.setType(jsonObject.getString("type"));
            if(jsonObject.has("url"))
                post.setUrl(jsonObject.getString("url"));
            if(jsonObject.has("timestamp"))
                post.setTimestamp(jsonObject.getString("timestamp"));
            if(jsonObject.has("likesCount"))
                post.setLikesCount(jsonObject.getInt("likesCount"));
            if(jsonObject.has("commentsCount"))
                post.setCommentsCount(jsonObject.getInt("commentsCount"));
            if(jsonObject.has("comments"))
                post.setComments(jsonObject.getString("comments"));
            if(jsonObject.has("publisher"))
                post.setPublisher(jsonObject.getString("publisher"));
            if(jsonObject.has("publisher"))
                publisherData = jsonObject.getString("publisher_data") ;

            if(jsonObject.has("publisher_url"))
                post.setPublisherImage(jsonObject.getString("publisher_url"));
            if(jsonObject.has("publisher_name"))
                post.setPublisherName(jsonObject.getString("publisher_name"));
            Integer value=InstaHome.likesMap.get(post.get_id());
            if(value ==1)
                post.setLiked(true);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    public static ArrayList<Post> ParsePostList(String response) {
        ArrayList<Post> posts = new ArrayList<Post>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i <jsonArray.length();i++) {
                posts.add(ParsePost(jsonArray.getJSONObject(i)));
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    private String postedbyId;
    private String postedbyName;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostedbyId() {
        return postedbyId;
    }

    public void setPostedbyId(String postedbyId) {
        this.postedbyId = postedbyId;
    }

    public String getPostedbyName() {
        return postedbyName;
    }

    public void setPostedbyName(String postedbyName) {
        this.postedbyName = postedbyName;
    }

    public boolean isLiked() {
        return liked;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
