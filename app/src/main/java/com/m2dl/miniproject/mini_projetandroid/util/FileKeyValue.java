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
