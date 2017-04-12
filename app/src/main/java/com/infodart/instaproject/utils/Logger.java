package com.infodart.instaproject.utils;

import com.infodart.instaproject.config.Constants;
import android.util.Log;
/**
 * Created by navraj.singh on 4/7/2017.
 */

public class Logger {
    public static String TAG = "InstaLogs";
    public static void i(String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.i(TAG,message);
    }
    public static void i(String TAG,String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.i(TAG,message);
    }

    public static void v(String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.v(TAG,message);
    }
    public static void v(String TAG,String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.v(TAG,message);
    }

    public static void d(String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.d(TAG,message);
    }
    public static void d(String TAG,String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.d(TAG,message);
    }

    public static void w(String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.w(TAG,message);
    }
    public static void w(String TAG,String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.w(TAG,message);
    }

    public static void e(String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.e(TAG,message);
    }
    public static void e(String TAG,String message) {
        if(Constants.DEV_MODE.equalsIgnoreCase(Constants.DEV_MODE_DEVELOPMENT))
            Log.e(TAG,message);
    }

}
