/*
 * Copyright (c) 2015 Marine Carrara, Akana Mao, Randy Ratsimbazafy
 *
 * This file is part of Biodiversity.
 *
 * Biodiversity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Biodiversity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Biodiversity.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.m2dl.miniproject.mini_projetandroid.controller;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;
import com.m2dl.miniproject.mini_projetandroid.view.CommentDialog;

import java.io.File;
import java.io.IOException;

/**
 * Activité de point d'intérêt
 * Possibilité de rajouter un point d’intérêt sur la photo
 * Permet de cibler ou de délimiter une plante ou un animal
 */
public class ActivityInterestPoint extends ActionBarActivity implements View.OnTouchListener {

    /**
     * Popup pour ajouter un commentaire
     */
    private CommentDialog commentDialog;
    /**
     * Gestion des données dans l'application
     */
    private DataStorage storage;
    /**
     * Point d'intérêt sur l'image, coordonnée X
     */
    private float imagePointX;
    /**
     * Point d'intérêt sur l'image, coordonnée Y
     */
    private float imagePointY;
    /**
     * Dimension de l'image, largeur
     */
    private float imageWidthX;
    /**
     * Dimension de l'image, hauteur
     */
    private float imageHeightY;
    /**
     * Point d'intérêt sur l'image, coordonnée X
     */
    private float screenPointX;
    /**
     * Point d'intérêt sur l'image, coordonnée Y
     */
    private float screenPointY;
    /**
     * Dimension de l'écran, largeur
     */
    private float screenWidthX;
    /**
     * Dimension de l'écran, hauteur
     */
    private float screenHeightY;
    /**
     * Chemin de la photo
     */
    private String photoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Mettre ces options avant la création de l'instance pour ne pas provoquer d'erreurs
        // Suppression de la barre de notification
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        // Suppression de l'ActionBar
        getSupportActionBar().hide();

        setContentView(R.layout.activity_activity_interest_point);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        // Données de l'application
        storage = new DataStorage(this, getResources().getString(R.string
                .sharedPreferencesFile));

        // Chemin de la photo
        photoPath = storage.getSharedPreference(getString(R.string.preferencePhotoPath));

        if (photoPath == null) {
            Toast.makeText(this.getApplicationContext(),
                    "Aucune photo prise", Toast.LENGTH_LONG).show();
            finish();

        } else {
            File photo = new File(photoPath);

            // Obtenir les dimensions de l'image
            this.setImageDimensions(photo.getAbsolutePath());

            Uri selectedImage = Uri.fromFile(photo);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;
            try {
                // Afficher l'image sur l'imageView
                bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, selectedImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setOnTouchListener(this);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_interest_point,
                menu);
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

    /**
     * Définir les dimensions de l'image
     * @param imagePath chemin de l'image
     */
    public void setImageDimensions(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);
        imageWidthX = options.outWidth;
        imageHeightY = options.outHeight;
    }

    /**
     * Définir les dimensions de l'écran
     */
    public void setScreenDimensions() {
        DisplayMetrics metrics = getApplicationContext().getResources()
                .getDisplayMetrics();
        screenWidthX = metrics.widthPixels;
        screenHeightY = metrics.heightPixels;
    }

    /**
     * Calculer le point d'intérêt proportionnellement à l'image et l'écran
     */
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

            // Comportement de la Popup de validation du point d'intérêt
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface
                    .OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            storage.newSharedPreference(
                                    "interestPointX", String.valueOf(
                                            imagePointX));
                            storage.newSharedPreference(
                                    "interestPointY", String.valueOf(
                                            imagePointY));
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            // Création de la Popup de validation du point d'intérêt
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder
                    .setMessage(
                            "Voulez-vous sélectionner ce point d'intérêt ?" +
                                    "\n" + "(" + imagePointX + ";" + imagePointY + ")")
                    .setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener)
                    .show();
        }
        return true;
    }

    /**
     * Popup d'ajout de commentaire
     */
    private void commentDialogbox() {
        commentDialog = new CommentDialog(this);
        commentDialog.show();
    }
}