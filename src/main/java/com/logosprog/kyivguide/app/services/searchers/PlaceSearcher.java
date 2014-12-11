package com.logosprog.kyivguide.app.services.searchers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by forando on 11.12.14.
 */
public abstract class PlaceSearcher {

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

    public static final String PLACE_SEE = "see";
    public static final String PLACE_ATTRACTIONS = "attractions";
    public static final String PLACE_SHOPPING = "shopPing";
    public static final String PLACE_BEAUTY = "beauty";

    public static final String PLACE_HOTELS = "hotels";
    public static final String PLACE_CAFE = "cafe";
    public static final String PLACE_BARS = "bars";
    public static final String PLACE_RESTAURANTS = "restaurants";

    public static final String PLACE_ATM = "atm";
    public static final String PLACE_BANK = "bank";
    public static final String PLACE_AIRPORT = "airport";
    public static final String PLACE_GAS = "gas";

    //============================================================================================


    protected double latitude;
    protected double longitude;
    protected String input;
    protected String pageToken = null;

    protected ArrayList<PlaceSearchPoint> arrayList;

    protected ProgressDialog dialog;

    public PlaceSearcher(double latitude, double longitude, String input){
        this.latitude = latitude;
        this.longitude = longitude;
        this.input = input;

        arrayList = new ArrayList<PlaceSearchPoint>();
    }

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

    public abstract String createURLString();
    public abstract ArrayList<PlaceSearchPoint> getPlaceSearchPointList();
}
