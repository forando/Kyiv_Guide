package com.logosprog.kyivguide.app.services.searchers;

import java.util.ArrayList;

/**
 * A class that filters out places that contain specific key-words in its name<br/>
 * Implements Decorator Pattern.<br>
 * Created by forando on 18.12.14.
 */
public class PlacesFilterOut extends PlacesFilterDecorator {

    private String[] keys;

    /**
     *
     * @param input String with multiple keys separated with "|".<br/>
     *              Example: "store|gas|service"
     */
    public PlacesFilterOut(PlacesFilter placesFilter, String input) {
        super(placesFilter);
        this.keys = input.split("\\|");
    }

    @Override
    public ArrayList<PlaceSearchPoint> filter() {
        ArrayList<PlaceSearchPoint> placesFilterInput = super.filter();
        ArrayList<PlaceSearchPoint> placesFilterOutput = new ArrayList<PlaceSearchPoint>();

        for (PlaceSearchPoint placeInput : placesFilterInput) {
            boolean keyExist = false;
            String placeInputName = placeInput.getName().toLowerCase();
            for (String key : keys) {
                if (placeInputName.matches("(.*)?\\b" + key + ".*")) {
                    keyExist = true;
                    break;
                }
            }
            if (!keyExist) placesFilterOutput.add(placeInput);
        }

        return placesFilterOutput;
    }
}
