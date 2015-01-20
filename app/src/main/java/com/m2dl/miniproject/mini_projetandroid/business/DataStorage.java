package com.m2dl.miniproject.mini_projetandroid.business;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.m2dl.miniproject.mini_projetandroid.controller.ActivityName;

public class DataStorage {

    private Context context;
    private String settingsFilename;
    private SharedPreferences settings;

    public DataStorage(Context activityContext, String filename) {
        context = activityContext;
        settingsFilename = filename;
        settings = context.getSharedPreferences(settingsFilename, Context.MODE_PRIVATE);
    }

    public void newSharedPreference(String key, String value) {
        settings.edit().putString(key, value).apply();
        //settings.edit().commit();
    }

    public String getSharedPreference(String key) {
        return settings.getString(key, null);
    }

    public void clearPreferences() {
        settings.edit().clear().apply();
    }
}
