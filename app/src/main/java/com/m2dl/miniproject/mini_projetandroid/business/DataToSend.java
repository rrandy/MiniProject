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

package com.m2dl.miniproject.mini_projetandroid.business;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.util.FileKeyValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Classe de gestion des métadonnées à envoyer
 * Created by kana on 17/01/15.
 */
public class DataToSend {
    /**
     * Chemin du fichier des métadonnées
     */
    private String filepath;
    /**
     * Point d'intérêt, coordonée X
     */
    private String interestPointX;
    /**
     * Point d'intérêt, coordonée Y
     */
    private String interestPointY;
    /**
     * Commentaire
     */
    private String comment;
    /**
     * Clé de détermination
     */
    private String determiningKey;
    /**
     * Fichier clé valeur à sauver
     */
    private FileKeyValue fileToSave;
    /**
     * Géolocalisation
     */
    private Location location;
    /**
     * Gestion des données de l'application
     */
    private DataStorage dataStorage;

    /**
     * Constructeur
     *
     * @param context contexte
     */
    public DataToSend(Context context) {
        // Création du fichier de métadonnées
        filepath = Environment.getExternalStorageDirectory() + "/" +
                context.getResources().getString(R.string.email_data_send_filename);
        fileToSave = new FileKeyValue(filepath);

        // Obtention des informations de l'application
        dataStorage = new DataStorage(context, context.getResources().getString(R.string.sharedPreferencesFile));
        interestPointX = getInterestPointXFromSharePreference();
        interestPointY = getInterestPointYFromSharePreference();
        comment = getCommentFromSharePreference();
        determiningKey = getDeterminingKeyFromSharePreference();
    }

    /**
     * Constructeur
     *
     * @param context        contexte
     * @param location       géolocalisation
     * @param interestPointX point d'intérêt coordonée X
     * @param interestPointY point d'intérêt coordonée Y
     * @param comment        commentaire
     */
    public DataToSend(Context context, Location location, String interestPointX, String interestPointY, String comment) {
        // Création du fichier de métadonnées
        filepath = Environment.getExternalStorageDirectory() + "/" +
                context.getResources().getString(R.string.email_data_send_filename);
        fileToSave = new FileKeyValue(filepath);

        // Obtention des informations de l'application
        dataStorage = new DataStorage(context, context.getResources().getString(R.string.sharedPreferencesFile));
        this.interestPointX = interestPointX;
        this.interestPointY = interestPointY;
        this.comment = comment;
        this.location = location;
    }

    /**
     * Sauvegarde du fichier de métadonnées
     *
     * @return Chemin du fichier de métadonnées
     */
    public String save() {
        try {
            fileToSave.put("interest_point_x", interestPointX);
            fileToSave.put("interest_point_y", interestPointY);
            fileToSave.put("comment", comment);
            fileToSave.put("date", getCurrentDate());
            fileToSave.put("username", getUsername());
            fileToSave.put("gps_latitute", location.getLatitude() + "");
            fileToSave.put("gps_longtitute", location.getLongitude() + "");
            fileToSave.put("determining_key", determiningKey);
        } catch (IOException e) {
            Log.e("file not found", e.getMessage());
        }
        return filepath;
    }

    private String getUsername() {
        return dataStorage.getSharedPreference("username");
    }

    /**
     * Obtention de la date courante
     *
     * @return la date courante
     */
    private String getCurrentDate() {
        String currentDate;
        currentDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(Calendar.getInstance().getTime());
        return currentDate;
    }

    public void setInterestPointX(String interestPointX) {
        this.interestPointX = interestPointX;
    }

    public void setInterestPointY(String interestPointY) {
        this.interestPointY = interestPointY;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCommentFromSharePreference() {
        return dataStorage.getSharedPreference("comment");
    }

    public String getInterestPointXFromSharePreference() {
        return dataStorage.getSharedPreference("interestPointX");
    }

    public String getInterestPointYFromSharePreference() {
        return dataStorage.getSharedPreference("interestPointY");
    }

    public String getDeterminingKeyFromSharePreference() {
        return dataStorage.getSharedPreference("determiningKey");
    }

}
