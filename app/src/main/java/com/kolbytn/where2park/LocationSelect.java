package com.kolbytn.where2park;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LocationSelect extends AppCompatActivity {

    private ListView locationList;
    private MultiAutoCompleteTextView locationAutoComplete;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> autoCompleteAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);

        Toolbar locationToolbar = findViewById(R.id.locationToolbar);
        setSupportActionBar(locationToolbar);

        locationList = findViewById(R.id.locationList);
        locationAutoComplete = findViewById(R.id.locationAutoComplete);

        String[] locations = getResources().getStringArray(R.array.locations);

        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, locations);
        autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, locations);

        locationList.setAdapter(listAdapter);
        locationAutoComplete.setAdapter(autoCompleteAdapter);

        locationAutoComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        locationList.setOnItemClickListener(itemClickedHandler);
        locationAutoComplete.setOnItemClickListener(itemClickedHandler);
    }

    private AdapterView.OnItemClickListener itemClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            locationAutoComplete.setText("");
            Intent intent = new Intent(parent.getContext(), DestinationSelect.class);
            intent.putExtra("location", "location" + position);
            startActivity(intent);
        }
    };

}
