package com.kolbytn.where2park;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

public class DestinationSelect extends AppCompatActivity {

    private ListView destinationList;
    private MultiAutoCompleteTextView destinationAutoComplete;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> autoCompleteAdapter;
    private String location;
    private ArrayList<JSONObject> destinations = new ArrayList<JSONObject>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_select);

        Toolbar destinationToolbar = findViewById(R.id.destinationToolbar);
        setSupportActionBar(destinationToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        destinationList = findViewById(R.id.destinationList);
        destinationAutoComplete = findViewById(R.id.destinationAutoComplete);

        Intent intent = getIntent();
        location = intent.getStringExtra("location");

        int tempRes = getResources().getIdentifier(location, "array", getPackageName());
        String[] tempDest = getResources().getStringArray(tempRes);
        for (int i = 0; i < tempDest.length; i++) {
            try {
                Log.d("Debug", tempDest[i]);
                destinations.add((JSONObject) new JSONTokener(tempDest[i]).nextValue());
            }
            catch (JSONException e) {
                Log.d("Error", e.getMessage());
            }
        }

        ArrayList<String> destArray = new ArrayList<String>();

        for (int i = 0; i < destinations.size(); i++) {
            try {
                destArray.add(destinations.get(i).get("dest").toString());
            }
            catch (JSONException e) {
                Log.d("Error", e.getMessage());
            }
        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, destArray);
        autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, destArray);

        destinationList.setAdapter(listAdapter);
        destinationAutoComplete.setAdapter(autoCompleteAdapter);

        destinationAutoComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        destinationList.setOnItemClickListener(itemClickedHandler);
        destinationAutoComplete.setOnItemClickListener(itemClickedHandler);
    }

    private AdapterView.OnItemClickListener itemClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            destinationAutoComplete.setText("");
            Intent intent = new Intent(parent.getContext(), ParkingResult.class);
            intent.putExtra("destination", destinations.get(position).toString());
            startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapButton:
                Intent intent = new Intent(this, MapsActivity.class);
                String[] lats = new String[21];
                String[] longs = new String[21];
                intent.putExtra("results", false);
                intent.putExtra("lats", lats);
                intent.putExtra("longs", longs);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
