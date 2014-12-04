package com.logosprog.kyivguide.app.activities;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.services.PlaceDetails;
import com.logosprog.kyivguide.app.services.PlaceSearch;
import com.logosprog.kyivguide.app.services.PlacesService;
import com.logosprog.kyivguide.app.utils.SystemUiHider;

/**
 * @author logosprog
 *<h2>Description:</h2>
 * <p>Standard Google Map with possibility to find place by name or address.</p>
 * <p>Then you can go to ActivityPlaceDetailes (see description for that class)</p>
 */
public class ActivityMaps extends FragmentActivity implements
        OnItemClickListener {

    private final String TAG = getClass().getSimpleName();

    /**
     * If AUTOHIDE is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 15000;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private UiSettings mUiSettings;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location loc;
    private Location tempLocation;

    Context context;

    ArrayAdapter<String> adapter;

    SimpleAdapter s_adapter;
    List<HashMap<String, String>> places;

    LinearLayout search_edit_frame;
    AutoCompleteTextView input;
    ImageButton btn_go;
    ImageButton btn_delete;

    // EditText input1;

    App getApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_maps);

        getApp = (App) getApplication();


        //b_restaurant.setOnTouchListener(mDelayHideTouchListener);

        tempLocation = new Location("dummyprovider");
        tempLocation.setLatitude(getApp.LATITUDE);
        tempLocation.setLongitude(getApp.LONGITUDE);

        context = this;

        search_edit_frame = (LinearLayout) findViewById(R.id.search_edit_frame);
        btn_go = (ImageButton) findViewById(R.id.search_go_btn);
        btn_delete = (ImageButton) findViewById(R.id.search_delete_btn);

        input = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        input.setThreshold(1);

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                new GetPredictions().execute(s.toString());

            }

        });

        //In landscape mode, adding listener to keyboard "SEARCH" button
        //this is only for phones and will not be necessary for tablets.
        input.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionID, KeyEvent event) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH){
                    if(!input.getText().toString().equals("")){
                        mMap.clear();
                        new PlaceSearch(context, PlacesService.TEXT_SEARCH, mMap, tempLocation, input.getText().toString(), "dummy_text").execute();
                    }

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    //remove focus frome searchBar
                    findViewById(R.id.layout_map).requestFocus();

                    return true;
                }else{
                    return false;
                }
            }});

        input.setOnItemClickListener(this);

        String[] countries = getResources().getStringArray(
                R.array.countries_array);

        // adapter = new ArrayAdapter<String>(context,
        // android.R.layout.simple_list_item_1, countries);

		/*
		 * adapter = new ArrayAdapter<String>(context, R.layout.list_maps_row,
		 * countries); input.setAdapter(adapter);
		 *
		 * adapter.notifyDataSetChanged();
		 */

        // mMap = ((MapFragment)
        // getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                delay_goBack(15000);

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng arg0) {
                delay_goBack(5000);

            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                delay_goBack(15000);

            }
        });

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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mMap != null) {
            mUiSettings = mMap.getUiSettings();
            mUiSettings.setCompassEnabled(false);
            // mUiSettings = mMap.getUiSettings();
        }
        // Trigger the initial goBack method
        delay_goBack(20000);
    }




    public void search_go_btn_OnClick(View v) {

        if(!input.getText().toString().equals("")){
            mMap.clear();
            new PlaceSearch(context, PlacesService.TEXT_SEARCH, mMap, tempLocation, input.getText().toString(), "dummy_text").execute();

            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

            //remove focus frome searchBar
            findViewById(R.id.layout_map).requestFocus();
        }
    }

    public void search_delete_btn_OnClick(View v) {

        input.setText("");

        LayoutParams btn_delete_params = btn_delete.getLayoutParams();
        btn_delete_params.width = 0;
        btn_delete.setLayoutParams(btn_delete_params);

        LayoutParams btn_go_params = btn_go.getLayoutParams();
        btn_go_params.width = LayoutParams.WRAP_CONTENT;
        btn_go.setLayoutParams(btn_go_params);
    }




    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            goBack();
        }
    };

    /**
     * Schedules goBack call in [delay] milliseconds, canceling any previously
     * scheduled calls.
     */
    private void delay_goBack(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void goBack() {
        //super.onBackPressed();
    }

    private void currentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

    //=================================================================================
    //AsyncTask<String GetPredictions BEGIN
    //=================================================================================
    public class GetPredictions extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... inputs) {
            PlacesService service = new PlacesService(null);
            // ArrayList<String> predictionsList =
            // service.placeQueryAutocomplete(41.69275175761847,
            // 44.81409441679716, inputs[0]);
            List<HashMap<String, String>> predictions = service.placeAutocomplete(App.LATITUDE, App.LONGITUDE, inputs[0]);
            return predictions;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> predictions) {
            super.onPostExecute(predictions);
            if (predictions != null) {

                String[] FROM = new String[] { "description" };
                int[] TO = new int[] { R.id.text1 };

                // Creating a SimpleAdapter for the AutoCompleteTextView
                s_adapter = new SimpleAdapter(getBaseContext(), predictions,
                        R.layout.list_maps_row, FROM, TO);

                // Setting the adapter
                input.setAdapter(s_adapter);
                s_adapter.notifyDataSetChanged();
            }
        }

    }
    //=================================================================================
    //AsyncTask<String GetPredictions END
    //=================================================================================

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position,
                            long id) {
        HashMap<String, String> hm = (HashMap<String, String>) adapterView.getItemAtPosition(position);

        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

        findViewById(R.id.layout_map).requestFocus();

        LayoutParams btn_go_params = btn_go.getLayoutParams();
        btn_go_params.width = 0;
        btn_go.setLayoutParams(btn_go_params);

        LayoutParams btn_delete_params = btn_delete.getLayoutParams();
        btn_delete_params.width = LayoutParams.WRAP_CONTENT;
        btn_delete.setLayoutParams(btn_delete_params);

        if(!input.getText().toString().equals("")){
            mMap.clear();
            String referense = hm.get("reference");
            new PlaceDetails(context, mMap, referense, "dummy_text").execute();
            //new PlaceSearch(context, PlacesService.TEXT_SEARCH, mMap, tempLocation, input.getText().toString(), "dummy_text").execute();
        }

        //Toast.makeText(this, hm.get("description"), Toast.LENGTH_SHORT).show();

    }
}
