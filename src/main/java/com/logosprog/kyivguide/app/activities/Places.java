package com.logosprog.kyivguide.app.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.fragments.Map;
import com.logosprog.kyivguide.app.fragments.delegates.MapDelegate;
import com.logosprog.kyivguide.app.services.searchers.PlaceSearcher;

/**
 * Activity to create and show strongly typed places
 * Created by forando on 26.11.14.
 */
public class Places extends FragmentActivity implements Map.MapListener {

    private final String TAG = getClass().getSimpleName();

    View contentView;
    View controlsView;

    int mControlsHeight;
    int mShortAnimTime;

    App getApp;

    TextView tv_quant;

    String current_place_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_places2);

        getApp = (App) getApplication();

        tv_quant = (TextView) findViewById(R.id.places_quantity);

        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentView = findViewById(R.id.fullscreen_content);


        mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        Fragment map = Map.newInstance(App.LATITUDE, App.LONGITUDE);
        transaction.replace(R.id.places_frame_map, map, "map").commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        delay_goBack(200);
    }

    public void b2_OnClick (View v){
        String placeType="";
        switch(v.getId()){
            case R.id.b_place_see:
                placeType = PlaceSearcher.PLACE_SEE;
                break;
            case R.id.b_place_attractions:
                placeType = PlaceSearcher.PLACE_ATTRACTIONS;
                break;
            case R.id.b_place_shopping:
                placeType = PlaceSearcher.PLACE_SHOPPING;
                break;
            case R.id.b_place_beauty:
                placeType = PlaceSearcher.PLACE_BEAUTY;
                break;
            case R.id.b_place_hotels:
                placeType = PlaceSearcher.PLACE_HOTELS;
                break;
            case R.id.b_place_cafe:
                placeType = PlaceSearcher.PLACE_CAFE;
                break;
            case R.id.b_place_bars:
                placeType = PlaceSearcher.PLACE_BARS;
                break;
            case R.id.b_place_restaurants:
                placeType = PlaceSearcher.PLACE_RESTAURANTS;
                break;
            case R.id.b_place_atm:
                placeType = PlaceSearcher.PLACE_ATM;
                break;
            case R.id.b_place_bank:
                placeType = PlaceSearcher.PLACE_BANK;
                break;
            case R.id.b_place_airport:
                placeType = PlaceSearcher.PLACE_AIRPORT;
                break;
            case R.id.b_place_gas:
                placeType = PlaceSearcher.PLACE_GAS;
                break;
        }
        if (!placeType.equals(current_place_type)) {
            current_place_type = placeType;
            mapDelegate.searchNearBy(current_place_type);
        }
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

    @Override
    public void OnMarkersAdded(int quantity) {
        tv_quant.setText(String.valueOf(quantity));
    }


}
