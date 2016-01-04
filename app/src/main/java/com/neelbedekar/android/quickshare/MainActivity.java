package com.neelbedekar.android.quickshare;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import com.desmond.squarecamera.CameraActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.neelbedekar.android.quickshare.data.QuickshareContract;



public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, Callback {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private double latitude;
    private double longitude;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FloatingActionButton fab;
    private LocationReceiver locationReceiver;
    public static boolean isTwoPane;
    public static final String SHARED_PREFS = "MyPrefs";
    public static final String PREFS_USERNAME_KEY = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        if (!settings.contains(PREFS_USERNAME_KEY)) {
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, settings.getString(PREFS_USERNAME_KEY, " "), Toast.LENGTH_SHORT).show();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.fragment_container_detail) != null) {
            isTwoPane = true;
        }
        else {
            isTwoPane = false;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ContactListFragment())
                    .commit();
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
    }


    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            AddContactActivityFragment.latitude = latitude;
            AddContactActivityFragment.longitude = longitude;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(getString(R.string.location_receiver));
        locationReceiver = new LocationReceiver();
        registerReceiver(locationReceiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(locationReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id) {
        ContactDetailFragment frag = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putString(ContactDetailFragment.ID_KEY, id);
        frag.setArguments(args);
        if (!isTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, frag)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container_detail, frag)
                    .commit();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Connected to");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        AddContactActivityFragment.latitude = location.getLatitude();
        AddContactActivityFragment.longitude = location.getLongitude();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

}
