package com.logosprog.kyivguide.app.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.utils.SystemUiHider;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Main extends Base {
    private static final String TAG = "ActivityMain";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    public VideoView video;
    //private static final String MOOVIE_URL = "http://www.ex.ua/get/893429553880/63690479";

    /**
     * The Path to "shipped with app" moovie (located in src/raw folder).
     */
    private String APP_MOVIE_URL;

    /**
     * The Path to current movie.
     */
    private String DEFAULT_MOVIE_PATH;

    /**
     * The Path to new movie.
     */
    private String MOVIE_PATH;


    /**
     * The time of where the current moovie has been stopped
     */
    private int length = 0;

    View contentView;
    View controlsView;

    int mControlsHeight;
    int mShortAnimTime;

    boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        createDirIfNotExists();

        APP_MOVIE_URL = "android.resource://" + getPackageName() + "/" + R.raw.clipcanvas_14348_offline;

        video = (VideoView) findViewById(R.id.video);

        boolean got_ext_media = false;
        MOVIE_PATH = getApp.prefs.getString(getApp.KEY_MEDIA_PATH, null);
        if (MOVIE_PATH != null){
            got_ext_media = true;
        }

        boolean got_def_media = false;
        DEFAULT_MOVIE_PATH = getApp.prefs.getString(getApp.KEY_DEFAULT_MEDIA_PATH, null);
        if (DEFAULT_MOVIE_PATH != null){
            got_def_media = true;
        }
        if (got_ext_media){
            Log.d(TAG, "New File: " + MOVIE_PATH);

            video.setVideoPath(MOVIE_PATH);

        } else if (got_def_media){
            Log.d(TAG, "Default File: " + MOVIE_PATH);

            video.setVideoPath(DEFAULT_MOVIE_PATH);

        } else {
            Log.d(TAG, "KEY_GOT_NEW_CONTENT = false");
            Uri vid_Uri = Uri.parse(APP_MOVIE_URL);
            video.setVideoURI(vid_Uri);
        }

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                boolean got_ext_media = false;
                MOVIE_PATH = getApp.prefs.getString(getApp.KEY_MEDIA_PATH, null);
                Log.d(TAG, "File: " + MOVIE_PATH);
                if (MOVIE_PATH != null){
                    got_ext_media = true;
                }

                if (got_ext_media){
                    try
                    {
                        Log.d(TAG, "File: " + MOVIE_PATH);
                        //video.setVideoPath(Environment.getExternalStorageDirectory() + "/" + getApp.DIR_PATH + "/" + "Georgia.mp4");
                        video.setVideoPath(MOVIE_PATH);
                        video.start();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, "CATCHED ERROR: " + e.toString());

                        //send HTTPPOST "failed to play new media"
                        //delete new Media File

                        getApp.editor.putBoolean(getApp.KEY_GOT_NEW_CONTENT, false);
                        getApp.editor.putString(getApp.KEY_MEDIA_PATH, null);
                        getApp.editor.commit();


                        String defaultvideoPath = getApp.prefs.getString(getApp.KEY_DEFAULT_MEDIA_PATH, null);
                        if(defaultvideoPath != null){
                            DEFAULT_MOVIE_PATH = defaultvideoPath;
                            Log.d(TAG, "Setting back defaultVideoPath: " + DEFAULT_MOVIE_PATH);
                            video.setVideoPath(DEFAULT_MOVIE_PATH);
                            video.start();

                        }else{
                            Uri vid_Uri = Uri.parse(APP_MOVIE_URL);
                            video.setVideoURI(vid_Uri);
                            video.start();
                            String text = "Failed to play defiened media";
                            Toast.makeText(Main.this, text, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    mp.start();
                }

            }
        });

        try
        {
            video.start();

            //if the line above didn't through an ERROR, set New Media as Default Media
            if(got_ext_media){
                getApp.editor.putString(getApp.KEY_DEFAULT_MEDIA_PATH, MOVIE_PATH);
                getApp.editor.putBoolean(getApp.KEY_GOT_NEW_CONTENT, false);
                getApp.editor.putString(getApp.KEY_MEDIA_PATH, null);
                getApp.editor.commit();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "CATCHED ERROR: " + e.toString());

            //---!!!---send HTTPPOST "failed to play new media"
            //---!!!---delete new Media File

            getApp.editor.putBoolean(getApp.KEY_GOT_NEW_CONTENT, false);
            getApp.editor.putString(getApp.KEY_MEDIA_PATH, null);
            getApp.editor.commit();

            if(DEFAULT_MOVIE_PATH != null){
                Log.d(TAG, "Setting back defaultVideoPath: " + DEFAULT_MOVIE_PATH);
                video.setVideoPath(DEFAULT_MOVIE_PATH);
            }else{
                Uri vid_Uri = Uri.parse(APP_MOVIE_URL);
                video.setVideoURI(vid_Uri);

                //String text = "Failed to play defiened media";
                //Toast.makeText(ActivityMain.this, text, Toast.LENGTH_SHORT).show();
            }

            video.start();
        }

        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentView = findViewById(R.id.fullscreen_content);

        mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    public void video_Clicked(View v){
        Log.d(TAG, "Clicked!!!");
        hide_view(false);
        video.pause();
        delayedHide(5000);
    }

    private void hide_view(boolean hide){
        if (hide){
            //controlsView.setVisibility(View.GONE);
            controlsView.animate().translationY(mControlsHeight).setDuration(mShortAnimTime);
        }else{
            controlsView.animate().translationY(0).setDuration(mShortAnimTime);
            //controlsView.setVisibility(View.VISIBLE);
            delayedHide(5000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        video.pause();
        length = video.getCurrentPosition();
    }



    @Override
    protected void onResume() {
        super.onResume();
        video.seekTo(length);
        video.start();
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //mSystemUiHider.hide();
            mControlsHeight = controlsView.getHeight();
            hide_view(true);
            video.start();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void b_maps_Clicked(View v){
		/*
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
			    Uri.parse("geo:50.44,30.49"));
		//Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
			startActivity(intent);
		*/
        Intent intent = new Intent(this, Maps.class);
        //Intent intent = new Intent(this, ActivityMaps.class);
        //Intent intent = new Intent(this, Test.class);
        startActivity(intent);
    }

    public void b_places_Clicked(View v){
/*		int id = v.getId();
		String text = id == R.id.b_places ? "Places button Clicked" : "Nothing has been clicked";
		Toast.makeText(ActivityMain.this, text, Toast.LENGTH_SHORT).show();
		delayedHide(10000);*/
        /*Intent intent = new Intent(this, Places.class);
        startActivity(intent);*/

    }

    public void b_weather_Clicked(View v){
        /*Intent intent = new Intent(this, Weather.class);
        startActivity(intent);*/

    }
}
