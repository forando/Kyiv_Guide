package com.logosprog.kyivguide.app.services.searchers;

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

    public abstract ArrayList<PlaceSearchPoint> getPlaces();
}
