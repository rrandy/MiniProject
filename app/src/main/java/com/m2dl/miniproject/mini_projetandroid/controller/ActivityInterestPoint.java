package com.m2dl.miniproject.mini_projetandroid.controller;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;
import com.m2dl.miniproject.mini_projetandroid.business.DataToSend;
import com.m2dl.miniproject.mini_projetandroid.view.CommentDialog;

import java.io.File;
import java.io.IOException;


public class ActivityInterestPoint extends ActionBarActivity implements View.OnTouchListener {

    private CommentDialog commentDialog;

    private DataStorage storage;

    private float imagePointX;
    private float imagePointY;
    private float imageWidthX;
    private float imageHeightY;

    private float screenPointX;
    private float screenPointY;
    private float screenWidthX;
    private float screenHeightY;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));

        // Mettre ces options avant la création de l'instance pour ne pas provoquer d'erreurs

        // Suppression de la barre de notification
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_activity_interest_point);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));
        photoPath = storage.getSharedPreference("photoPath");
        File photo = new File(photoPath);

        // Obtenir les dimensions de l'image
        this.setImageDimensions(photo.getAbsolutePath());

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
        } else if (id == R.id.action_validate) {
            Intent intent = new Intent(this, ActivityValidate.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setImageDimensions(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);
        imageWidthX = options.outWidth;
        imageHeightY = options.outHeight;
    }

    public void setScreenDimensions() {
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidthX = metrics.widthPixels;
        screenHeightY = metrics.heightPixels;
    }

    public void setImagePoint() {
        imagePointX = (screenPointX * imageWidthX) / screenWidthX;
        imagePointY = (screenPointY * imageHeightY) / screenHeightY;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        } else if (action == MotionEvent.ACTION_DOWN) {
            screenPointX = event.getX();
            screenPointY = event.getY();



            // Obtenir les dimensions de l'écran
            this.setScreenDimensions();
            // Calculer le point d'intérêt proportionnellement à l'image
            this.setImagePoint();

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            storage.newSharedPreference("interestPointX", String.valueOf(imagePointX));
                            storage.newSharedPreference("interestPointY", String.valueOf(imagePointY));
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder
                    .setMessage("Voulez-vous sélectionner ce point d'intérêt ?"
                                    + "\n" + "(" + imagePointX + ";" + imagePointY + ")")
                    .setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener)
                    .show();
        }
        return true;
    }

    private void commentDialogbox() {
        commentDialog = new CommentDialog(this);
        commentDialog.show();
    }
}
