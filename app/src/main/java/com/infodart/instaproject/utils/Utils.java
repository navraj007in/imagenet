package com.infodart.instaproject.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.infodart.instaproject.config.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by navrajsingh on 11/15/16.
 */

public class Utils {
    public static String TimestampTotDate(long timeStamp,String format){

        try{
            DateFormat sdf = new SimpleDateFormat(format);
            Date netDate = (new Date(timeStamp));

            Calendar c=Calendar.getInstance();
            c.getTimeInMillis();
            String cur_day=String.format("%te %B %tY",c,c,c); // This will give date like 22 February 2012

            c.setTimeInMillis(timeStamp);//set your saved timestamp
            String that_day=String.format("%te %B %tY",c,c,c); //

            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    public static Uri resIdToUri(Context context, int resId) {
        return Uri.parse(Constants.ANDROID_RESOURCE + context.getPackageName()
                + Constants.FORESLASH + resId);
    }

    public static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static String Random(int MAX_LENGTH) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    public static boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void RefreshGallery(Context context,File image) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String newPhotoPath = "file:" + image.getAbsolutePath(); // image is the created file image
        File file = new File(newPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        scanIntent.setData(contentUri);
        context.sendBroadcast(scanIntent);
    }

    public static void WriteObjectToFile(String fileName,Object object,Context context) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Object ReadObjectFromFile(String fileName,Context context) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            Object object = objectInputStream.readObject();
            return  object;
            //Map myNewlyReadInMap = (HashMap) objectInputStream.readObject();
            //objectInputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static void WriteToFile(String fileName,String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Logger.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void WriteToFile1(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput("feed.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Logger.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String ReadFromFile(Context context,String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.DAY_OF_YEAR) > b.get(Calendar.DAY_OF_YEAR)) {
            diff--;
        }
        return diff;
    }
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
    public static void ShowToast(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void WritePreference(Context context,String prefName,String prefValue) {
        SharedPreferences sharedPref = context.getSharedPreferences("followers",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefName, prefValue);
        editor.commit();
    }

    public static String ReadPreference(Context context,String prefName) {
        SharedPreferences sharedPref = context.getSharedPreferences("followers",Context.MODE_PRIVATE);

        return sharedPref.getString(prefName,"");
    }
}
