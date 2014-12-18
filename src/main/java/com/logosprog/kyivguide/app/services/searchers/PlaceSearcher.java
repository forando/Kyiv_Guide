package com.logosprog.kyivguide.app.services.searchers;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by forando on 11.12.14.<br>
 *     Abstract class that defines google.places API query algorithm
 */
public abstract class PlaceSearcher {

    private final String TAG = getClass().getSimpleName();

    //===================================================================================

    /**
     * The constant to define that Google Places "Nearby Search" algorithm must be applied.
     * The only difference with "Text Search" is that "Text Search" returns "formatted_address"
     * variable insted of "vicinity"
     */
    public static final String NEARBY_SEARCH = "NearbySearch";
    /**
     * The constant to define that Google Places "Text Search" algorithm must be applied.
     * The only difference with "Nearby Search" is that "Nearby Search" returns "vicinity"
     * variable insted of "formatted_address"
     */
    public static final String TEXT_SEARCH = "TextSearch";

    //===================================================================================

    //============================================================================================

    public static final String PLACE_SEE = "0";
    public static final String PLACE_ATTRACTIONS = "1";
    public static final String PLACE_SHOPPING = "2";
    public static final String PLACE_BEAUTY = "3";

    public static final String PLACE_HOTELS = "4";
    public static final String PLACE_CAFE = "5";
    public static final String PLACE_BARS = "6";
    public static final String PLACE_RESTAURANTS = "7";

    public static final String PLACE_ATM = "8";
    public static final String PLACE_BANK = "9";
    public static final String PLACE_AIRPORT = "10";
    public static final String PLACE_GAS = "11";

    //============================================================================================


    protected final double latitude;
    protected final double longitude;
    protected int radius;
    protected String pageToken = null;

    protected ArrayList<PlaceSearchPoint> places;

    public PlaceSearcher(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;

        radius = 15000;
        places = new ArrayList<PlaceSearchPoint>();
    }

    public abstract String createURLString(double latitude, double longitude, int r);

    public String executeQuery(String urlString){
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

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
