package com.logosprog.kyivguide.app.services.searchers;

import java.util.ArrayList;

/**
 * Created by forando on 19.12.14.
 */
public class PlaceNearbySearcherAttractions extends PlaceNearbySearcher {

    private final String TAG = getClass().getSimpleName();

    private static final String PLACE_TYPE = "zoo|amusement_park|bowling_alley|casino|night_club";

    /**
     * Keys for some place types that must be filtered out from list of desired places.
     */
    public static final String FILTER_KEYS = "учебн|ремонт|remont|фото|студ|реклам|тов|" +
            "ооо|сто|sto|компани|цех|майстер|мастер|интернет|школ|хостел|hostel";

    public PlaceNearbySearcherAttractions(double latitude, double longitude) {
        super(latitude, longitude, PLACE_TYPE);
    }

    @Override
    public ArrayList<PlaceSearchPoint> getPlaces() {
        PlacesHolder places = new PlacesHolder(super.getPlaces());
        PlacesFilter placesFilter = new PlacesFilterOut(new PlacesFilterUnique(places), FILTER_KEYS);
        return placesFilter.filter();
    }
}
