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

public class ActivityDetermining extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private List<String> elementList;
    private int selectedElement;
    private ArrayAdapter<String> adapter;
    private Spinner elementSpinner;
    private InterestXmlParser parser;
    private TextView tvElementSelection;
    private String parentElements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_determining);

        tvElementSelection = (TextView) findViewById(R.id.tvElements);
        parentElements = "";

        elementSpinner = (Spinner) findViewById(R.id.elementSpinner);
        elementSpinner.setOnItemSelectedListener(this);

        try {
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

    public void exploreElement(View v) {
        parentElements = parentElements + parser.getElementName(elementList.get(selectedElement)).trim() + " >> ";
        List<String> elementDesc = parser.getDescendantsForElement(elementList.get(selectedElement));
        List<String> elementNames = new ArrayList<>();
        for (String element : elementDesc) {
            elementNames.add(parser.getElementName(element));
        }
        elementList = elementDesc;
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, elementNames);
        elementSpinner.setAdapter(adapter);

        updateElementText();
    }

    public void saveElement(View v) {
        DataStorage storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));
        storage.newSharedPreference("determiningKey",elementList.get(selectedElement));
        System.out.println("Selected element : " + elementList.get(selectedElement));
        Intent nextActivity = new Intent(this, ActivityMainMenu.class);
        startActivity(nextActivity);
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

    private boolean isLastElementLevel(String elementSelected) {
        return parser.getDescendantsForElement(elementSelected).isEmpty();
    }

    private void updateElementText() {
        tvElementSelection.setText(parentElements + parser.getElementName(elementList.get(selectedElement)));
    }
}
