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

package com.m2dl.miniproject.mini_projetandroid.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Fichier de type clé valeur pour sauver les métadonnées
 * Created by kana on 17/01/15.
 */
public class FileKeyValue {
    /**
     * Séparateur entre la clé et la valeur
     */
    private final static String SEPERATOR = "=";
    /**
     * Remplacement d'un saut de ligne pour l'affichage
     */
    private final static String REPLACE_NEW_LINE = " ";
    /**
     * Saut de ligne
     */
    private final static String NEW_LINE = "\n";
    /**
     * Fichier de sauvegarde des métadonnées
     */
    private File file;

    public FileKeyValue(String filepath) {
        // Initialisation et destruction du fichier s'il existe déjà
        this.file = new File(filepath);
        file.delete();
    }

    /**
     * Ajout d'une clé valeur au fichier
     *
     * @param key   clé
     * @param value valeur
     * @throws IOException
     */
    public void put(String key, String value) throws IOException {
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(file, true));
        bWriter.write(convertNewLineToSpecialChar(key + SEPERATOR + value) + NEW_LINE);
        bWriter.close();
    }

    /**
     * Convertit les sauts de lignes en espaces
     *
     * @param text texte à remplacer
     * @return
     */
    private String convertNewLineToSpecialChar(String text) {
        return text.replaceAll("\n", REPLACE_NEW_LINE);
    }

}
