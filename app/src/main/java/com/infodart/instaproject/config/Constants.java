package com.infodart.instaproject.config;

/**
 * Created by navraj.singh on 4/7/2017.
 */

public class Constants {
    public static String DEV_MODE_PRODUCTION ="Production";
    public static String DEV_MODE_DEVELOPMENT ="Development";
    public static String DEV_MODE =DEV_MODE_DEVELOPMENT;

    public static String SERVER_URL_PRODUCTION = "http://192.168.1.102:8000/";
    public static String SERVER_URL_DEVELOPMENT = "http://192.168.1.102:8000/";

    public static String URL_USERS = "users" ;
    public static String URL_USER_POSTS = "/%s/posts" ;
    public static String URL_USER_FOLLOW = "/%s/follow" ;
    public static String URL_USER_UNFOLLOW = "/%s/unfollow" ;
    public static String URL_USER_PROFILE = "/%s/profile" ;
    public static String URL_POSTS = "posts" ;
    public static String URL_LIKES = "/%s/like/?op=%d&userid=%s" ;
    public static String URL_COMMENT = "posts/%s/comment" ;
    public static String URL_COMMENTS = "posts/%s/comments" ;
    public static String URL_REACTIONS = "posts/%s/reactions" ;
    public static String URL_POSTS_LIKE = "/%s/like" ;
    public static String FILE_FEEDS = "feeds.txt";
    public static String FILE_PROFILE = "profile.txt";
    public static int MIN_SIGNUP_AGE = 14;
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORESLASH = "/";

    public static String GetServerURL() {
        if(DEV_MODE.equalsIgnoreCase(DEV_MODE_DEVELOPMENT))
            return SERVER_URL_DEVELOPMENT;
        else
            return SERVER_URL_PRODUCTION;
    }
}
