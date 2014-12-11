package com.logosprog.kyivguide.app.services.searchers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by forando on 11.12.14.
 */
public class PlaceSearchPoint {

    String TAG = "PlaceSearchPoint";

    private String searchAlgorithm;

    JSONObject jsonPlaceSearchPoint;

    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private String formatted_address;
    private Double latitude;
    private Double longitude;

    private String reference;

    public PlaceSearchPoint(JSONObject jsonPoint, String searchAlgorithm) {

        this.jsonPlaceSearchPoint = jsonPoint;
        this.searchAlgorithm = searchAlgorithm;

        setId();
        setIcon();
        setLatitude();
        setLongitude();
        setName();
        setVicinity();
        setFormattedAddress();
        setReference();
    }

    public String getId() {
        return id;
    }

    private void setId() {
        try {
            this.id = jsonPlaceSearchPoint.getString("id");
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public String getIcon() {
        return icon;
    }

    private void setIcon() {
        try {
            this.icon = jsonPlaceSearchPoint.getString("icon");
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    private void setLatitude() {
        try {
            JSONObject geometry = (JSONObject) jsonPlaceSearchPoint
                    .get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            this.latitude = (Double) location.get("lat");
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public Double getLongitude() {
        return longitude;
    }

    private void setLongitude() {
        try {
            JSONObject geometry = (JSONObject) jsonPlaceSearchPoint.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            this.longitude = (Double) location.get("lng");
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }

    private void setName() {
        try {
            this.name = jsonPlaceSearchPoint.getString("name");
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public String getVicinity() {
        return vicinity;
    }

    private void setVicinity() {
        try {
            if(searchAlgorithm.equals(PlaceSearcher.NEARBY_SEARCH)){
                this.vicinity = jsonPlaceSearchPoint.getString("vicinity");
            }else{
                this.vicinity = jsonPlaceSearchPoint.getString("formatted_address");
            }
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public String getFormattedAddress() {
        return formatted_address;
    }

    private void setFormattedAddress() {
        try {
            if(searchAlgorithm.equals(PlaceSearcher.NEARBY_SEARCH)){
                this.formatted_address = jsonPlaceSearchPoint.getString("vicinity");
            }else{
                this.formatted_address = jsonPlaceSearchPoint.getString("formatted_address");
            }
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public String getReference() {
        return reference;
    }

    private void setReference() {
        try {
            this.reference = jsonPlaceSearchPoint.getString("reference");
        } catch (JSONException ex) {
            Logger.getLogger(PlaceSearchPoint.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name
                + ", latitude=" + latitude + ", longitude=" + longitude
                + '}';
    }

}
