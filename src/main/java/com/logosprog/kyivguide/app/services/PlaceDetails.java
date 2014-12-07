package com.logosprog.kyivguide.app.services;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;

public class PlaceDetails extends AsyncTask<Void, Void, Place> implements OnMarkerClickListener, OnInfoWindowClickListener{


    private final String TAG = getClass().getSimpleName();

    private ProgressDialog dialog;
    private Context context;
    private String searchAlgorithm;
    private String reference;
    private String b_name;
    private GoogleMap map;
    HashMap<Marker, Place> markerPlaces;

    public PlaceDetails(Context context, GoogleMap _map, String _reference, String badge_name) {
        this.context = context;
        this.reference = _reference;
        this.b_name = badge_name;
        this.map = _map;
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
    protected Place doInBackground(Void... params) {
        PlacesService service = new PlacesService(searchAlgorithm);

        //ArrayList<Place> arrayPlaces = service.findPlaces(loc.getLatitude(), loc.getLongitude(), places); // 28.632808   77.218276

        Place place = new Place(reference);

        if(place != null){
            Log.e(TAG, "places : " + place.getName());
        }else{
            Log.e(TAG, "There is no such a place.");
        }
        return place;
    }

    @Override
    protected void onPostExecute(Place place) {
        super.onPostExecute(place);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if(place != null){
            addMarkerToMap(place);

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
                        .target(new LatLng(place.getLatitude(), place.getLongitude())) // Sets the center of the map to Mountain View
                        .zoom(16) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build();
            } catch (Exception e) {
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(App.LATITUDE, App.LONGITUDE)) // Sets the center of the map to Mountain View 50.469356, 30.337684
                        .zoom(14) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
            }
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else{
            Toast.makeText(context, "No Results Found.", Toast.LENGTH_SHORT).show();
        }
    }


    private void addMarkerToMap(Place place){
        markerPlaces = new HashMap<Marker, Place>();
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();


        // Setting the title for the marker.
        markerOptions.title(place.getName());
        // Setting the position for the marker
        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
        markerOptions.snippet(place.getFormattedAddress());

        // Placing a marker on the touched position
        final Marker marker = map.addMarker(markerOptions);
        markerPlaces.put(marker, place);
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
        Place p = markerPlaces.get(marker);
        Intent i = new Intent(context, com.logosprog.kyivguide.app.activities.PlaceDetails.class);
        i.putExtra("reference", p.getReference());
        context.startActivity(i);

    }

}
