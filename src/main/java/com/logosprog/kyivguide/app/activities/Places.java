package com.logosprog.kyivguide.app.activities;

import java.util.ArrayList;
import java.util.HashMap;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.fragments.Map;
import com.logosprog.kyivguide.app.fragments.delegates.MapDelegate;
import com.logosprog.kyivguide.app.services.AdapterInfoWindow;
import com.logosprog.kyivguide.app.services.PlaceSearch;
import com.logosprog.kyivguide.app.services.PlacesService;

/**
 * Created by forando on 26.11.14.
 */
public class Places extends FragmentActivity implements Map.MapListener {




    private final String TAG = getClass().getSimpleName();

    /*private UiSettings mUiSettings;
    private GoogleMap mMap;*/
    private String[] places;

    /*private LocationManager locationManager;
    private Location loc;
    private Location tempLocation;*/


    HashMap<Marker, PlacesService.PlaceSearchPoint> markerPlaces;

    View contentView;
    View controlsView;

    int mControlsHeight;
    int mShortAnimTime;

    boolean visible;

    App getApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_places);
        setContentView(R.layout.activity_places2);

        getApp = (App) getApplication();


        //b_restaurant.setOnTouchListener(mDelayHideTouchListener);

        /*tempLocation = new Location("dummyprovider");
        tempLocation.setLatitude(getApp.LATITUDE);
        tempLocation.setLongitude(getApp.LONGITUDE);*/


        /*initMap();
        currentLocation();*/
        places = getResources().getStringArray(R.array.places);

        /*if (loc != null) {
            mMap.clear();
            new GetPlaces(ActivityPlaces.this, "restaurant".toLowerCase().
                    replace("-", "_").replace(" ", "_"), "restaurant").execute();

            new PlaceSearch(Places.this, PlacesService.NEARBY_SEARCH, mMap,
                    tempLocation, "restaurant", PlaceSearch.PLACE_RESTAURANTS).execute();
        }*/

		/*final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(
				this, R.array.places, android.R.layout.simple_list_item_1),
				new ActionBar.OnNavigationListener() {

					@Override
					public boolean onNavigationItemSelected(int itemPosition, long itemId) {
						Log.e(TAG, places[itemPosition].toLowerCase().replace("-", "_"));
						if (loc != null) {
							mMap.clear();
							new GetPlaces(ActivityPlaces.this, places[itemPosition].toLowerCase().replace("-", "_").replace(" ", "_"))
							.execute();
						}
						return true;
					}

				});*/


        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentView = findViewById(R.id.fullscreen_content);


        mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        Fragment map = Map.newInstance(App.LATITUDE, App.LONGITUDE);
        transaction.replace(R.id.places_frame_map, map, "map").commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        /*if (mMap != null) {
            mUiSettings = mMap.getUiSettings();
            mUiSettings.setCompassEnabled(false);
            //mUiSettings = mMap.getUiSettings();
        }*/
        //delay_goBack(7000);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
/*	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			mMap.clear();
			new GetPlaces(ActivityPlaces.this, "restaurant").execute();
			Log.e(TAG, "Button Clicked!!!");

			return false;
		}
	};
	*/
/*	public void b_OnClick (View v){
		String b_name="";
		String place_type = "restaurant";
		switch(v.getId()){
		case R.id.b_restaurant:
			place_type = "restaurant|cafe|bar";
			b_name = this.PLACE_RESTAURANT;
			Log.d(TAG, this.PLACE_RESTAURANT + " click");
			break;
		case R.id.b_shoping:
			place_type = "clothing_store|convenience_store|furniture_store|home_goods_store|shoe_store|shopping_mall";
			b_name = this.PLACE_SHOPING;
			Log.d(TAG, this.PLACE_SHOPING + " click");
			break;
		case R.id.b_bank:
			place_type = "bank|atm";
			b_name = this.PLACE_BANK;
			break;
		case R.id.b_point_of_interest:
			place_type = "art_gallery|amusement_park|park|aquarium|church|museum|painter|place_of_worship|rv_park|zoo";
			b_name = this.PLACE_POINT_OF_INTEREST;
			break;
		case R.id.b_hospital:
			place_type = "hospital|pharmacy";
			b_name = this.PLACE_HOSPITAL;
			break;
		case R.id.b_transport:
			place_type = "airport|bus_station|car_rental|subway_station|taxi_stand|train_station";
			b_name = this.PLACE_TRANSPORT;
			break;
		}
		if (loc != null) {
			mMap.clear();
			new GetPlaces(ActivityPlaces.this, place_type, b_name).execute();
		}
		togge_view(false);
	}*/

    public void b2_OnClick (View v){
        String b_name="";
        String place_type = "restaurant";
        switch(v.getId()){
            case R.id.b_place_see:
                place_type = "point_of_interest|art_gallery|aquarium|park|church|museum|painter|rv_park|zoo";
                b_name = PlaceSearch.PLACE_SEE;
                break;
            case R.id.b_place_attractions:
                place_type = "amusement_park|bowling_alley|casino|night_club";
                b_name = PlaceSearch.PLACE_ATTRACTIONS;
                break;
            case R.id.b_place_shopping:
                place_type = "shopping_mall|jewelry_store";
                b_name = PlaceSearch.PLACE_SHOPPING;
                break;
            case R.id.b_place_beauty:
                place_type = "beauty_salon|spa";
                b_name = PlaceSearch.PLACE_BEAUTY;
                break;
            case R.id.b_place_hotels:
                place_type = "lodging";
                b_name = PlaceSearch.PLACE_HOTELS;
                break;
            case R.id.b_place_cafe:
                place_type = "cafe";
                b_name = PlaceSearch.PLACE_CAFE;
                break;
            case R.id.b_place_bars:
                place_type = "bar";
                b_name = PlaceSearch.PLACE_BARS;
                break;
            case R.id.b_place_restaurants:
                place_type = "restaurant";
                b_name = PlaceSearch.PLACE_RESTAURANTS;
                break;
            case R.id.b_place_atm:
                place_type = "atm";
                b_name = PlaceSearch.PLACE_ATM;
                break;
            case R.id.b_place_bank:
                place_type = "bank";
                b_name = PlaceSearch.PLACE_BANK;
                break;
            case R.id.b_place_airport:
                place_type = "airport";
                b_name = PlaceSearch.PLACE_AIRPORT;
                break;
            case R.id.b_place_gas:
                place_type = "gas_station";
                b_name = PlaceSearch.PLACE_GAS;
                break;
        }
        /*if (loc != null) {
            mMap.clear();
            //new GetPlaces(ActivityPlaces.this, place_type, b_name).execute();
            new PlaceSearch(Places.this, PlacesService.NEARBY_SEARCH, mMap, tempLocation, place_type, b_name).execute();
        }*/
        mapDelegate.searchNearBy(place_type, b_name);
        togge_view(false);
    }

    public void b_map_hide_OnClick(View v){
        Log.d(TAG, "Cliked!!!");
        //togge_view(true);
        delay_goBack(0);
    }

    private void togge_view(boolean hide){
        if (hide){
            //controlsView.setVisibility(View.GONE);
            controlsView.animate().translationY(mControlsHeight).setDuration(mShortAnimTime);
        }else{
            controlsView.animate().translationY(0).setDuration(mShortAnimTime);
            //controlsView.setVisibility(View.VISIBLE);
            //delay_goBack(10000);
        }
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //togge_view(true);
            mControlsHeight = controlsView.getHeight();
            togge_view(true);
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delay_goBack(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private MapDelegate mapDelegate;

    @Override
    public void registerMapDelegate(MapDelegate delegate) {
        mapDelegate = delegate;
    }

    @Override
    public void showMap() {

    }

/*
    //================================================================================================
    // AsyncTask GetPlaces BEGIN
    //================================================================================================
    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<PlacesService.PlaceSearchPoint>> implements OnMarkerClickListener, OnInfoWindowClickListener{

        private ProgressDialog dialog;
        private Context context;
        private String places;
        private String b_name;

        public GetPlaces(Context context, String places, String button_name) {
            this.context = context;
            this.places = places;
            this.b_name = button_name;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<PlacesService.PlaceSearchPoint> doInBackground(Void... params) {
            PlacesService service = new PlacesService(PlacesService.NEARBY_SEARCH);

            Log.e(TAG, "places before request: " + places);

            //ArrayList<Place> arrayPlaces = service.findPlaces(loc.getLatitude(), loc.getLongitude(), places); // 28.632808   77.218276

            ArrayList<PlacesService.PlaceSearchPoint> arrayPlaces = service.nearbySearch(41.69275175761847, 44.81409441679716, places);

            for (int i = 0; i < arrayPlaces.size(); i++) {

                PlacesService.PlaceSearchPoint placeDetail = arrayPlaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }

            return arrayPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<PlacesService.PlaceSearchPoint> arrayPlaces) {
            super.onPostExecute(arrayPlaces);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            addMarkersToMap(arrayPlaces);

            // Setting an info window adapter allows us to change the both the contents and look of the
            // info window.
            mMap.setInfoWindowAdapter(new AdapterInfoWindow(this.context, b_name));

            // Set listeners for marker events.  See the bottom of this class for their behavior.
            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(this);

					*//*Log.e(TAG, "Long of 0 element = " + arrayPlaces.get(0).getLongitude() + "; Lat of 0 element = "
							+ arrayPlaces.get(0).getLatitude());*//*
            CameraPosition cameraPosition;

            try {//\E5\F1\EB\E8 \F0\E5\E7\F3\EB\FC\F2\E0\F2\FB = null \F2\EE \EA\EE\E4 \E2 \E1\EB\EE\EA\E5 try \E2\FB\E4\E0\F1\F2 \EE\F8\E8\E1\EA\F3
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
                final Marker marker = mMap.addMarker(markerOptions);
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
            PlacesService.PlaceSearchPoint p = markerPlaces.get(marker);
            Intent i = new Intent(context, PlaceDetails.class);
            i.putExtra("reference", p.getReference());
            context.startActivity(i);

        }

    }
    //================================================================================================
    // AsyncTask GetPlaces END
    //================================================================================================

    private void initMap() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_places)).getMap();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                //delay_goBack(10000);

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng arg0) {
                //delay_goBack(10000);

            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                //delay_goBack(10000);

            }
        });
    }

    private void currentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(new Criteria(), false);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) {
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        } else {
            loc = location;
            //new GetPlaces(ActivityPlaces.this, places[0].toLowerCase().replace("-", "_")).execute();
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

    };*/


}
