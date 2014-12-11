package com.logosprog.kyivguide.app.services.searchers;

import android.util.Log;
import com.logosprog.kyivguide.app.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 11.12.14.
 */
public class PlaceTextSearcher extends PlaceSearcher {

    private final String TAG = getClass().getSimpleName();

    public PlaceTextSearcher(double latitude, double longitude, String input) {
        super(latitude, longitude, input);
    }

    @Override
    public String createURLString() {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/textsearch/json?");// ATTANTION
        // one
        // request
        // considered
        // as
        // 10
        try {
            urlString.append("&query=" + URLEncoder.encode(input, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&sensor=false");
        urlString.append("&key=" + App.API_KEY_WEB);
        urlString.append("&location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=15000");
        // urlString.append("&language=en");
        if (pageToken != null && !pageToken.equals("")) {
            urlString.append("&pagetoken=" + pageToken);
        }
        return urlString.toString();
    }

    @Override
    public ArrayList<PlaceSearchPoint> getPlaceSearchPointList() {

        String urlString = createURLString();
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
                arrayList.add(placeSearchPoint);
            }
            // return arrayList;
        } catch (JSONException e1) {
            e1.printStackTrace();
            // return null;
        }
        if (arrayList.size() > 0) {
            return arrayList;
        } else {
            return null;
        }
    }
}
