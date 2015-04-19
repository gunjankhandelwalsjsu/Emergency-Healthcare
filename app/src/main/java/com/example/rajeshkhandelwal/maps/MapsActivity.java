package com.example.rajeshkhandelwal.maps;



import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;
    private MarkerOptions[] places;
    private int otherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        otherIcon = R.drawable.purple_point;
        setUpMapIfNeeded();
       // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        placeMarkers = new Marker[MAX_PLACES];
        mLocationProvider = new LocationProvider(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            Log.i("map","isNull");

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.d("PLACES", "in handlenewLocation for latitude and longitude"+location.getLatitude());
    // String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&rankby=distance&types=hospital&key=AIzaSyCXSJG6fYQ-f5K1TAxHVo-MFC2oNUoD0b8";
//&rankby=distance
       String placesSearchStr= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.getLatitude()+","+location.getLongitude()+"&rankby=distance&types=hospital&key=AIzaSyCXSJG6fYQ-f5K1TAxHVo-MFC2oNUoD0b8";
      //  +currentLatitude+","+currentLongitude
        Log.d("placesSearchStr.............", "in handlenewLocation for latitude and longitude"+placesSearchStr);

        new GetPlaces().execute(placesSearchStr);
    }


    private class GetPlaces extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... placesURL) {
            Log.d("placesSearchStr*************", "in handlenewLocation for latitude and longitude"+placesURL[0]);
            String placeSearchURL=placesURL[0];
            StringBuilder placesBuilder = new StringBuilder();
            //process search parameter string(s)
          //  for (String placeSearchURL : placesURL) {

//execute search
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    Log.d("URL*************", "in handlenewLocation for latitude and longitude"+placeSearchURL);

                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    HttpResponse placesResponse = placesClient.execute(placesGet);
                    StatusLine placeSearchStatus = placesResponse.getStatusLine();
                    if (placeSearchStatus.getStatusCode() == 200) {
                        Log.d("PLACES", "in statusCode");

                        HttpEntity placesEntity = placesResponse.getEntity();
                        InputStream placesContent = placesEntity.getContent();
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            Log.d("PLACES", "in statusCode"+lineIn);

                            placesBuilder.append(lineIn);
                        }

//we have an OK responseHttpEntity placesEntity = placesResponse.getEntity();
                    }
                    //try to fetch the data
                }
                catch(Exception e){
                    e.printStackTrace();
                }
           // }
            return placesBuilder.toString();

        }
//fetch and parse place data

              protected void onPostExecute(String result) {
                  //parse place data returned from Google Places
                  //remove existing markers

                  if(placeMarkers!=null){
                      for(int pm=0; pm<placeMarkers.length; pm++){
                          if(placeMarkers[pm]!=null)
                              placeMarkers[pm].remove();
                      }
                  }
                  try {
                      //parse JSON

                      //create JSONObject, pass stinrg returned from doInBackground
                      JSONObject resultObject = new JSONObject(result);
                    //  Log.d("PLACES", "in onExecute"+result);

                      //get "results" array
                      JSONArray placesArray = new JSONArray(
                              resultObject.getString("results"));
                      places = new MarkerOptions[placesArray.length()];
                      //loop through places
                      Log.d("PLACES", "in onExecute"+placesArray.length());

                      for (int p=0; p<placesArray.length(); p++) {
                          //parse each place
                          //if any values are missing we won't show the marker
                 //         Log.d("PLACES", "in onExecute"+p);

                          boolean missingValue;
                          LatLng placeLL=null;
                          String placeName="";
                          String vicinity="";
                          int currIcon = otherIcon;
                          try{

                              //attempt to retrieve place data values
                              missingValue=false;
                              //get place at this index
                              JSONObject placeObject = placesArray.getJSONObject(p);
                              //get location section
                              JSONObject loc = placeObject.getJSONObject("geometry")
                                      .getJSONObject("location");
                              //read lat lng
                              placeLL = new LatLng(Double.valueOf(loc.getString("lat")),
                                      Double.valueOf(loc.getString("lng")));
                              //get types
                              JSONArray types = placeObject.getJSONArray("types");
                              //loop through types

                              //vicinity
                              vicinity = placeObject.getString("vicinity");
                              //name
                              placeName = placeObject.getString("name");
                          }
                          catch(JSONException jse){
                              Log.v("PLACES", "missing value");
                              missingValue=true;
                              jse.printStackTrace();
                          }
                          //if values missing we don't display
                          if(missingValue)	places[p]=null;
                          else
                              places[p]=new MarkerOptions()
                                      .position(placeLL)
                                      .title(placeName)
                                      .icon(BitmapDescriptorFactory.fromResource(currIcon))
                                      .snippet(vicinity);
                      }
                  }
                  catch (Exception e) {
                      e.printStackTrace();
                  }
                  if(places!=null && placeMarkers!=null){
                      for(int p=0; p<places.length && p<placeMarkers.length; p++){
                          //will be null if a value was missing
                          if(places[p]!=null)
                              placeMarkers[p]=mMap.addMarker(places[p]);
                      }
                  }

              }

          }
}

/*
<fragment xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:map="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/map"
        tools:context=".MapsActivity"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        map:cameraTilt="45"
        map:cameraZoom="14" />
*/