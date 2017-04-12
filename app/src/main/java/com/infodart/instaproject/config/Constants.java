package com.infodart.instaproject.config;

/**
 * Created by navraj.singh on 4/7/2017.
 */

public class Constants {
    public static String DEV_MODE_PRODUCTION ="Production";
    public static String DEV_MODE_DEVELOPMENT ="Development";
    public static String DEV_MODE =DEV_MODE_DEVELOPMENT;

    public static String SERVER_URL_PRODUCTION = "http://192.168.1.101:8000/";
    public static String SERVER_URL_DEVELOPMENT = "http://192.168.1.101:8000/";

    public static String URL_USERS = "users" ;
    public static String URL_POSTS = "posts" ;
    public static String URL_POSTS_LIKE = "/%s/like" ;
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
