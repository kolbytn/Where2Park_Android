package com.kolbytn.where2park;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

public class ParkingResult extends AppCompatActivity {

    public ArrayList<LotResult> responseList = new ArrayList<>();
    public Queue<JSONObject> lotsQueue = new ConcurrentLinkedQueue<>();
    private Intent intent;
    private ListView parkingList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_result);

        Toolbar resultsToolbar = findViewById(R.id.resultsToolbar);
        setSupportActionBar(resultsToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        parkingList = findViewById(R.id.parkingList);

        GetDistances apiThread = new GetDistances();

        apiThread.start();
    }

    class GetDistances implements Runnable {
        @Override
        public void run() {
            try {
                JSONObject dest = new JSONObject(intent.getStringExtra("destination"));
                Log.d("Debug", dest.toString());

                String[] lots = getResources().getStringArray(R.array.lots);
                for (int i = 0; i < lots.length; i++) {
                    JSONObject lot = new JSONObject(lots[i]);
                    lotsQueue.add(lot);
                }
                for (int i = 0; i < lots.length; i++) {
                    JSONObject lot = new JSONObject(lots[i]);
                    getDistance(dest.get("lat").toString(), dest.get("long").toString(),
                            lot.get("lat").toString(), lot.get("long").toString());
                }
            }
            catch (JSONException e) {
                Log.d("Error", e.getMessage());
            }
        }

        public void start() {
            Thread t = new Thread (this);
            t.start ();
        }
    }

    private void reloadList() {
        Collections.sort(responseList);
        ArrayList<String> responsesTemp = new ArrayList<>();

        for (int i = 0; i < responseList.size(); i++) {
            responsesTemp.add(Integer.toString(i + 1) + ") " + responseList.get(i).toString());
        }

        ArrayAdapter<String> resAdapter = new ArrayAdapter<>
                (this, R.layout.simplerow, responsesTemp);

        parkingList.setAdapter(resAdapter);

        parkingList.setOnItemClickListener(itemClickedHandler);
    }

    private AdapterView.OnItemClickListener itemClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            String lat = responseList.get(position).latitude;
            String lon = responseList.get(position).longitude;
            String url = "https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lon;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    };

    private void getDistance(String lat1, String long1, String lat2, String long2) {
        String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=" + lat1 +
                "," + long1 + "&destinations=" + lat2 + "," + long2 +
                "&mode=walking&units=imperial&" + R.string.maps_key;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject res) {
                        try {
                            Log.d("Debug", res.toString());
                            String dist = res.getJSONArray("rows").getJSONObject(0)
                                    .getJSONArray("elements").getJSONObject(0)
                                    .getJSONObject("distance").get("text").toString();
                            String dur = res.getJSONArray("rows").getJSONObject(0)
                                    .getJSONArray("elements").getJSONObject(0)
                                    .getJSONObject("duration").get("text").toString();
                            int distInt = (int) res.getJSONArray("rows").getJSONObject(0)
                                    .getJSONArray("elements").getJSONObject(0)
                                    .getJSONObject("distance").get("value");
                            int durInt = (int) res.getJSONArray("rows").getJSONObject(0)
                                    .getJSONArray("elements").getJSONObject(0)
                                    .getJSONObject("duration").get("value");
                            LotResult element = new LotResult
                                    (lotsQueue.peek().get("name").toString(),
                                            lotsQueue.peek().get("lat").toString(),
                                            lotsQueue.remove().get("long").toString(),
                                            dur, dist, durInt, distInt);
                            responseList.add(element);
                            reloadList();
                        }
                        catch (JSONException e) {
                            Log.d("Error", e.getMessage());
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                    }
                });

        queue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapButton:
                Intent intent = new Intent(this, MapsActivity.class);
                String[] lats = new String[21];
                String[] longs = new String[21];
                for (int i = 0; i < responseList.size(); i++) {
                    lats[i] = responseList.get(i).latitude;
                    longs[i] = responseList.get(i).longitude;
                }
                intent.putExtra("results", true);
                intent.putExtra("lats", lats);
                intent.putExtra("longs", longs);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}