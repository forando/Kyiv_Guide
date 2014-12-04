package com.logosprog.kyivguide.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.fragments.delegates.MapDelegate;
import com.logosprog.kyivguide.app.services.PlaceDetails;
import com.logosprog.kyivguide.app.services.PlaceSearch;
import com.logosprog.kyivguide.app.services.PlacesService;

/**
 * A {@link Fragment} subclass.<br>
 * Activities that contain this fragment must implement the
 * {@link com.logosprog.kyivguide.app.activities.controllers.DelegateController} interface
 * to handle interaction events.<br>
 * Use the {@link Map#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Map extends Fragment implements MapDelegate {

    private static final String TAG = "MapFragment";

    Context activityContext;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LATITUDE = "lat";
    private static final String ARG_LONGITUDE = "lon";

    // TODO: Rename and change types of parameters
    private double lat;
    private double lon;

    private MapListener mListener;

    private UiSettings mUiSettings;
    private LocationManager locationManager;
    private Location loc;
    private Location tempLocation;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lat The kiosk latitude.
     * @param lon The kiosk longitude.
     * @return A new instance of fragment Map.
     */
    // TODO: Rename and change types and number of parameters
    public static Map newInstance(double lat, double lon) {
        Map fragment = new Map();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, lat);
        args.putDouble(ARG_LONGITUDE, lon);
        fragment.setArguments(args);
        return fragment;
    }
    public Map() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getDouble(ARG_LATITUDE);
            lon = getArguments().getDouble(ARG_LONGITUDE);
            activityContext = getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_map, container, false);

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

        if (mMap != null) {
            mUiSettings = mMap.getUiSettings();
            mUiSettings.setCompassEnabled(false);
            // mUiSettings = mMap.getUiSettings();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // TODO: uncomment this later
            //mListener = (MapListener) activity;
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
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
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
                .target(new LatLng(41.69275175761847, 44.81409441679716))
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
    public void searchText(String text) {
        new PlaceSearch(activityContext, PlacesService.TEXT_SEARCH, mMap, tempLocation,
                text, "dummy_text").execute();
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
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    };

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
    }

}
