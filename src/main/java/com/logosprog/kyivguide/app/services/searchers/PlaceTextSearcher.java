package com.logosprog.kyivguide.app.services.searchers;

import android.util.Log;
import com.logosprog.kyivguide.app.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by forando on 11.12.14.<br>
 * A class that queries google.places API for objects using text search. <br>
 *   ATTENTION! one text request considered as 10 category requests.
 */
public class PlaceTextSearcher extends PlaceSearcher {

    private final String TAG = getClass().getSimpleName();

    private String input;

    public PlaceTextSearcher(double latitude, double longitude, String input) {
        super(latitude, longitude);

        this.input = input;
    }

    @Override
    public String createURLString(double latitude, double longitude, int radius) {
        /*
        ATTENTION! one request considered as 10
         */
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/textsearch/json?");
        try {
            urlString.append("&query=");
            urlString.append(URLEncoder.encode(input, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&sensor=false");
        urlString.append("&key=" + App.API_KEY_WEB);
        urlString.append("&location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=");
        urlString.append(radius);
        // urlString.append("&language=en");
        if (pageToken != null && !pageToken.equals("")) {
            try {
                urlString.append("&pageToken=");
                urlString.append(URLEncoder.encode(pageToken, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return urlString.toString();
    }

    @Override
    public ArrayList<PlaceSearchPoint> getPlaces() {

        String urlString = createURLString(latitude, longitude, radius);
        Log.e(TAG, "URL: " + urlString);
        String result = executeQuery(urlString);
        System.out.println(result);
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                PlaceSearchPoint placeSearchPoint = new PlaceSearchPoint(
                        (JSONObject) jsonArray.get(i), TEXT_SEARCH);
                Log.v("Places Services ", "" + placeSearchPoint);
                places.add(placeSearchPoint);
            }
            // return places;
        } catch (JSONException e1) {
            e1.printStackTrace();
            // return null;
        }
        if (places.size() > 0) {
            return places;
        } else {
            return null;
        }
    }
}
