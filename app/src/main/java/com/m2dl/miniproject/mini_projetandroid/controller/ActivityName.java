package com.m2dl.miniproject.mini_projetandroid.controller;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;


/**
 * Activité nom
 * Permet à l'utilisateur de renseigner son nom
 */
public class ActivityName extends ActionBarActivity {

    /**
     * Gestion des données de l'application
     */
    private DataStorage storage;
    /**
     * Nom de l'utilisateur
     */
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtenir l'username des préférences
        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));
        username = storage.getSharedPreference(getString(R.string.preferenceUsername));
        if (username != null) {
            // Aller au menu principal si le nom est déjà renseigné
            Intent nextActivity = new Intent(this, ActivityMainMenu.class);
            startActivity(nextActivity);
        } else {
            // Sinon lancer l'activité Name
            setContentView(R.layout.activity_name);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_name, menu);
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
     * Sauver le nom d'utilisateur
     *
     * @param v vue
     */
    public void saveUsername(View v) {
        EditText usernameTxt = (EditText) findViewById(R.id.editUserName);
        String usernameStr = usernameTxt.getText().toString();

        // S'il y a un username dans l'edittext, sauvegarder et aller au menu principal
        if (!usernameStr.equals("")) {
            storage.newSharedPreference(getString(R.string.preferenceUsername), usernameStr);
            Intent mainMenuActivity = new Intent(this, ActivityMainMenu.class);
            startActivity(mainMenuActivity);
        }
    }
}
