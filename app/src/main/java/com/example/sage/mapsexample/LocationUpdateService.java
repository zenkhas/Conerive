package com.example.sage.mapsexample;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationUpdateService extends Service {
    private static final String TAG = "LocationUpdateService";

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private DatabaseReference ownerReference;
    private FirebaseDatabase database;
    private GeoFire ownerGeoFireObject;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ownerReference = database.getReference("USERS/"+currentUser.getUid());
        ownerGeoFireObject = new GeoFire(ownerReference);

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
                    mLastKnownLocation=location;
                    ownerGeoFireObject.setLocation("Location", new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));

                }
            }
        };

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + "service started");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
