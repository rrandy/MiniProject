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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;
import com.m2dl.miniproject.mini_projetandroid.business.DataToSend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Activité de validation et d'envoi de l'image et des métadonnées
 */
public class ActivityValidate extends ActionBarActivity implements LocationListener {
    /**
     * Gestion des données de l'application
     */
    private DataStorage storage;
    /**
     * Gestion de la géolocalisation
     */
    private LocationManager mLocationManager = null;

    /**
     * Position actuelle
     */
    private Location currentLocation = null;
    /**
     * Chemin de la photo
     */
    private String filePath = null;
    /**
     * Gestion de l'envoi des métadonnées
     */
    private DataToSend dataToSend;
    /**
     * Chemin du fichier des métadonnées
     */
    private String dataSendPath;
    /**
     * Constante correspondant à deux minutes
     */
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Listener d'envoi final de la photo et des métadonnées
     */
    private View.OnClickListener myListener = new View.OnClickListener() {
        public void onClick(View arg0) {
            sendMail();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        setContentView(R.layout.activity_validate);
        // Gestion des données
        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));
        // Chemin de la photo
        filePath = storage.getSharedPreference("photoPath");
        // Gestion des métadonnées à envoyer
        dataToSend = new DataToSend(this);

        // Obtient une référence au Location Manager du système
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Enregistre les listeners avec le Location Manager pour recevoir les mises à jour
        // de géolocalisation
        // Géolocalisation du réseau
        if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        // Géolocalisation GPS
        if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        currentLocation = this.getLastLocation();

        // ImageView de la photo
        ImageView imageView = (ImageView) findViewById(R.id.validateImageView);
        // Fichier photo
        File photo = new File(filePath);

        // Mettre la photo dans l'imageView
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

        // Sauver toutes les métadonnées
        saveMetadata();

        // Bouton de validation
        Button buttonComment = (Button) findViewById(R.id.validateButton);
        buttonComment.setOnClickListener(myListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mini_project, menu);
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

    /**
     * Sauvegarde des métadonnées, création et affichage du fichier créé dans le textView
     * pour validation
     */
    public void saveMetadata() {
        // Obtention des données, sauvegarde et création du fichier métadonnées
        currentLocation.getLatitude();
        dataToSend.setLocation(getCurrentLocation());
        dataSendPath = dataToSend.save();
        File file = new File(dataSendPath);

        // Affichage du fichier dans le textView pour validation
        TextView textView = (TextView) findViewById(R.id.validateTextView);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.setText(text);
    }

    /**
     * A chaque changement de localisation
     *
     * @param location localisation
     */
    @Override
    public void onLocationChanged(Location location) {
        // S'il n'y a pas de localisation précédente, on prend celle-ci
        if (currentLocation == null) {
            currentLocation = location;
        } else
            // Sinon on prend la nouvelle
            if (isBetterLocation(location, currentLocation)) {
                currentLocation = location;
            }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Obtention de la dernière géolocalisation connue
     * @return la dernière géolocalisation connue
     */
    private Location getLastLocation() {
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        long NetLocationTime = 0;

        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }

    /**
     * Retourne la géolocalisation courante
     *
     * @return La géolocalisation courante
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Détermine si une géolocalisation est meilleure que la géolocalisation actuelle
     *
     * @param location            La nouvelle géolocalisation à évaluer
     * @param currentBestLocation La géolocalisation actuelle
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        // Vérifie si la nouvelle géolocalisation est plus ou moins récente
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // Si la nouvelle géolocalisation est récente de plus de deux minutes de différence,
        // on la choisit car l'utilisateur a bougé
        if (isSignificantlyNewer) {
            return true;
            // Sinon, si elle est ancienne, on ne la prend choisit pas
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Vérifie si la nouvelle géolocalisation est plus ou moins précise
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Vérifie si les géolocalisations sont du même provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Détermine la qualité de la géolocalisation par rapport au temps et à la précision
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Vérifie si deux providers sont les mêmes
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Envoi de mail avec en pièce jointe la photo et le fichier de métadonnées
     */
    private void sendMail() {
        Resources resources = getResources();
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        // Informations supplémentaires du mail
        emailIntent.setType("image/*");
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{resources.getString(R.string.email_send_address)});
        emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.email_text));
        // Fichiers joints
        ArrayList<Uri> attachFiles = new ArrayList<Uri>();
        attachFiles.add(Uri.fromFile(new File(filePath)));
        attachFiles.add(Uri.fromFile(new File(dataSendPath)));
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachFiles);
        // Démarrage de l'intent mail
        startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.email_send_message)));
    }

}
