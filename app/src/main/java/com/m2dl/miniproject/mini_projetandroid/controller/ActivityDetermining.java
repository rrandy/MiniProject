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

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;
import com.m2dl.miniproject.mini_projetandroid.util.InterestXmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Activité de suivi d'une clé de détermination
 * Pour déterminer si ce qui est photographié est une plante, un animal...
 */
public class ActivityDetermining extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    /**
     * Liste d'éléments
     */
    private List<String> elementList;
    /**
     * Itérateur de l'élément sélectionné
     */
    private int selectedElement;
    /**
     * Adaptateur de valeurs pour le spinner
     */
    private ArrayAdapter<String> adapter;
    /**
     * Spinner pour sélectionner l'élément
     */
    private Spinner elementSpinner;
    /**
     * Parseur de fichier XML
     */
    private InterestXmlParser parser;
    /**
     * TextView de sélection de l'élément
     */
    private TextView tvElementSelection;
    /**
     * Eléments parents
     */
    private String parentElements;
    /**
     * Eléments sélectionnés
     */
    private List<String> elementSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        setContentView(R.layout.activity_determining);

        // Texte de la sélection
        tvElementSelection = (TextView) findViewById(R.id.tvElements);
        parentElements = "";

        // Spinner
        elementSpinner = (Spinner) findViewById(R.id.elementSpinner);
        elementSpinner.setOnItemSelectedListener(this);

        // Bouton précédent
        Button btn = (Button) findViewById(R.id.btnPrec);
        btn.setEnabled(false);

        // Elément sélectionné
        elementSelection = new ArrayList<>();
        elementSelection.add("FIRST_NODE");

        try {
            // Lecture du fichier XML
            InputStream stream = getApplicationContext().getAssets().open("interestpointstructure.xml");
            parser = new InterestXmlParser(stream);
            elementList = parser.getFirstLevelElements();
            List<String> elementNames = new ArrayList<>();
            for (String element : elementList) {
                elementNames.add(parser.getElementName(element));
            }
            tvElementSelection.setText(elementNames.get(0));
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, elementNames);
            elementSpinner.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_determining, menu);
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
     * Explorer un élément, le sélectionner et aller plus loin dans l'arbre
     *
     * @param v vue
     */
    public void exploreElement(View v) {
        // Ajout de l'élément courant à la liste des parents
        parentElements = parentElements + parser.getElementName(elementList.get(selectedElement)).trim() + " >> ";
        elementSelection.add(elementList.get(selectedElement));

        // Met à jour le spinner et la liste des éléments courants
        List<String> elementDesc = parser.getDescendantsForElement(elementList.get(selectedElement));
        updateSpinner(elementDesc);

        // Bouton précédent, possibilité de revenir en arrière
        Button btn = (Button) findViewById(R.id.btnPrec);
        btn.setEnabled(true);

        updateElementText();
    }

    /**
     * Revenir à l'élément précédent
     *
     * @param v vue
     */
    public void precedentElement(View v) {
        // Supprimer l'élément courant
        elementSelection.remove(elementSelection.size() - 1);
        String previousElement = elementSelection.get(elementSelection.size() - 1);
        // Si on revient au début, on ne peut plus revenir en arrière
        if (previousElement.equals("FIRST_NODE")) {
            updateSpinner(parser.getFirstLevelElements());
            Button btn = (Button) findViewById(R.id.btnPrec);
            btn.setEnabled(false);
        } else {
            // Sinon on met à jour le spinner
            updateSpinner(parser.getDescendantsForElement(previousElement));
        }
        rebuildParentElementString();
        selectedElement = 0;
        updateElementText();
    }

    /**
     * Sauvegarder l'élément choisi dans les préférences
     *
     * @param v vue
     */
    public void saveElement(View v) {
        // On sauve l'élément choisi
        DataStorage storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));
        storage.newSharedPreference("determiningKey", elementList.get(selectedElement));
        // On revient au menu principal
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedElement = position;
        Button btn = (Button) findViewById(R.id.btnExploreElement);
        btn.setEnabled(!isLastElementLevel(elementList.get(position)));

        updateElementText();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Retourne si l'élément est au dernier niveau de l'arbre
     *
     * @param elementSelected Element à tester
     * @return
     */
    private boolean isLastElementLevel(String elementSelected) {
        return parser.getDescendantsForElement(elementSelected).isEmpty();
    }

    /**
     * Met à jour le texte
     */
    private void updateElementText() {
        tvElementSelection.setText(parentElements + parser.getElementName(elementList.get(selectedElement)));
    }

    /**
     * Met à jour le spinner
     *
     * @param elementDesc
     */
    private void updateSpinner(List<String> elementDesc) {
        List<String> elementNames = new ArrayList<>();
        for (String element : elementDesc) {
            elementNames.add(parser.getElementName(element));
        }
        elementList = elementDesc;
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, elementNames);
        elementSpinner.setAdapter(adapter);
    }

    /**
     * Reconstruit le texte contenant la clé choisie et ses parents
     */
    private void rebuildParentElementString() {
        parentElements = "";
        for (int i = 1; i < elementSelection.size(); i++) {
            parentElements += parser.getElementName(elementSelection.get(i)).trim() + " >> ";
        }
    }
}
