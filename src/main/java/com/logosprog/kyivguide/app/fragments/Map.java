package com.logosprog.kyivguide.app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.fragments.delegates.MapDelegate;
import com.logosprog.kyivguide.app.services.*;
import com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint;
import com.logosprog.kyivguide.app.services.searchers.PlaceSearchQueryExecutor;
import com.logosprog.kyivguide.app.services.searchers.PlaceSearcher;
import com.logosprog.kyivguide.app.services.searchers.PlaceSearcherFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link com.google.android.gms.maps.SupportMapFragment} subclass.<br>
 *     Activities that contain this fragment must implement
 *     {@link com.logosprog.kyivguide.app.fragments.Map.MapListener}
 *     interface.
 * Use the {@link Map#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Map extends SupportMapFragment implements MapDelegate,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "MapFragment";

    Context activityContext;
    Map mapContext;

    private static final String ARG_LATITUDE = "lat";
    private static final String ARG_LONGITUDE = "lon";

    private double lat;
    private double lon;

    private MapListener mListener;

    private UiSettings mUiSettings;
    private LocationManager locationManager;
    private Location loc;
    private Location tempLocation;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    HashMap<Marker, PlaceSearchPoint> markerPlaces;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lat The kiosk latitude.
     * @param lon The kiosk longitude.
     * @return A new instance of fragment Map.
     */
    public static Map newInstance(double lat, double lon) {
        Map fragment = new Map();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, lat);
        args.putDouble(ARG_LONGITUDE, lon);
        fragment.setArguments(args);
        return fragment;
    }
    public Map() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getDouble(ARG_LATITUDE);
            lon = getArguments().getDouble(ARG_LONGITUDE);
            activityContext = getActivity();
            mapContext = this;

            /*if (loc != null) {
                mMap.clear();
            *//*new GetPlaces(ActivityPlaces.this, "restaurant".toLowerCase().
                    replace("-", "_").replace(" ", "_"), "restaurant").execute();*//*

                new PlaceSearch(Places.this, PlacesService.NEARBY_SEARCH, mMap,
                        tempLocation, "restaurant", PlaceSearch.PLACE_RESTAURANTS).execute();
            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        // Inflate the layout for this fragment
        //View fragment = inflater.inflate(R.layout.fragment_map, container, false);
        View fragment = super.onCreateView(inflater, container, savedInstanceState);

        tempLocation = new Location("dummyprovider");
        tempLocation.setLatitude(lat);
        tempLocation.setLongitude(lon);

        setUpMapIfNeeded();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MapListener listener = (MapListener) getActivity();
        listener.registerMapDelegate(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link MapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            Log.d(TAG, "Set up Map!");
            mMap = getMap();
            mUiSettings = mMap.getUiSettings();
            mUiSettings.setCompassEnabled(false);
            // mUiSettings = mMap.getUiSettings();
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker"));
        currentLocation();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lon))
                        // .target(new LatLng(loc.getLatitude(), loc.getLongitude())) //
                        // Sets the center of the map to Mountain View
                .zoom(14) // Sets the zoom
                .tilt(30) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void moveTo(double lat, double lon) {

    }

    @Override
    public void clearMap() {
        mMap.clear();
    }

    @Override
    public void searchReference(String reference) {
        new PlaceDetails(activityContext, mMap, reference, "dummy_text").execute();
    }

    @Override
    public void searchText(String input) {
        //new PlaceSearchExecutor(PlaceSearcher.TEXT_SEARCH, input, "dummy_text").execute();
        new PlaceSearchExecutor(input, "dummy_text").execute();
    }

    @Override
    public void searchNearBy(String input, String placeType) {
        /*if (loc != null){
            mMap.clear();
            new PlaceSearch(activityContext, PlacesService.NEARBY_SEARCH, mMap, tempLocation, input, placeType).execute();
        }*/
        mMap.clear();
        new PlaceSearchExecutor(PlaceSearcher.NEARBY_SEARCH, input, placeType).execute();
    }

    @Override
    public void getDirections(Place place, String mode) {
        new DirectionsExecutor(place, mode).execute();
    }

    private void currentLocation() {
        locationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager
                .getBestProvider(new Criteria(), false);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) {
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        } else {
            loc = location;
            Log.e(TAG, "location : " + location);
        }

    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location update : " + location);
            loc = location;
            locationManager.removeUpdates(listener);

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    };

    private void addMarkersToMap(ArrayList<PlaceSearchPoint> arrayPlaces){


        markerPlaces = new HashMap<Marker, PlaceSearchPoint>();
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
            final Marker marker = mMap.addMarker(markerOptions);
            markerPlaces.put(marker, arrayPlaces.get(i));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        PlaceSearchPoint p = markerPlaces.get(marker);
        Intent i = new Intent(activityContext, com.logosprog.kyivguide.app.activities.PlaceDetails.class);
        Log.i(TAG, "reference = " + p.getReference());
        i.putExtra("reference", p.getReference());
        activityContext.startActivity(i);
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


    //============================AsyncTasks  BEGIN===========================================

    private class DirectionsExecutor extends AsyncTask<Void, Void, List<List<HashMap<String,String>>>> {

        private DirectionsService directionsService;

        Place place;
        private final LatLng origin;
        private final LatLng dest;

        public DirectionsExecutor(Place _place, String _mode){
            this.origin = new LatLng(App.LATITUDE, App.LONGITUDE);
            this.dest = new LatLng(_place.getLatitude(), _place.getLongitude());
            this.place = _place;
            directionsService = new DirectionsService(origin, dest, null, _mode);
        }



        @Override
        protected List<List<HashMap<String, String>>> doInBackground(Void... params) {
            return directionsService.getRoutes();
        }



        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            super.onPostExecute(routes);
            int route = 0;
            int _latlong = 0;
            List<LatLng> points = new ArrayList<LatLng>();
            for (List<HashMap<String, String>> path : routes) {
                route++;
                for(HashMap<String, String> hm_latlong : path){
                    points.add(new LatLng(Double.parseDouble(hm_latlong.get("lat")),
                            Double.parseDouble(hm_latlong.get("lng"))));
                    _latlong++;

                    Log.d(TAG, "Route#" + route + ", Point#" + _latlong + ", Latitude = " + hm_latlong.get("lat") +
                            ", Longitude = " + hm_latlong.get("lng"));
                }
            }
            mMap.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .width(7)
                    .color(Color.BLUE)
                    .geodesic(true));//ïîâòîðÿåò êðèâèçíó Çåìëè

            addEndPointMarker(origin, R.drawable.ic_maps_indicator_startpoint_route,
                    "You are here", "InfoBox location");
            addEndPointMarker(dest, R.drawable.ic_maps_indicator_endpoint_route,
                    place.getName(), place.getFormattedAddress());

            mListener.showMap();

            CameraPosition cameraPosition;
            cameraPosition = new CameraPosition.Builder()
                    .target(points.get(0)) // Sets the center of the map to Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        private void addEndPointMarker(LatLng point, int icon_res_id, String title, String snippet){
            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();

            // Setting the position for the marker
            markerOptions.position(point);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(icon_res_id));
            markerOptions.title(title);
            markerOptions.snippet(snippet);

            // Placing a marker
            mMap.addMarker(markerOptions);
        }

    }

    private class PlaceSearchExecutor extends AsyncTask<Void, Void, ArrayList<PlaceSearchPoint>>{

        private final String TAG = getClass().getSimpleName();

        private ProgressDialog dialog;

        private String searchAlgorithm;
        private String input;
        private String placeType;

        public PlaceSearchExecutor(String searchAlgorithm, String input, String placeType){
            this.searchAlgorithm = searchAlgorithm;
            this.input = input;
            this.placeType = placeType;
        }

        public PlaceSearchExecutor(String input, String placeType){
            this(PlaceSearcher.TEXT_SEARCH, input, placeType);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(activityContext);
            dialog.setCancelable(false);
            dialog.setMessage("Loading...");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<PlaceSearchPoint> doInBackground(Void... voids) {
            PlacesService service = new PlacesService(searchAlgorithm);
            PlaceSearcher placeSearcher = PlaceSearcherFactory.newInstance(lat, lon, input, placeType);
            //ArrayList<Place> arrayPlaces = service.findPlaces(loc.getLatitude(), loc.getLongitude(), places); // 28.632808   77.218276

            ArrayList<PlaceSearchPoint> arrayPlaces = null;

            if(searchAlgorithm.equals(PlaceSearcher.NEARBY_SEARCH)){
                arrayPlaces = service.nearbySearch(loc.getLatitude(), loc.getLongitude(), input);
            }else if(searchAlgorithm.equals(PlaceSearcher.TEXT_SEARCH)){
                //arrayPlaces = service.textSearch(loc.getLatitude(), loc.getLongitude(), input);
                arrayPlaces = placeSearcher.getPlaceSearchPointList();
            }

            if(arrayPlaces != null){
                for (int i = 0; i < arrayPlaces.size(); i++) {

                    PlaceSearchPoint placeDetail = arrayPlaces.get(i);
                    Log.e(TAG, "places : " + placeDetail.getName());
                }
            }else{
                Log.e(TAG, "places result is empty.");
            }

            return arrayPlaces;
        }


        @Override
        protected void onPostExecute(ArrayList<PlaceSearchPoint> arrayPlaces) {
            super.onPostExecute(arrayPlaces);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(arrayPlaces != null){
                addMarkersToMap(arrayPlaces);

                // Setting an info window adapter allows us to change the both the contents and look of the
                // info window.
                mMap.setInfoWindowAdapter(new AdapterInfoWindow(activityContext, placeType));

                // Set listeners for marker events.  See the bottom of this class for their behavior.
                mMap.setOnMarkerClickListener(mapContext);
                mMap.setOnInfoWindowClickListener(mapContext);

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
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }else{
                Toast.makeText(activityContext, "No Results Found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //============================AsyncTasks END===========================================

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface MapListener {
        public void registerMapDelegate(MapDelegate delegate);
        public void showMap();
    }

}
