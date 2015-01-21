package com.m2dl.miniproject.mini_projetandroid.controller;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;
import com.m2dl.miniproject.mini_projetandroid.view.CommentDialog;


public class ActivityComment extends ActionBarActivity {

    private String comment;
    private DataStorage sharePreference;

    private View.OnClickListener myListener = new View.OnClickListener() {
        public void onClick(View arg0) {
            EditText commentEditText = (EditText) findViewById(R.id.comment_editText);
            comment = commentEditText.getText().toString();
            sharePreference.newSharedPreference("comment", comment);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        sharePreference = new DataStorage(this, getString(R.string.sharedPreferencesFile));

        Button buttonComment = (Button) findViewById(R.id.comment_ok);
        buttonComment.setOnClickListener(myListener);

        //Deactivate button cancel for activity mode
        Button cancelButton = (Button) findViewById(R.id.comment_cancel);
        cancelButton.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_comment, menu);
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
}
