package com.m2dl.miniproject.mini_projetandroid.controller;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.view.CommentDialog;

import java.io.File;
import java.io.IOException;


public class ActivityInterestPoint extends ActionBarActivity implements View.OnTouchListener {

    private CommentDialog commentDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_interest_point);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");

        Uri selectedImage = Uri.fromFile(photo);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media
                    .getBitmap(cr, selectedImage);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setOnTouchListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_interest_point, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_comment) {
            commentDialogbox();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL){
            return false;
        } else if (action == MotionEvent.ACTION_DOWN){
        float posx = event.getX();
        float posy = event.getY();
        Toast.makeText(getApplicationContext(), "X : " + posx + " Y : " + posy, Toast.LENGTH_LONG).show();

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder
                    .setMessage("Voulez-vous sélectionner ce point d'intérêt ?")
                    .setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener)
                    .show();

        }
        return true;
    }

    private void commentDialogbox(){
        commentDialog = new CommentDialog(this);
        commentDialog.show();
    }
}
