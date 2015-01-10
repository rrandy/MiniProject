package com.m2dl.miniproject.mini_projetandroid;

import android.location.Location;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Métadonnées de photos
 * Classe étendue de ExifInterface, rajoute des tags et la gestion de localisation
 */
public class ExifInterfaceExtended extends ExifInterface {
    /**
     * Type is String.
     */
    public static final String TAG_USER_COMMENT = "UserComment";
    /**
     * Type is String.
     * Warning : does not work
     */
    public static final String TAG_ARTIST = "Artist";
    /**
     * Type is String. Format is "YYYY:MM:DD HH:MM:SS"
     * Warning : does not work
     */
    public static final String TAG_DATETIME_ORIGINAL = "DateTimeOriginal";
    /**
     * Type is String. Format is "YYYY:MM:DD HH:MM:SS"
     */
    public static final String TAG_DATETIME_DIGITIZED = "DateTimeDigitized";

    /**
     * Reads Exif tags from the specified JPEG file.
     *
     * @param filename
     */
    public ExifInterfaceExtended(String filename) throws IOException {
        super(filename);
    }

    /**
     * Ajoute la géolocalisation aux métadonnées
     * @param loc localisation
     */
    public void setLocation(Location loc) {
        this.setAttribute(ExifInterface.TAG_GPS_LATITUDE, decimalsToMinutesSeconds(loc.getLatitude()));
        this.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, decimalsToMinutesSeconds(loc.getLongitude()));
        if (loc.getLatitude() > 0)
            this.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
        else
            this.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
        if (loc.getLongitude() > 0)
            this.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
        else
            this.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
    }

    /**
     * Convertit une localisation décimale en minutes secondes
     * @param decimals localisation décimale
     * @return localisation minutes secondes
     */
    String decimalsToMinutesSeconds(double decimals) {
        // Degrés
        decimals = decimals > 0 ? decimals : -decimals;
        String minutesSeconds = Integer.toString((int) decimals) + "/1,";
        // Minutes
        decimals = (decimals % 1) * 60;
        minutesSeconds = minutesSeconds + Integer.toString((int) decimals) + "/1,";
        // Secondes, au Millième pour plus de précision
        decimals = (decimals % 1) * 60000;
        minutesSeconds = minutesSeconds + Integer.toString((int) decimals) + "/1000";
        return minutesSeconds;
    }

    /**
     * Renvoie la localisation depuis les métadonnées
     * @return localisation
     */
    public Location getLocation() {
        String latitude = "";
        String latitudeRef = "";
        String longitude = "";
        String longitudeRef = "";

        latitude = this.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        longitude = this.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        latitudeRef = this.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        longitudeRef = this.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        double locationLatitude = minutesSecondsToDecimals(latitude);
        if (locationLatitude > 180.0) return null;
        double locationLongitude = minutesSecondsToDecimals(longitude);
        if (locationLongitude > 180.0) return null;

        locationLatitude = latitudeRef.contains("S") ? -locationLatitude : locationLatitude;
        locationLongitude = longitudeRef.contains("W") ? -locationLongitude : locationLongitude;

        Location loc = new Location("exifLocation");
        loc.setLatitude(locationLatitude);
        loc.setLongitude(locationLongitude);
        return loc;
    }

    /**
     * Convertit la localisation minutes secondes en décimale
     * @param minutesSeconds localisation minutes secondes
     * @return localisation décimale
     */
    double minutesSecondsToDecimals(String minutesSeconds) {
        double decimals = 999.0; // défaut en cas d'erreur
        try {
            String[] DMSs = minutesSeconds.split(",", 3);
            String minutesSecondsValues[] = DMSs[0].split("/", 2);
            decimals = (new Double(minutesSecondsValues[0]) / new Double(minutesSecondsValues[1]));
            minutesSecondsValues = DMSs[1].split("/", 2);
            decimals += ((new Double(minutesSecondsValues[0]) / new Double(minutesSecondsValues[1])) / 60);
            minutesSecondsValues = DMSs[2].split("/", 2);
            decimals += ((new Double(minutesSecondsValues[0]) / new Double(minutesSecondsValues[1])) / 3600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decimals;
    }

}
