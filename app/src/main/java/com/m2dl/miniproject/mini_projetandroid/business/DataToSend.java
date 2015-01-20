package com.m2dl.miniproject.mini_projetandroid.business;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.util.FileKeyValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kana on 17/01/15.
 */
public class DataToSend {

    private String filepath;
    private Context context;


    private String interestPointX;
    private String interestPointY;
    private String comment;
    private String determiningKey;
    private FileKeyValue fileToSave;
    private Location location;
    private DataStorage dataStorage;

    public DataToSend(Context context){
        this.context = context;
        filepath = Environment.getExternalStorageDirectory() + "/" +
                context.getResources().getString(R.string.email_data_send_filename);

        fileToSave = new FileKeyValue( filepath );
        dataStorage = new DataStorage(context, context.getResources().getString(R.string.sharedPreferencesFile));

        interestPointX = getInterestPointXFromSharePreference();
        interestPointY = getInterestPointYFromSharePreference();
        comment = getCommentFromSharePreference();
        determiningKey = getDeterminingKeyFromSharePreference();
    }

    public DataToSend(Context context, Location location, String interestPointX, String interestPointY, String comment){
        this.interestPointX = interestPointX;
        this.interestPointY = interestPointY;
        this.comment = comment;
        filepath = Environment.getExternalStorageDirectory() + "/" +
                context.getResources().getString(R.string.email_data_send_filename);

        fileToSave = new FileKeyValue( filepath );
        this.location = location;
        dataStorage = new DataStorage(context, context.getResources().getString(R.string.sharedPreferencesFile));
    }

    /**
     * save file
     * @return  directory of saved file
     */
    public String save(){
        try {
            fileToSave.put("interest_point_x", interestPointX );
            fileToSave.put("interest_point_y", interestPointY );
            fileToSave.put("comment", comment);
            fileToSave.put("date", getCurrentDate());
            fileToSave.put("username", getUsername());
            fileToSave.put("gps_latitute", location.getLatitude() + "");
            fileToSave.put("gps_longtitute", location.getLongitude() + "");
            fileToSave.put("determiningKey",determiningKey);
        } catch (IOException e) {
            Log.e("file not found", e.getMessage() );
        }
        return  filepath;
    }

    private String getUsername(){
        return dataStorage.getSharedPreference("username");
    }

    private String getCurrentDate(){
        String currentDate;
        currentDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(Calendar.getInstance().getTime());
        return  currentDate;
    }


    public void setInterestPointX(String interestPointX) {
        this.interestPointX = interestPointX;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setInterestPointY(String interestPointY) {
        this.interestPointY = interestPointY;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCommentFromSharePreference(){
        return dataStorage.getSharedPreference("comment");
    }

    public String getInterestPointXFromSharePreference(){
        return dataStorage.getSharedPreference("interestPointX");
    }

    public String getInterestPointYFromSharePreference(){
        return dataStorage.getSharedPreference("interestPointY");
    }

    public String getDeterminingKeyFromSharePreference(){
        return  dataStorage.getSharedPreference("determiningKey");
    }

}
