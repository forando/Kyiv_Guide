package com.logosprog.kyivguide.app.services.searchers;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by forando on 12.12.14.<br>
 * An Abstract class that defines google.places API query algorithm based on
 * a specific object category type.
 */
public abstract class PlaceNearbySearcher extends PlaceSearcher {

    private static final String TAG = "PlaceNearbySearcher";

    protected String pageToken;

    public PlaceNearbySearcher(double latitude, double longitude) {
        super(latitude, longitude);

        pageToken = null;
    }

    protected ArrayList<ArrayList<String>> nearbySearch_processNextPageTokensHandler(ArrayList<String> tokens, ArrayList<String> urls){
        ArrayList<String> Tokens = new ArrayList<String>();
        ArrayList<String> URLs = new ArrayList<String>();
        ArrayList<ArrayList<String>> return_result = new ArrayList<ArrayList<String>>();
        for (int k = 0; k < tokens.size(); k++) {
            try {
                JSONObject json = null;
                String urlString;
                urlString = urls.get(k) + "&pagetoken=" + tokens.get(k);
                Log.e(TAG, "URL: " + urlString);
                String result = executeQuery(urlString);
                System.out.println(result);
                json = new JSONObject(result);
                String status = json.getString("status");
                if (status.equals("OK")) {
                    JSONArray jsonArray = json.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PlaceSearchPoint placeSearchPoint = new PlaceSearchPoint(
                                (JSONObject) jsonArray.get(i), NEARBY_SEARCH);
                        Log.v("Places Services ", "" + placeSearchPoint);
                        places.add(placeSearchPoint);
                    }
                    String next_page_token;
                    if (json.has("next_page_token")) {
                        next_page_token = json.getString("next_page_token");
                        URLs.add(urls.get(k));
                        Tokens.add(next_page_token);
                    }
                }
                // return places;
            } catch (JSONException e1) {
                e1.printStackTrace();
                // return null;
            }
        }

        Log.e(TAG, "tokenUrls size is: " + Tokens.size());
        if (Tokens.size() > 0 && Tokens.size() == URLs.size() ) {
            return_result.add(Tokens);
            return_result.add(URLs);
            return return_result;
        }else{
            return null;
        }
    }
}
