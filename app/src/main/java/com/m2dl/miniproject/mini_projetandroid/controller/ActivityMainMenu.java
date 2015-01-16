package com.m2dl.miniproject.mini_projetandroid.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;
import com.m2dl.miniproject.mini_projetandroid.business.ExifInterfaceExtended;
import com.m2dl.miniproject.mini_projetandroid.R;

import java.io.IOException;


public class ActivityMainMenu extends ActionBarActivity {

    private String filePath = null;
    private DataStorage storage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_menu);

        // To delete
        storage = new DataStorage(this, getResources().getString(R.string.sharedPreferencesFile));

        filePath = Environment.getExternalStorageDirectory() + "/Pic.jpg";

        final ListView listview = (ListView) findViewById(R.id.listView);

        String[] values = new String[]{
                getResources().getString(R.string.take_photo),
                getResources().getString(R.string.add_interest_point),
                getResources().getString(R.string.follow_determination_key),
                getResources().getString(R.string.add_comment),
                getResources().getString(R.string.validate),

        };
        MyArrayAdapter adapter = new MyArrayAdapter(this, values);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), item + " selected", Toast.LENGTH_LONG).show();

                if (getResources().getString(R.string.take_photo).equals(item)) {
                    Intent intent = new Intent(ActivityMainMenu.this, ActivityPhoto.class);
                    startActivity(intent);
                } else if (getResources().getString(R.string.add_interest_point).equals(item)) {
                    Intent intent = new Intent(ActivityMainMenu.this, ActivityInterestPoint.class);
                    startActivityForResult(intent, 1);
                } else if (getResources().getString(R.string.follow_determination_key).equals(item)) {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            ExifInterfaceExtended exif = new ExifInterfaceExtended(filePath);
            if (resultCode == RESULT_OK) {
//                    Toast.makeText(this.getApplicationContext(), comment, Toast.LENGTH_LONG).show();

//                if (requestCode == 1) {
//                    String comment = data.getStringExtra("comment");
//                    exif.setAttribute(ExifInterfaceExtended.TAG_USER_COMMENT, comment);
//                }
//                if (requestCode == 1) {
//                    String comment = data.getStringExtra("comment");
//                    exif.setAttribute(ExifInterfaceExtended.TAG_USER_COMMENT, comment);
//                }
                if (requestCode == 1) {
                    String comment = data.getStringExtra("comment");
                    exif.setAttribute(ExifInterfaceExtended.TAG_USER_COMMENT, comment);
                }
//                if (requestCode == 1) {
//                    String comment = data.getStringExtra("comment");
//                    exif.setAttribute(ExifInterfaceExtended.TAG_USER_COMMENT, comment);
//                }
                exif.saveAttributes();
            }

            if (resultCode == RESULT_CANCELED) {
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        switch(id) {
            case R.id.action_clearSharedPref :
                storage.clearPreferences();
                break;
            case R.id.action_leave :
                System.exit(RESULT_OK);
        }

        return super.onOptionsItemSelected(item);
    }

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
