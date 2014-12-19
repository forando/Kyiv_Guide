package com.logosprog.kyivguide.app.services.searchers;

import com.logosprog.kyivguide.app.App;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * A class that queries google.places API for Point_Of_Interest objects.<br>
 *
 * Created by forando on 12.12.14.
 */
public class PlaceNearbySearcherSee extends PlaceNearbySearcher {

    private final String TAG = getClass().getSimpleName();

    //public static final String PLACE_TYPE = "point_of_interest|art_gallery|aquarium|park|church|museum|painter|rv_park";
    private static final String PLACE_TYPE = "point_of_interest|art_gallery|aquarium|museum|painter|rv_park";

    /**
     * Keys for some place types that must be filtered out from list of desired places.
     */
    public static final String FILTER_KEYS = "учебн|ремонт|remont|фото|студ|реклам|тов|" +
            "ооо|сто|sto|компани|цех|майстер|мастер|интернет|школ|хостел|hostel";

    public PlaceNearbySearcherSee(double latitude, double longitude) {
        super(latitude, longitude, PLACE_TYPE);
    }

    @Override
    public String createURLString(double latitude, double longitude, int radius) {
        String result;
        if (PLACE_TYPE.equals("")) {//this is done only for init purpose
            StringBuilder urlString = new StringBuilder(
                    "https://maps.googleapis.com/maps/api/place/search/json?");
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

            result = urlString.toString();
        } else {
            result = super.createURLString(latitude, longitude, radius);
        }
        return result;
    }

    @Override
    public ArrayList<PlaceSearchPoint> getPlaces() {
        PlacesHolder places = new PlacesHolder(super.getPlaces());
        PlacesFilter placesFilter = new PlacesFilterOut(new PlacesFilterUnique(places), FILTER_KEYS);
        return placesFilter.filter();
    }
}
