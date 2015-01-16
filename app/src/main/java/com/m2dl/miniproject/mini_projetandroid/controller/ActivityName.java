package com.m2dl.miniproject.mini_projetandroid.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;


public class ActivityName extends ActionBarActivity {

    private DataStorage storage;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));
        username = storage.getSharedPreference("username");
        Toast.makeText(this, username, Toast.LENGTH_LONG).show();
        if (username != null) {
            Intent nextActivity = new Intent(this, ActivityMainMenu.class);
            startActivity(nextActivity);
        } else {
            setContentView(R.layout.activity_name);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveUsername(View v) {
        EditText usernameTxt = (EditText) findViewById(R.id.editUserName);
        storage.newSharedPreference("username", usernameTxt.getText().toString());

        Intent nextActivity = new Intent(this, ActivityMainMenu.class);
        startActivity(nextActivity);
    }
}
