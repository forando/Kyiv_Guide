package com.logosprog.kyivguide.app.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Create request for Places API.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 *
 */

public class PlacesService {

    final static String TAG = "PlacesService";

    private String searchAlgorithm;

    private ArrayList<PlaceSearchPoint> arrayList;

    public PlacesService(String _searchAlgorithm) {
        this.searchAlgorithm = _searchAlgorithm;
    }

    public ArrayList<PlaceSearchPoint> nearbySearch(double latitude,
                                                    double longitude, String placeSpacification) {

        double[] lat = { 50.420733, 50.384308, 50.420499, 50.482146, 50.484986, 50.399714, 50.353953 };
        double[] lon = { 30.513979, 30.461483, 30.413246, 30.457191, 30.589714, 30.625763, 30.932350 };
        String[] r = { "4000", "2000", "4000", "5000", "6000", "4000", "6000" };
        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<String> urls = new ArrayList<String>();
        arrayList = new ArrayList<PlaceSearchPoint>();
        for (int k = 0; k < r.length; k++) {
            try {
                JSONObject json = null;
                String urlString;
				/*
				 * boolean firstLoop = true; do { if(firstLoop){ urlString =
				 * nearbySearchUrl(lat[k], lon[k], r[k], placeSpacification,
				 * null); }else{ urlString = nearbySearchUrl(lat[k], lon[k],
				 * r[k], placeSpacification, json.getString("next_page_token"));
				 * } firstLoop = false; Log.e(TAG, "URL: " + urlString); String
				 * result = getUrlContents(urlString);
				 * System.out.println(result); json = new JSONObject(result);
				 * String status = json.getString("status");
				 * if(status.equals("OK")){ JSONArray jsonArray =
				 * json.getJSONArray("results"); for (int i = 0; i <
				 * jsonArray.length(); i++) { PlaceSearchPoint placeSearchPoint
				 * = new PlaceSearchPoint((JSONObject) jsonArray.get(i));
				 * Log.v("Places Services ", "" + placeSearchPoint);
				 * places.add(placeSearchPoint); } }
				 *
				 * } while (json.has("next_page_token"));
				 */

                urlString = nearbySearchUrl(lat[k], lon[k], r[k],
                        placeSpacification, null);
                Log.e(TAG, "URL: " + urlString);
                String result = getUrlContents(urlString);
                System.out.println(result);
                json = new JSONObject(result);
                String status = json.getString("status");
                if (status.equals("OK")) {
                    JSONArray jsonArray = json.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PlaceSearchPoint placeSearchPoint = new PlaceSearchPoint(
                                (JSONObject) jsonArray.get(i), searchAlgorithm);
                        Log.v("Places Services ", "" + placeSearchPoint);
                        arrayList.add(placeSearchPoint);
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
                return_result = nearbySearch_processNextPageTokensHandler(return_result.get(0), return_result.get(1));
            }
        }

        if (arrayList.size() > 0) {
            return arrayList;
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
                String result = getUrlContents(urlString);
                System.out.println(result);
                json = new JSONObject(result);
                String status = json.getString("status");
                if (status.equals("OK")) {
                    JSONArray jsonArray = json.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PlaceSearchPoint placeSearchPoint = new PlaceSearchPoint(
                                (JSONObject) jsonArray.get(i), searchAlgorithm);
                        Log.v("Places Services ", "" + placeSearchPoint);
                        arrayList.add(placeSearchPoint);
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

    Handler nearbySearch_processNextPageTokensHandler;

    public class NearbySearch_processNextPageTokensRunnable implements Runnable {
        private ArrayList<String> tokens;
        private ArrayList<String> urls;

        public NearbySearch_processNextPageTokensRunnable(
                ArrayList<String> Tokens, ArrayList<String> Urls) {
            this.tokens = Tokens;
            this.urls = Urls;
        }

        public void run() {
            ArrayList<String> Tokens = new ArrayList<String>();
            ArrayList<String> URLs = new ArrayList<String>();
            for (int k = 0; k < tokens.size(); k++) {
                try {
                    JSONObject json = null;
                    String urlString;
                    urlString = urls.get(k) + "&pagetoken=" + tokens.get(k);
                    Log.e(TAG, "URL: " + urlString);
                    String result = getUrlContents(urlString);
                    System.out.println(result);
                    json = new JSONObject(result);
                    String status = json.getString("status");
                    if (status.equals("OK")) {
                        JSONArray jsonArray = json.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            PlaceSearchPoint placeSearchPoint = new PlaceSearchPoint(
                                    (JSONObject) jsonArray.get(i), searchAlgorithm);
                            Log.v("Places Services ", "" + placeSearchPoint);
                            arrayList.add(placeSearchPoint);
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
            if (Tokens.size() > 0) {
                // nearbySearch_processNextPageTokensHandler = new Handler();
                NearbySearch_processNextPageTokensRunnable Runnable = new NearbySearch_processNextPageTokensRunnable(
                        Tokens, URLs);
                nearbySearch_processNextPageTokensHandler.postDelayed(Runnable, 2000);
            }

        }
    }

    public ArrayList<PlaceSearchPoint> textSearch(double latitude,
                                                  double longitude, String query) {

        arrayList = new ArrayList<PlaceSearchPoint>();

        String urlString = textSearchUrl(latitude, longitude, query, null);
        Log.e(TAG, "URL: " + urlString);
        String result = getUrlContents(urlString);
        System.out.println(result);
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                PlaceSearchPoint placeSearchPoint = new PlaceSearchPoint(
                        (JSONObject) jsonArray.get(i), searchAlgorithm);
                Log.v("Places Services ", "" + placeSearchPoint);
                arrayList.add(placeSearchPoint);
            }
            // return places;
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

    public List<HashMap<String, String>> placeAutocomplete(double latitude,
                                                           double longitude, String input) {

        if (input == null || input.equals("")) {
            return null;
        }
        String urlString = placeAutocompletehUrl(latitude, longitude, input);
        Log.e(TAG, "URL: " + urlString);
        String result = getUrlContents(urlString);
        System.out.println(result);
        try {
            JSONObject json = new JSONObject(result);
            if (json.get("status").equals("OK")) {
                List<HashMap<String, String>> predictions = null;
                PlaceJSONParser placeJsonParser = new PlaceJSONParser();

                // Getting the parsed data as a List construct
                predictions = placeJsonParser.parse(json);

                return predictions;
            } else {
                return null;
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> placeQueryAutocomplete(double latitude,
                                                    double longitude, String input) {

        if (input == null || input.equals("")) {
            return null;
        }
        String urlString = placeQueryAutocompletehUrl(latitude, longitude,
                input);
        Log.e(TAG, "URL: " + urlString);
        String result = getUrlContents(urlString);
        System.out.println(result);
        try {
            JSONObject json = new JSONObject(result);
            if (json.get("status").equals("OK")) {
                JSONArray jsonArray = json.getJSONArray("predictions");
                ArrayList<String> predictionsList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject prediction = (JSONObject) jsonArray.get(i);
                    predictionsList.add(prediction.getString("description"));
                }
                return predictionsList;
            } else {
                return null;
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    // https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String nearbySearchUrl(double latitude, double longitude, String r,
                                   String place_type, String pageToken) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");

        if (place_type.equals("")) {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=15000");
            // urlString.append("&language=en");
            urlString.append("&types=restaurant");
            urlString.append("&sensor=false&key=" + App.API_KEY_WEB);
            if (pageToken != null && !pageToken.equals("")) {
                try {
                    urlString.append("&pageToken="
                            + URLEncoder.encode(pageToken, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=" + r);
            // urlString.append("&language=en");
            urlString.append("&types=" + place_type);
            urlString.append("&sensor=false&key=" + App.API_KEY_WEB);
            if (pageToken != null && !pageToken.equals("")) {
                try {
                    urlString.append("&pageToken="
                            + URLEncoder.encode(pageToken, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return urlString.toString();
    }

    private String textSearchUrl(double latitude, double longitude,
                                 String query, String pagetoken) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/textsearch/json?");// ATTANTION
        // one
        // request
        // considered
        // as
        // 10
        try {
            urlString.append("&query=" + URLEncoder.encode(query, "utf-8"));
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
        if (pagetoken != null && !pagetoken.equals("")) {
            urlString.append("&pagetoken=" + pagetoken);
        }
        return urlString.toString();
    }

    private String placeAutocompletehUrl(double latitude, double longitude,
                                         String input) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/autocomplete/json?");
        try {
            urlString.append("&input=" + URLEncoder.encode(input, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&components=country:ua");
        urlString.append("&sensor=false");
        urlString.append("&key=" + App.API_KEY_WEB);
        urlString.append("&location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=15000");
        // urlString.append("&language=en");
        return urlString.toString();
    }

    private String placeQueryAutocompletehUrl(double latitude,
                                              double longitude, String input) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/queryautocomplete/json?");
        try {
            urlString.append("&input=" + URLEncoder.encode(input, "utf-8"));
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
        return urlString.toString();
    }

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
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
}
