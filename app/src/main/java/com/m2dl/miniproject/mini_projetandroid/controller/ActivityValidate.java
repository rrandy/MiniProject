package com.m2dl.miniproject.mini_projetandroid.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;
import com.m2dl.miniproject.mini_projetandroid.business.DataToSend;
import com.m2dl.miniproject.mini_projetandroid.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ActivityValidate extends ActionBarActivity implements LocationListener {
    /**
     * Vue image
     */
    private ImageView view;
    private DataStorage storage;
    private LocationManager mLocationManager = null;

    private Location currentLocation = null;
    private String filePath = null;
    private DataToSend dataToSend;
    private String dataSendPath;


    private View.OnClickListener myListener = new View.OnClickListener() {
        public void onClick(View arg0) {
        sendMail();
        finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        filePath = Environment.getExternalStorageDirectory() + "/Pic.jpg";
//        setContentView(R.layout.activity_mini_project);
        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));

        filePath = storage.getSharedPreference("photoPath");

        dataToSend = new DataToSend(this);
        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Register the listeners with the Location Manager to receive location updates
        if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        currentLocation = this.getLastLocation();

        ImageView imageView = (ImageView) findViewById(R.id.validateImageView);


        File photo = new File(filePath);


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


        saveMetadata();

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

    public void saveMetadata() {
        currentLocation.getLatitude();
        dataToSend.setLocation(getCurrentLocation());
        dataSendPath = dataToSend.save();

        TextView textView = (TextView) findViewById(R.id.validateTextView);

        File file = new File(dataSendPath);
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

    @Override
    public void onLocationChanged(Location location) {

        if(currentLocation == null){
            currentLocation = location;
        } else if ( isBetterLocation(location, currentLocation) ) {
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

    private void sendMail(){
        Resources resources = getResources();
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("image/*");
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {resources.getString(R.string.email_send_address)} );
        emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.email_text));
        ArrayList<Uri> attachFiles = new ArrayList<Uri>();
        attachFiles.add(Uri.fromFile(new File(filePath)));
        attachFiles.add(Uri.fromFile(new File(dataSendPath)));
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachFiles);
        startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.email_send_message)));
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
