package com.m2dl.miniproject.mini_projetandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ActivityValidate extends ActionBarActivity implements LocationListener {
    /**
     * Vue texte
     */
    private TextView tv;
    /**
     * Vue image
     */
    private ImageView view;

    private LocationManager mLocationManager = null;

    private Location currentLocation = null;
    private String userName = null;
    private String filePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mini_project);

        userName = "Utilisateur";
        filePath = Environment.getExternalStorageDirectory() + "/Pic.jpg";

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Register the listeners with the Location Manager to receive location updates
        if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        currentLocation = this.getLastLocation();

        // Layout des vues de l'application
        LinearLayout lv = new LinearLayout(this);
        tv = new TextView(this);
        tv.append("Hello, Android, modèle du téléphone : " + Build.MODEL + "\n");
        view = new ImageView(this);

        tv.setMaxWidth(300);
        view.setMaxWidth(300);

        lv.addView(tv);
        lv.addView(view);
        setContentView(lv);

        this.saveMetadata();
        this.showMetadata();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","biodiversite@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Biodiversité");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Photo de la biodiversité");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));

        finish();

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

    public void saveMetadata() {
        ExifInterfaceExtended exif = null;
        try {
            exif = new ExifInterfaceExtended(filePath);
//            exif.setAttribute(ExifInterfaceExtended.TAG_USER_COMMENT, "Informations \n plante : ok");
            exif.setAttribute(ExifInterfaceExtended.TAG_MAKE, userName);
            String date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(Calendar.getInstance().getTime());
            exif.setAttribute(ExifInterface.TAG_DATETIME, date);
            exif.setAttribute(ExifInterfaceExtended.TAG_DATETIME_ORIGINAL, date);
            exif.setAttribute(ExifInterfaceExtended.TAG_DATETIME_DIGITIZED, date);
            exif.setLocation(this.getCurrentLocation());

            exif.saveAttributes();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showMetadata() {
        ExifInterfaceExtended exif = null;
        try {
            exif = new ExifInterfaceExtended(filePath);

            String exifInfos = "Exif information ---\n";
            exifInfos += getTagString(ExifInterface.TAG_DATETIME, exif);
            exifInfos += getTagString(ExifInterface.TAG_FLASH, exif);
            exifInfos += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
            exifInfos += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
            exifInfos += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
            exifInfos += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
            exifInfos += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
            exifInfos += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
            exifInfos += getTagString(ExifInterface.TAG_MAKE, exif);
            exifInfos += getTagString(ExifInterface.TAG_MODEL, exif);
            exifInfos += getTagString(ExifInterface.TAG_ORIENTATION, exif);
            exifInfos += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);

            exifInfos += getTagString(ExifInterfaceExtended.TAG_USER_COMMENT, exif);
            exifInfos += getTagString(ExifInterfaceExtended.TAG_ARTIST, exif);
            exifInfos += getTagString(ExifInterfaceExtended.TAG_DATETIME_ORIGINAL, exif);
            exifInfos += getTagString(ExifInterfaceExtended.TAG_DATETIME_DIGITIZED, exif);

            exifInfos += exif.getLocation();

            tv.setText(exifInfos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    @Override
    public void onLocationChanged(Location location) {

        if(currentLocation == null){
            currentLocation = location;
        } else if ( isBetterLocation(location, currentLocation) ) {
            currentLocation = location;
        }
        tv.append(currentLocation + "");

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
     * @return the last know best location
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

    public Location getCurrentLocation() {
        return currentLocation;
    }


    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
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
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


//    Le principal scénario d'utilisation est le suivant :
//    1. Prise d'une photo
//    ◦ En interne la photo est géolocalisée, datée et associée au pseudo de l'utilisateur
//    2. Possibilité de rajouter un point d’intérêt sur la photo
//    ◦ Permet de cibler ou de délimiter une plante ou un animal
//    3. Possibilité de suivre une clé de détermination (voir plus bas)
//    4. Possibilité de rajouter un commentaire
//    5. Validation
//    ◦ En interne on envoie alors l'image et les méta-données à un serveur


}
