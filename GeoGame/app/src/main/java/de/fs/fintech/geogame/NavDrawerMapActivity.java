package de.fs.fintech.geogame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fs.fintech.geogame.service.CompassSensorService;
import de.fs.fintech.geogame.service.PlayerLocationService;
import de.fs.fintech.geogame.service.PortalMapService;

/**
 * Created by axel on 07.05.17.
 */

public class NavDrawerMapActivity extends NavDrawerActivity {

    private static final Logger log = LoggerFactory.getLogger(NavDrawerMapActivity.class);
    public static final String STATE_LAT = "lat";
    public static final String STATE_LNG = "lng";


    private PlayerLocationService.GeoFireLocationListener mLocationListener;
    private CompassSensorService.CompassSensorEventListener mSensorEventListener;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LatLng pos= mLocationListener.getMapPosition();
        if(pos!=null) {
            outState.putDouble(STATE_LAT, pos.latitude);
            outState.putDouble(STATE_LNG, pos.longitude);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        double lat=savedInstanceState.getDouble(STATE_LAT);
        double lng=savedInstanceState.getDouble(STATE_LNG);
        LatLng pos=new LatLng(lat,lng);
        mLocationListener.moveCameraToCurrentPosition(pos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // schon in super: setContentView(R.layout.activity_maps);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String email = prefs.getString("google.plus.email",null);
        //String gid = prefs.getString("google.plus.id",null);

        PlayerLocationService.UserStateHandler userStateHandler=new PlayerLocationService.UserStateHandler(email);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Intent intent = getIntent();
        float mapRadius = 0.4f; //(float) intent.getDoubleExtra(MapsActivity.EXTRA_MAP_RADIUS, PortalMapService.DEFAULT_INNER_RADIUS);
        long updateInterval = 300L; // intent.getLongExtra(MapsActivity.EXTRA_UPDATE_INTERVAL, PlayerLocationService.DEFAULT_UPDATE_INTERVAL);
        Class<? extends Activity> clsActivityDetails = PortalDetailsActivity.class;
//        if(mapRadius>1) {
            clsActivityDetails = PortalUniqueEditorActivity.class;
//        }

        PortalMapService.PortalMarkerDecorator deco=new PortalMapService.UniquePortalMarkerDecorator();
        final PortalMapService.PortalMapReadyListener portalMap = new PortalMapService.PortalMapReadyListener(this, mapRadius, clsActivityDetails,deco,userStateHandler);

        mLocationListener = new PlayerLocationService.GeoFireLocationListener(this,portalMap, mapRadius,updateInterval,true);

        mapFragment.getMapAsync(portalMap);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        CompassSensorService.OnCompassChangeListener compassChangeListener=new  CompassSensorService.OnCompassChangeListener() {

            @Override
            public void onAzimuthChange(float degrees) {
                portalMap.setMarkerDirection(degrees);
            }
        };
        mSensorEventListener = new CompassSensorService.CompassSensorEventListener(sensorManager,compassChangeListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationListener.stopLocationUpdates();

        mSensorEventListener.unregister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationListener.requestLocationUpdates(this);

        mSensorEventListener.register();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    log.info("granted");
                    mLocationListener.requestLocationUpdates(this);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    log.info("permission denied");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_position) {
            mLocationListener.moveCameraToCurrentPosition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
