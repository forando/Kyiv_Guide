package com.logosprog.kyivguide.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.logosprog.kyivguide.app.App;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * Created by forando on 26.11.14.
 */
public class Base extends Activity{
    static final String TAG = "ActivityBase";

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

    static String DIR_PATH;

    App getApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base);

        //mDisplay = (TextView) findViewById(R.id.display);

        getApp = (App) getApplication();

        DIR_PATH = getApp.DIR_PATH;

        File root = android.os.Environment.getExternalStorageDirectory();

        context = getApplicationContext();
        regid = getRegistrationId(context);

        if (regid.length() == 0) {
            registerBackground();
        }
        gcm = GoogleCloudMessaging.getInstance(this);
    }

    /**
     * Gets the current registration id for application on GCM service.
     * <p>
     * If result is empty, the registration has failed.
     *
     * @return registration id, or empty string if the registration is not
     *         complete.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v(TAG, "Registration not found.");
            return "";
        }
        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || isRegistrationExpired()) {
            Log.v(TAG, "App version changed or registration expired.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(Base.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Checks if the registration has expired.
     *
     * <p>To avoid the scenario where the device sends the registration to the
     * server but the server loses it, the app developer may choose to re-register
     * after REGISTRATION_EXPIRY_TIME_MS.
     *
     * @return true if the registration has expired.
     */
    private boolean isRegistrationExpired() {
        final SharedPreferences prefs = getGCMPreferences(context);
        // checks if the information is not stale
        long expirationTime =
                prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration id, app versionCode, and expiration time in the
     * application's shared preferences.
     */
    private void registerBackground() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    regid = gcm.register(getApp.SENDER_ID);
                    msg = "Device registered, registration id=" + regid;
                    Log.e(TAG, regid);

                    new sendRegIDtoSerever().execute();
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the message
                    // using the 'from' address in the message.

                    // Save the regid - no need to register again.

                    setRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration id, app versionCode, and expiration time in the
     * application's {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration id
     */
    private void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.v(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + getApp.REGISTRATION_EXPIRY_TIME_MS;

        Log.v(TAG, "Setting registration expiry time to " +
                new Timestamp(expirationTime));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }

    private class sendRegIDtoSerever extends AsyncTask<Void, Void, String>{

        private ProgressDialog dialog;
        private Context context;
        private String places;

        private HttpClient httpclient;
        private HttpPost httppost;

        public sendRegIDtoSerever() {
            // Create a new HttpClient and Post Header
            this.httpclient = new DefaultHttpClient();
            this.httppost = new HttpPost("http://www.logosprog.com/georgiaguide/includes/register.php");

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("regId", regid));
            nameValuePairs.add(new BasicNameValuePair("name", "admin"));
            nameValuePairs.add(new BasicNameValuePair("email", "admin@gmail.com"));
            try {
                this.httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Loading..");
			dialog.isIndeterminate();
			dialog.show();*/
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                return response.toString();
            } catch (ClientProtocolException e) {
                return "ClientProtocolException = " + e ;
            } catch (IOException e) {
                return "IOException = " + e ;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            //mDisplay.append("\n" + response);
        }

    }

    public static boolean createDirIfNotExists() {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), DIR_PATH);

        Log.e(TAG, "New Folder = " + DIR_PATH);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }
}
