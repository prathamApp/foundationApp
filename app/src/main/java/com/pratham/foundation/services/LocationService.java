package com.pratham.foundation.services;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Status;
import com.pratham.foundation.utility.FC_Constants;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;


public class LocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationService.class.getSimpleName();
    Context context;
    GoogleApiClient mGoogleApiClient;

    public LocationService(Context context) {
        this.context = context;
    }

    private void startLocationListener() {
        //Track location every 15 seconds
        long mLocTrackingInterval = 1000 * 15; // 15 sec
        float trackingDistance = 0;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;
        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);
        SmartLocation.with(context)
                .location()
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        //Get latest location value(i.e. Latitiude and Longitude) and update the database
                        try {
                            Status statusObj = new Status();

                            AppDatabase.getDatabaseInstance(context).getStatusDao().updateValue("Latitude", ""+location.getLatitude());
//                        BaseActivity.statusDao.insert(statusObj);

                            AppDatabase.getDatabaseInstance(context).getStatusDao().updateValue("Longitude","" + location.getLongitude());

//                        BaseActivity.statusDao.insert(statusObj);

                            statusObj.setStatusKey("GPSDateTime");
                            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                            Date gdate = new Date(location.getTime());
                            String gpsDateTime = format.format(gdate);
                            statusObj.setValue("" + gpsDateTime);
                            AppDatabase.getDatabaseInstance(ApplicationClass.getInstance()).getStatusDao().updateValue("GPSDateTime", "" + gpsDateTime);

//                        BaseActivity.statusDao.insert(statusObj);
                            if(!ApplicationClass.LocationFlg) {
                                String requestString = generateRequestString();
                                pushDataToServer(requestString, FC_Constants.uploadDataUrl);
                            }
                            Log.d(TAG, "onLocationUpdated:" + location.getLatitude() + ":::" + location.getLongitude());

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                });
    }

    private String generateRequestString() {
        //Get the data from database and assign it to a string
        String requestString = "";
        try {
            JSONObject sessionObj = new JSONObject();
            JSONObject metaDataObj = new JSONObject();
            metaDataObj.put("ScoreCount", 0);

            metaDataObj.put("CRLID", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("CRLID"));
            metaDataObj.put("group1", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group1"));
            metaDataObj.put("group2", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group2"));
            metaDataObj.put("group3", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group3"));
            metaDataObj.put("group4", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group4"));
            metaDataObj.put("group5", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group5"));
            metaDataObj.put("DeviceId", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId"));
            metaDataObj.put("DeviceName", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceName"));
            metaDataObj.put("ActivatedDate", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("ActivatedDate"));
            metaDataObj.put("village", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("village"));
            metaDataObj.put("ActivatedForGroups", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("ActivatedForGroups"));
            metaDataObj.put("AndroidVersion", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("AndroidVersion"));
            metaDataObj.put("InternalAvailableStorage", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("InternalAvailableStorage"));
            metaDataObj.put("DeviceManufacturer", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceManufacturer"));
            metaDataObj.put("DeviceModel", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceModel"));
            metaDataObj.put("ScreenResolution", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("ScreenResolution"));
            metaDataObj.put("SerialID", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("SerialID"));
            metaDataObj.put("gpsFixDuration", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("gpsFixDuration"));
            metaDataObj.put("prathamCode", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("prathamCode"));
            metaDataObj.put("programId", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("programId"));
            metaDataObj.put("WifiMAC", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("wifiMAC"));
            metaDataObj.put("apkType", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("apkType"));
            metaDataObj.put("appName", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("appName"));
            metaDataObj.put("apkVersion", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("apkVersion"));
            metaDataObj.put("GPSDateTime", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("GPSDateTime"));
            metaDataObj.put("Latitude", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("Latitude"));
            metaDataObj.put("Longitude", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("Longitude"));
            metaDataObj.put("AndroidVersion", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("AndroidVersion"));
            metaDataObj.put("InternalAvailableStorage", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("InternalAvailableStorage"));
            metaDataObj.put("DeviceManufacturer", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceManufacturer"));
            metaDataObj.put("DeviceModel", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceModel"));
            metaDataObj.put("ScreenResolution", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("ScreenResolution"));

            sessionObj.put("scoreData", "[]");
            sessionObj.put("studentData", "[]");
            sessionObj.put("attendanceData", "[]");
            sessionObj.put("sessionsData", "[]");
            sessionObj.put("learntWordsData", "[]");
            sessionObj.put("logsData", "[]");
            sessionObj.put("assessmentData", "[]");
            sessionObj.put("supervisor", "[]");

            requestString = "{ \"session\": " + sessionObj +
                    ", \"metadata\": " + metaDataObj +
                    "}";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return requestString;
    }

    //Pass the requestString(i.e Data) and url to this method for pushing data to server
    private void pushDataToServer(String data, String url) {
        try {
            JSONObject jsonArrayData = new JSONObject(data);
            AndroidNetworking.post(url)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(jsonArrayData)
                    .build()
                    .getAsString(new StringRequestListener() {

                        @Override
                        public void onResponse(String response) {
                            //Success - Data push successs
                            // Log.d("onLocationUpdated", "Data pushed successfully");
                            ApplicationClass.LocationFlg = true;
                        }

                        @Override
                        public void onError(ANError anError) {
                            //Failure - Data push fail
                            Log.d("onLocationUpdated", "Data push failed");
                            ApplicationClass.LocationFlg = false;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkLocation() {
        //Check for current location
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //start fetching location
            startLocationListener();
        } else {
            //Show setting dialog to enable location of device
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API).addConnectionCallbacks(LocationService.this)
                    .addOnConnectionFailedListener(LocationService.this)
                    .build();
            mGoogleApiClient.connect();
            showSettingDialog();
        }
    }

    //Location Setting Dialog
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final com.google.android.gms.common.api.Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        startLocationListener();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) context, 10);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationListener();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
