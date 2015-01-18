package com.m2dl.miniproject.mini_projetandroid.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by kana on 17/01/15.
 */
public class FileKeyValue {

    private final static String SEPERATOR = "=";
    private final static String REPLACE_NEW_LINE = " ";
    private final static String NEW_LINE = "\n";

    private File file;
    public FileKeyValue(String filepath){
        this.file = new File(filepath);
        //delete old file before write data to a new file
        file.delete();
    }

    public void put(String key, String value) throws IOException {
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(file, true));
        bWriter.write( convertNewLineToSpecialChar( key + SEPERATOR + value ) + NEW_LINE );
        bWriter.close();
    }

    private String convertNewLineToSpecialChar(String text){
        return text.replaceAll("\n", REPLACE_NEW_LINE);
    }

}
