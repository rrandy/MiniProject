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

package com.m2dl.miniproject.mini_projetandroid.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;


/**
 * Activité du menu principal
 * Choix des différentes fonctionnalités de l'application
 */
public class ActivityMainMenu extends ActionBarActivity {

    /**
     * Gestion des données dans l'application
     */
    private DataStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        setContentView(R.layout.activity_main_list_menu);
        // Gestion des données
        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));
        // Vue du menu
        final ListView listview = (ListView) findViewById(R.id.listView);
        // Eléments titres du menu
        String[] values = new String[]{
                getResources().getString(R.string.take_photo),
                getResources().getString(R.string.add_interest_point),
                getResources().getString(R.string.follow_determination_key),
                getResources().getString(R.string.add_comment),
                getResources().getString(R.string.validate),

        };
        // Layouts spécifiques pour chaque élément
        MyArrayAdapter adapter = new MyArrayAdapter(this, values);
        listview.setAdapter(adapter);

        // Gestion des clics sur chaque élément
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                // Lancement des activités correspondantes aux éléments
                if (getResources().getString(R.string.take_photo).equals(item)) {
                    Intent intent = new Intent(ActivityMainMenu.this, ActivityPhoto.class);
                    startActivity(intent);
                } else if (getResources().getString(R.string.add_interest_point).equals(item)) {
                    Intent intent = new Intent(ActivityMainMenu.this, ActivityInterestPoint.class);
                    startActivityForResult(intent, 1);
                } else if (getResources().getString(R.string.follow_determination_key).equals(item)) {
                    Intent intent = new Intent(ActivityMainMenu.this, ActivityDetermining.class);
                    startActivity(intent);
                } else if (getResources().getString(R.string.add_comment).equals(item)) {
                    Intent intent = new Intent(ActivityMainMenu.this, ActivityComment.class);
                    startActivityForResult(intent, 1);
                } else if (getResources().getString(R.string.validate).equals(item)) {
                    Intent intent = new Intent(ActivityMainMenu.this, ActivityValidate.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_clearSharedPref:
                storage.clearPreferences();
                Intent nextActivity = new Intent(this, ActivityName.class);
                startActivity(nextActivity);
                break;
            case R.id.action_leave:
                System.exit(RESULT_OK);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adapteur spécifique pour chaque élément du menu
     */
    public class MyArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MyArrayAdapter(Context context, String[] values) {
            super(context, R.layout.row_layout, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values[position]);
            String item = values[position];

            // Changement de l'icone en fonction du menu
            if (this.getContext().getResources().getString(R.string.take_photo).equals(item)) {
                imageView.setImageResource(R.drawable.take_photo);
            } else if (this.getContext().getResources().getString(R.string.add_interest_point).equals(item)) {
                imageView.setImageResource(R.drawable.add_interest_point);
            } else if (this.getContext().getResources().getString(R.string.follow_determination_key).equals(item)) {
                imageView.setImageResource(R.drawable.follow_determination_key);
            } else if (this.getContext().getResources().getString(R.string.add_comment).equals(item)) {
                imageView.setImageResource(R.drawable.add_comment);
            } else if (this.getContext().getResources().getString(R.string.validate).equals(item)) {
                imageView.setImageResource(R.drawable.validate);
            }

            return rowView;
        }

    }

}
