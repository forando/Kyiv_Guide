package com.logosprog.kyivguide.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by forando on 26.11.14.
 */
public class App  extends Application{
    /**
     * Default lifespan (7 days) of a reservation until it is considered expired.
     */
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
    /**
     * Substitute you own sender ID here.
     */
    public static final String SENDER_ID = "1068249931061";
    /**
     * Google API key for my Application (Key for browser apps)
     */
    public static final String API_KEY_WEB = "AIzaSyDGKZxm2z8OnCIBMicpBeXzltx8kl3ZA28";
    /**
     * Directory where to store media content.
     */
    public static final String DIR_PATH = "GeorgiaGuide";

    /**
     * URL to the media directory on the SERVER.
     */
    public static final String DOWNLOAD_URL = "http://logosprog.com/georgiaguide/media/";

    /**
     * SharedPref key indicate if there is a new commercial content available to substitute the current one.
     */
    public static final String KEY_GOT_NEW_CONTENT = "got_new_content";

    /**
     * SharedPref key indicate if the new commercial content played successfully.
     */
    public static final String KEY_PLAYED_NEW_CONTENT = "played_new_content";

    /**
     * SharedPref key to store path to new media content file.
     */
    public static final String KEY_MEDIA_PATH = "media_path";

    /**
     * SharedPref key to store path to currently running media content file.
     */
    public static final String KEY_DEFAULT_MEDIA_PATH = "default_media_path";

    /**
     * The latitude of the City.
     */
    public static final double LATITUDE =  50.449853677749175;
    /**
     * The longitude of the City.
     */
    public static final double LONGITUDE =  30.52301287651062;

    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    Context CONTEXT;

    public App(){
    }

    public App(Context context){
        this.CONTEXT = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //prefs.registerOnSharedPreferenceChangeListener(this);
        editor = prefs.edit();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.registerOnSharedPreferenceChangeListener(this);
        editor = prefs.edit();

        CONTEXT = getApplicationContext();

    }
}
