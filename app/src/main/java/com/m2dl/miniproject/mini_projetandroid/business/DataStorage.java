package com.m2dl.miniproject.mini_projetandroid.business;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe de gestion des données et préférences de l'application
 * Permet d'enregistrer le nom et les informations pour l'application
 */
public class DataStorage {

    /**
     * Contexte de l'application
     */
    private Context context;
    /**
     * Nom du fichier de paramètres
     */
    private String settingsFilename;
    /**
     * Paramètres partagés pour l'application
     */
    private SharedPreferences settings;

    public DataStorage(Context activityContext, String filename) {
        // Obtention du fichier de préférences partagées
        context = activityContext;
        settingsFilename = filename;
        settings = context.getSharedPreferences(settingsFilename, Context.MODE_PRIVATE);
    }

    /**
     * Créer une nouvelle préférence partagée
     *
     * @param key   clé de la préférence
     * @param value valeur de la préférence
     */
    public void newSharedPreference(String key, String value) {
        settings.edit().putString(key, value).apply();
    }

    /**
     * Obtenir une préférence partagée existante
     *
     * @param key clé de la préférence
     * @return valeur de la préférence
     */
    public String getSharedPreference(String key) {
        return settings.getString(key, null);
    }

    /**
     * Supprimer les préférences
     */
    public void clearPreferences() {
        settings.edit().clear().apply();
    }
}
