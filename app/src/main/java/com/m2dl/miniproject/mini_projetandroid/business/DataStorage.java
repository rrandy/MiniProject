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
