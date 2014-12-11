package com.logosprog.kyivguide.app.services.searchers;

/**
 * Created by forando on 11.12.14.
 * Realizes Factory Method Pattern
 */
public class PlaceSearcherFactory {

    public static PlaceSearcher newInstance(double latitude, double longitude, String input, String placeType){
        // Use the equals() method on a Marker to check for equals.  Do not use ==.
        if (placeType.equals(PlaceSearcher.PLACE_SEE)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_ATTRACTIONS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_SHOPPING)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_BEAUTY)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_HOTELS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_CAFE)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_BARS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_RESTAURANTS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_ATM)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_BANK)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_AIRPORT)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_GAS)) {
            return null;
        } else {
            // Passing 0 to setImageResource will clear the image view.
            return new PlaceTextSearcher(latitude, longitude, input);
        }
    }
}
