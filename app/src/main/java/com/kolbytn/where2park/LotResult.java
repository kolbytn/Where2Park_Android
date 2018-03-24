package com.kolbytn.where2park;

import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by kolby on 3/24/2018.
 */

public class LotResult implements Comparable<LotResult> {

    public String name;
    public String latitude;
    public String longitude;
    public String durationString;
    public String distanceString;
    public int durationInt;
    public int distanceInt;

    LotResult(String n, String lat, String lon, String dur, String dist, int durInt, int distInt) {
        name = n;
        latitude = lat;
        longitude = lon;
        durationString = dur;
        distanceString = dist;
        durationInt = durInt;
        distanceInt = distInt;
    }

    @Override
    public int compareTo(LotResult compLot) {
        return this.durationInt - compLot.durationInt;
    }

    @Override
    public String toString() {
        return name + ": " + durationString + ", " + distanceString;
    }
}
