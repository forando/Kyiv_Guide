package com.logosprog.kyivguide.app.services;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.utils.AdapterInfoWindow;

public class PlaceSearch extends AsyncTask<Void, Void, ArrayList<PlacesService.PlaceSearchPoint>>
        implements OnMarkerClickListener, OnInfoWindowClickListener{

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


    private final String TAG = getClass().getSimpleName();

    private ProgressDialog dialog;
    private Context context;
    private String searchAlgorithm;
    private String input;
    private String b_name;
    private GoogleMap map;
    private Location loc;
    HashMap<Marker, PlacesService.PlaceSearchPoint> markerPlaces;

    public PlaceSearch(Context context, String _searchAlgorithm, GoogleMap _map, Location _loc, String _input, String button_name) {
        this.context = context;
        this.searchAlgorithm = _searchAlgorithm;
        this.input = _input;
        this.b_name = button_name;
        this.map = _map;
        this.loc = _loc;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.isIndeterminate();
        dialog.show();
    }

    @Override
    protected ArrayList<PlacesService.PlaceSearchPoint> doInBackground(Void... params) {
        PlacesService service = new PlacesService(searchAlgorithm);

        //ArrayList<Place> arrayPlaces = service.findPlaces(loc.getLatitude(), loc.getLongitude(), places); // 28.632808   77.218276

        ArrayList<PlacesService.PlaceSearchPoint> arrayPlaces = null;

        if(searchAlgorithm.equals(PlacesService.NEARBY_SEARCH)){
            arrayPlaces = service.nearbySearch(loc.getLatitude(), loc.getLongitude(), input);
        }else if(searchAlgorithm.equals(PlacesService.TEXT_SEARCH)){
            arrayPlaces = service.textSearch(loc.getLatitude(), loc.getLongitude(), input);
        }

        if(arrayPlaces != null){
            for (int i = 0; i < arrayPlaces.size(); i++) {

                PlacesService.PlaceSearchPoint placeDetail = arrayPlaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
        }else{
            Log.e(TAG, "places result is empty.");
        }

        return arrayPlaces;
    }

    @Override
    protected void onPostExecute(ArrayList<PlacesService.PlaceSearchPoint> arrayPlaces) {
        super.onPostExecute(arrayPlaces);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if(arrayPlaces != null){
            addMarkersToMap(arrayPlaces);

            // Setting an info window adapter allows us to change the both the contents and look of the
            // info window.
            map.setInfoWindowAdapter(new AdapterInfoWindow(this.context, b_name));

            // Set listeners for marker events.  See the bottom of this class for their behavior.
            map.setOnMarkerClickListener(this);
            map.setOnInfoWindowClickListener(this);

					/*Log.e(TAG, "Long of 0 element = " + arrayPlaces.get(0).getLongitude() + "; Lat of 0 element = "
							+ arrayPlaces.get(0).getLatitude());*/
            CameraPosition cameraPosition;

            try {//åñëè ðåçóëüòàòû = null òî êîä â áëîêå try âûäàñò îøèáêó
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(arrayPlaces.get(0).getLatitude(), arrayPlaces.get(0).getLongitude())) // Sets the center of the map to Mountain View
                        .zoom(14) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build();
            } catch (Exception e) {
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(loc.getLatitude(), loc.getLongitude())) // Sets the center of the map to Mountain View 50.469356, 30.337684
                        .zoom(14) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
            }
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else{
            Toast.makeText(context, "No Results Found.", Toast.LENGTH_SHORT).show();
        }
    }


    private void addMarkersToMap(ArrayList<PlacesService.PlaceSearchPoint> arrayPlaces){


        markerPlaces = new HashMap<Marker, PlacesService.PlaceSearchPoint>();
        for (int i = 0; i < arrayPlaces.size(); i++) {

            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();


            // Setting the title for the marker.
            markerOptions.title(arrayPlaces.get(i).getName());
            // Setting the position for the marker
            markerOptions.position(new LatLng(arrayPlaces.get(i).getLatitude(), arrayPlaces.get(i).getLongitude()));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
            markerOptions.snippet(arrayPlaces.get(i).getVicinity());

            // Placing a marker on the touched position
            final Marker marker = map.addMarker(markerOptions);
            markerPlaces.put(marker, arrayPlaces.get(i));
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        // This causes the marker to bounce into position when it is clicked.
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();


        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(1 - interpolator
                        .getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //todo: uncomment later
        /*PlacesService.PlaceSearchPoint p = markerPlaces.get(marker);
        Intent i = new Intent(context, ActivityPlaceDetails.class);
        i.putExtra("reference", p.getReference());
        context.startActivity(i);*/

    }

}