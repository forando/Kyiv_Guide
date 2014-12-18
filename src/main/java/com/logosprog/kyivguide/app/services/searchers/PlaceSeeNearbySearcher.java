package com.logosprog.kyivguide.app.services.searchers;

import com.logosprog.kyivguide.app.App;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by forando on 12.12.14.<br>
 *     A class that queries google.places API for Point_Of_Interest objects.
 */
public class PlaceSeeNearbySearcher extends PlaceNearbySearcher {

    private final String TAG = getClass().getSimpleName();

    //public static final String placeType = "point_of_interest|art_gallery|aquarium|park|church|museum|painter|rv_park";
    public static final String placeType = "point_of_interest|art_gallery|museum|painter|rv_park";

    public PlaceSeeNearbySearcher(double latitude, double longitude) {
        super(latitude, longitude);
    }

    @Override
    public String createURLString(double latitude, double longitude, int radius) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");

        if (placeType.equals("")) {//this is done only for init purpose
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=");
            urlString.append(radius);
            // urlString.append("&language=en");
            urlString.append("&types=restaurant");
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
        } else {
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
        }
        return urlString.toString();
    }

    @Override
    public ArrayList<PlaceSearchPoint> getPlaces() {
        ArrayList<PlaceSearchPoint> places = super.getPlaces();
        PlaceFilter placeFilter = new FilterUniquePlaces(places);
        return placeFilter.doFilter();
    }
}
