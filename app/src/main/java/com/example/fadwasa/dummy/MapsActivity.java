package com.example.fadwasa.dummy; /**
 * Created by Fadwasa on 31/01/2018 AD.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    static final int REQUEST_LOCATION=1;
    GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "perfect", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_maps);
            initMap();
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            getLocation();


        }

        //setUpMapIfNeeded();
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }
    Circle circle;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }


        mMap.setMyLocationEnabled(true);
        circle=drawCircle(new LatLng(latti,longi));
        startDemo();

/*
     LatLng sydney = new LatLng(-34, 151);
     mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Riyadh"));
     mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
     */
    }
    ////////////////////////
    private Circle drawCircle(LatLng latLng) {
        CircleOptions options=new CircleOptions().center(latLng).radius(1000).fillColor(0x33FF0000)
                .strokeColor(Color.GREEN).strokeWidth(3);

        return mMap.addCircle(options);
    }
    double latti,longi;
    void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                latti = location.getLatitude();
                longi = location.getLongitude();



                //setCircle(latti, longi);



            }
        }

    }

/*
    Circle circle;
    public void setCircle(double lat,double lng){
       circle=drawCircle(new LatLng(lat,lng));

    }
    */





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }

    }



    /////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\////////////
    private static final int ALT_HEATMAP_RADIUS = 10;

    /**
     * Alternative opacity of heatmap overlay
     */
    private static final double ALT_HEATMAP_OPACITY = 0.4;

    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();

     protected int getLayoutId() {
        return R.layout.activity_maps;
    }



     protected void startDemo() {
        //||||||||||||||||||||||||||||\\\\\\\\\\\\\\\\


        LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location=locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);



        //||||||||||||||||||||||||||||\\\\\\\\\\\\\\\\







       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude), 11));
        LatLng sydney = new LatLng(latitude, longtitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("There you are!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        try {
            mLists.put(getString(R.string.pocator_places), new DataSet(readItems(R.raw.places_r),
                    getString(R.string.police_stations_url)));

        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
        }
        mProvider = new HeatmapTileProvider.Builder().data(
                mLists.get(getString(R.string.pocator_places)).getData()).build();
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

        // Make the handler deal with the map
        // Input: list of WeightedLatLngs, minimum and maximum zoom levels to calculate custom
        // intensity from, and the map to draw the heatmap on
        // radius, gradient and opacity not specified, so default are used
    }
    ///////|||||||||||||||||||||||||||||||||||////////
    double longtitude;
    double latitude;
    public void onLocationChanged(Location location){

        longtitude=location.getLongitude();
        latitude=location.getLatitude();


    }

    /////////////||||||||||||||||||||////////////////////
    public void changeRadius(View view) {
        if (mDefaultRadius) {
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
        }
        mOverlay.clearTileCache();
        mDefaultRadius = !mDefaultRadius;
    }

    public void changeGradient(View view) {
        if (mDefaultGradient) {
            mProvider.setGradient(ALT_HEATMAP_GRADIENT);
        } else {
            mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
        }
        mOverlay.clearTileCache();
        mDefaultGradient = !mDefaultGradient;
    }

    public void changeOpacity(View view) {
        if (mDefaultOpacity) {
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
        } else {
            mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
        }
        mOverlay.clearTileCache();
        mDefaultOpacity = !mDefaultOpacity;
    }

    // Dealing with spinner choices


    // Datasets from http://data.gov.au
    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

    /**
     * Helper class - stores data sets and sources.
     */
    private class DataSet {
        private ArrayList<LatLng> mDataset;
        private String mUrl;

        public DataSet(ArrayList<LatLng> dataSet, String url) {
            this.mDataset = dataSet;
            this.mUrl = url;
        }

        public ArrayList<LatLng> getData() {
            return mDataset;
        }

        public String getUrl() {
            return mUrl;
        }
    }



    /////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\///////////////
}