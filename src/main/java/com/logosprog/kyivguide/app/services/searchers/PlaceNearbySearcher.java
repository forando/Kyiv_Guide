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
 * Created by forando on 12.12.14.<br>
 * An Abstract class that defines google.places API query algorithm based on
 * a specific object category type.
 */
public abstract class PlaceNearbySearcher extends PlaceSearcher {

    private static final String TAG = "PlaceNearbySearcher";

    private String placeType;

    protected String pageToken;

    public PlaceNearbySearcher(double latitude, double longitude, String placeType) {
        super(latitude, longitude);
        this.placeType = placeType;
        this.pageToken = null;
    }

    @Override
    public String createURLString(double latitude, double longitude, int r) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");

        urlString.append("&location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=");
        urlString.append(radius);
        // urlString.append("&language=en");
        urlString.append("&types=");
        urlString.append(placeType);
        urlString.append("&sensor=false&key=");
        urlString.append(App.API_KEY_WEB);
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
        double[] lat = { 50.420733, 50.384308, 50.420499, 50.482146, 50.484986, 50.399714, 50.353953 };
        double[] lon = { 30.513979, 30.461483, 30.413246, 30.457191, 30.589714, 30.625763, 30.932350 };
        int[] r = { 4000, 2000, 4000, 5000, 6000, 4000, 6000 };
        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<String> urls = new ArrayList<String>();
        places = new ArrayList<PlaceSearchPoint>();
        for (int k = 0; k < r.length; k++) {
            try {
                JSONObject json;
                String urlString;

                urlString = createURLString(lat[k], lon[k], r[k]);
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
                        urls.add(urlString);
                        tokens.add(next_page_token);
                    }
                }
                // return places;
            } catch (JSONException e1) {
                e1.printStackTrace();
                // return null;
            }
        }

        Log.e(TAG, "tokenUrls size is: " + tokens.size());
        if (tokens.size() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<ArrayList<String>> return_result = nearbySearch_processNextPageTokensHandler(tokens, urls);
            if (return_result != null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nearbySearch_processNextPageTokensHandler(return_result.get(0), return_result.get(1));
            }
        }

        if (places.size() > 0) {
            return places;
        } else {
            return null;
        }
    }

    private ArrayList<ArrayList<String>> nearbySearch_processNextPageTokensHandler(ArrayList<String> tokens, ArrayList<String> urls){
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
