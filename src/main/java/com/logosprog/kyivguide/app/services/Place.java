package com.logosprog.kyivguide.app.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.logosprog.kyivguide.app.App;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

/**
 * Model class for Places data.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 *
 */

public class Place {

    String TAG = "Place";

    private String id = "empty";
    private String icon = "empty";
    // private String name = "empty";
    //private String vicinity = "empty";
    private Double latitude = 0.0;
    private Double longitude = 0.0;

    public String reference = "empty";

    JSONObject jsonDetailsPoint;

    private String formatted_address = "empty";
    private String formatted_phone_number = "empty";
    private String name = "emty";
    private String international_phone_number = "empty";

    private boolean havePhoto = false;
    private ArrayList<String> photoreferenceList = new ArrayList<String>();
    private String photoWidth = "emty";
    private String photoHeight = "emty";

    private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

    public Place(String Reference) {

        if (Reference != null && !Reference.equals("")) {
            setReference(Reference);
            String urlString = placeDetailsUrl(Reference);
            Log.e(TAG, "URL: " + urlString);
            String result = getUrlContents(urlString);// firing URL POST and
            // getting results
            System.out.println(result);

            try {
				/*
				 * JSONObject json = new JSONObject(result); JSONArray jsonArray
				 * = json.getJSONArray("results");
				 */
                jsonDetailsPoint = new JSONObject(result).getJSONObject("result");
                setLocation();
                setFormatedAddress();
                setFormattedPhoneNumber();
                setName();
                setInternationalPhoneNumber();
                setPhotoReference();
                setPhotoWidth();
                setPhotoHeight();

                if (havePhoto) {
                    for(int i=0; i<photoreferenceList.size(); i++){
                        //urlString = placePhotoUrl(photoreferenceList.get(i));
                        //Log.e(TAG, "PHOTO URL: " + urlString);
                        setPhoto(photoreferenceList.get(i));
                    }
                } else {
                    Log.e(TAG, "Nas Photo: " + havePhoto);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Place(Location location) {


    }

/*	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}



	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}



	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	 * public String getName() { return name; }





	 * public void setName(String name) { this.name = name; }


	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	*/

    // ==========================================================================================
    // Place Details Setters BEGIN
    // ==========================================================================================

    private void setReference(String reference) {
        this.reference = reference;
    }

    private void setLocation() {
        if (jsonDetailsPoint.has("geometry")) {
            try {
                JSONObject geometry = jsonDetailsPoint.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                this.latitude = (Double) location.get("lat");
                this.longitude = (Double) location.get("lng");
            } catch (JSONException ex) {
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
    }

    private void setFormatedAddress() {
        if (jsonDetailsPoint.has("formatted_address")) {
            try {
                formatted_address = jsonDetailsPoint
                        .getString("formatted_address");
                Log.d(TAG, "Formatted Address is: " + formatted_address);
            } catch (JSONException ex) {
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
    }

    private void setFormattedPhoneNumber() {
        if (jsonDetailsPoint.has("formatted_phone_number")) {
            try {
                formatted_phone_number = jsonDetailsPoint
                        .getString("formatted_phone_number");
                Log.d(TAG, "Formated Phone number is: "
                        + formatted_phone_number);
            } catch (JSONException ex) {
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
    }

    private void setName() {
        if (jsonDetailsPoint.has("name")) {
            try {
                this.name = jsonDetailsPoint.getString("name");
                Log.d(TAG, "Name is: " + name);
            } catch (JSONException ex) {
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
    }

    private void setInternationalPhoneNumber() {
        if (jsonDetailsPoint.has("international_phone_number")) {
            try {
                this.international_phone_number = jsonDetailsPoint
                        .getString("international_phone_number");
                Log.d(TAG, "Name is: " + international_phone_number);
            } catch (JSONException ex) {
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
    }

    private void setHavePhoto(boolean value) {
        this.havePhoto = value;
    }

    private void setPhotoReference() {
        boolean has = jsonDetailsPoint.has("photos");
        Log.d(TAG, "Has Photo: " + has);
        if (jsonDetailsPoint.has("photos")) {
            Log.d(TAG, "Has Photo: TRY begun!!!");
            try {
                JSONArray Jarray = jsonDetailsPoint.getJSONArray("photos");
                Log.d(TAG, "Has Photo: PHOTOS FOUND!!!");
                for(int i=0; i<Jarray.length(); i++){
                    JSONObject jsonPhotosPoint = Jarray.getJSONObject(i);
                    Log.d(TAG, "Has Photo: FIRST ARRAY ELEMENT CHOSEN!!!");
                    if (jsonPhotosPoint.has("photo_reference")) {
                        Log.d(TAG, "Has Photo: PHOTOREFERENCE FOUND!!!");
                        setHavePhoto(true);
                        photoreferenceList.add(jsonPhotosPoint.getString("photo_reference"));
                    }
                }
            } catch (JSONException ex) {
                setHavePhoto(false);
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        } else {
            setHavePhoto(false);
        }
    }

    private void setPhotoWidth() {
        if (this.havePhoto) {
            try {
                JSONArray Jarray = jsonDetailsPoint.getJSONArray("photos");
                JSONObject jsonPhotosPoint = Jarray.getJSONObject(0);
                photoWidth = jsonPhotosPoint.getString("width");
            } catch (JSONException ex) {
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
    }

    private void setPhotoHeight() {
        if (this.havePhoto) {
            try {
                JSONArray Jarray = jsonDetailsPoint.getJSONArray("photos");
                JSONObject jsonPhotosPoint = Jarray.getJSONObject(0);
                photoHeight = jsonPhotosPoint.getString("height");
            } catch (JSONException ex) {
                Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
    }

    private void setPhoto(String photoreference) {
        if (this.havePhoto) {
            String urlString = placePhotoUrl(photoreference);
            Log.e(TAG, "PHOTO URL: " + urlString);
            bitmapList.add(getUrlContentsPhoto(urlString));// firing URL POST and
            // getting results
        }
    }

    // ==========================================================================================
    // Place Details Setters END
    // ==========================================================================================

    // ==========================================================================================
    // Place Details GEtters BEGIN
    // ==========================================================================================

    public String getReference() {
        return reference;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getFormattedAddress() {
        return formatted_address;
    }

    public String getFormattedPhoneNumber() {
        return formatted_phone_number;
    }

    public String getName() {
        return this.name;
    }

    public String getInternationalPhoneNumber() {
        return international_phone_number;
    }

    public boolean havePhoto() {
        return this.havePhoto;
    }

    public ArrayList<String> getPhotoReferenceList() {
        return photoreferenceList;
    }

    public String getPhotoWidth() {
        return photoWidth;
    }

    public String getPhotoHeight() {
        return photoHeight;
    }

    public ArrayList<Bitmap> getBitmapList() {
        return bitmapList;
    }

    // ==========================================================================================
    // Place Details Getters END
    // ==========================================================================================

    private String placeDetailsUrl(String reference) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/details/json?");

        urlString.append("&sensor=false");
        urlString.append("&key=" + App.API_KEY_WEB);
        try {
            urlString.append("&reference="
                    + URLEncoder.encode(reference, "utf8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return urlString.toString();

    }

    private String placePhotoUrl(String phtoReference) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/photo?");

        urlString.append("&sensor=false");
        urlString.append("&maxwidth=512");
        urlString.append("&key=" + App.API_KEY_WEB);
        urlString.append("&photoreference=" + phtoReference);
        return urlString.toString();

    }

    private String getUrlContents(String theUrl) {
        StringBuilder jsonResults = new StringBuilder();
        HttpURLConnection conn = null;
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults.toString();
    }

    private Bitmap getUrlContentsPhoto(String theUrl) {
        try {
            URL url = new URL(theUrl);
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
            ByteArrayBuffer baf = new ByteArrayBuffer(100000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
                    (baf.toByteArray()).length);
            return bitmap;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

	/*
	 * static Place jsonPlaceSearch_To_PointReference(JSONObject jsonPoint){ try
	 * { Place place = new Place(); JSONObject geometry = (JSONObject)
	 * jsonPoint.get("geometry"); JSONObject location = (JSONObject)
	 * geometry.get("location"); place.setLatitude((Double)
	 * location.get("lat")); place.setLongitude((Double) location.get("lng"));
	 * place.setIcon(jsonPoint.getString("icon"));
	 * place.setName(jsonPoint.getString("name"));
	 * place.setVicinity(jsonPoint.getString("vicinity"));
	 * place.setId(jsonPoint.getString("id"));
	 * place.setReference(jsonPoint.getString("reference"));
	 *
	 * return place; } catch (JSONException ex) {
	 * Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex); }
	 * return null; }
	 */

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name
                + ", latitude=" + latitude + ", longitude=" + longitude
                + ", formatted_address=" + formatted_address
                + ", formatted_phone_number=" + formatted_phone_number + '}';
    }
}
