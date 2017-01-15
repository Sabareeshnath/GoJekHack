package com.go.jek.godrive;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private MapView mapView;
    private GoogleMap googleMap;

    Button btnGarages;
    Button btnFuel;
    ImageView imgMore;
    ImageView imgInspection;
    ImageView imgTemp;
    ImageView imgNoise;

    SharedPreferences sharedpreferences;

    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude


    Gson gson;

    JSONObject obj;
    List<Garage> garages;
    List<GarageInfo> garageInfo;

    Sensors sensors_data;

    private FirebaseAnalytics firebaseAnalytics;



    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private ProgressDialog pDialog;
    private String extremeAreas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        firebaseAnalytics=FirebaseAnalytics.getInstance(this);

        sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        btnGarages= (Button) findViewById(R.id.buttonGarages);
        btnFuel= (Button) findViewById(R.id.buttonFuel);
        imgMore= (ImageView) findViewById(R.id.imgMore);
        imgInspection= (ImageView) findViewById(R.id.imgInspection);
        imgTemp= (ImageView) findViewById(R.id.imgTemp);
        imgNoise= (ImageView) findViewById(R.id.imgPressure);

        btnGarages.setOnClickListener(this);
        btnFuel.setOnClickListener(this);
        imgMore.setOnClickListener(this);



        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);





    }






    @Override
    protected void onResume() {
        super.onResume();

        getLocation();
        mapView.onResume();



//        new GetGarages().execute();

        Firebase.setAndroidContext(this);
        Firebase ref=new Firebase(Config.FIREBASE_URL_SENSORS);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sensors_data= dataSnapshot.getValue(Sensors.class);
                if(sensors_data.getNoise()>25){
                    imgNoise.setImageResource(R.drawable.noise_red);
                }else{
                    imgNoise.setImageResource(R.drawable.noise);
                }

                if(sensors_data.getTemperature()>25){
                    imgTemp.setImageResource(R.drawable.temp_red);
                }else{
                    imgTemp.setImageResource(R.drawable.temp);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        String restoredText = sharedpreferences.getString("inspection", null);

        if(restoredText!=null){
            if(restoredText.equals("on")){
                imgInspection.setImageResource(R.drawable.inspection_red);
            }else{
                imgInspection.setImageResource(R.drawable.inspection);
            }
        }

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

                //showSettingsAlert();

            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

//                if (isGPSEnabled) {
//                    if (location == null) {
//                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                        }
//                        locationManager.requestLocationUpdates(
//                                LocationManager.GPS_PROVIDER,
//                                MIN_TIME_BW_UPDATES,
//                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                        Log.d("GPS Enabled", "GPS Enabled");
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                latitude = location.getLatitude();
//                                longitude = location.getLongitude();
//                            }
//                        }
//                    }
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }



    public double getLatitude(){

        if(location != null){
            latitude = location.getLatitude();
        }


        return latitude;
    }


    public double getLongitude(){

        if(location != null){
            longitude = location.getLongitude();
        }


        return longitude;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getLatitude(), getLongitude()), 13));




            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(20)
                    .bearing(90)
                    .tilt(40)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));




            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .position(new LatLng(getLatitude(), getLongitude()))
                    .title("Current Location"));




//            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red))
//                    .position(new LatLng(getLatitude()+0.0013, getLongitude()+0.0003))
//                    .title("Others"));
//
//            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red))
//                    .position(new LatLng(getLatitude()+0.1008, getLongitude()+0.009))
//                    .title("Others"));
//
//            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red))
//                    .position(new LatLng(getLatitude()+0.0118, getLongitude()+0.0019))
//                    .title("Others"));
//
//            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red))
//                    .position(new LatLng(getLatitude()+0.0028, getLongitude()+0.0029))
//                    .title("Others"));



        }
    }


    public void addGarages(List<GarageInfo> obj){

//        try {
//            JSONArray jsonArray=obj.getJSONArray("garages");
//            System.out.print("");
//            Toast.makeText(MainActivity.this, ""+jsonArray, Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        String status;

        googleMap.clear();






        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .position(new LatLng(getLatitude(), getLongitude()))
                .title("Current Location"));

        for(int i=0;i<obj.size();i++){
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            if(obj.get(i).getStatus().equals("0")){
                status="available";
            }else{
                status="not available";
            }

            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.garage_marker))
                    .position(new LatLng(obj.get(i).getLatitude(), obj.get(i).getLongitude()))
                    .snippet(status+" , "+obj.get(i).getContactNo())

                    .title(obj.get(i).getName()));







        }

        googleMap.setOnMarkerClickListener(this);

//
//            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.garage_marker))
//                    .position(new LatLng(getLatitude()+0.1008, getLongitude()+0.009))
//                    .title("Others"));
//
//            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.garage_marker))
//                    .position(new LatLng(getLatitude()+0.0118, getLongitude()+0.0019))
//                    .title("Others"));
//
//            googleMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.garage_marker))
//                    .position(new LatLng(getLatitude()+0.0028, getLongitude()+0.0029))
//                    .title("Others"));

    }

    public void showFuelStations(List<Garage> obj){

        googleMap.clear();





        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .position(new LatLng(getLatitude(), getLongitude()))
                .title("Others"));

        for(int i=0;i<obj.size();i++){
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));


            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fuel_marker))
                    .position(new LatLng(obj.get(i).getLatitude(), obj.get(i).getLongitude()))
//                    .snippet(obj.get(i).getStatus()+" , "+obj.get(i).getContact_info())

//                    .title(obj.get(i).getGarage_name())
            );







        }

        googleMap.setOnMarkerClickListener(this);

//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
//
//        googleMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fuel_marker))
//                .position(new LatLng(getLatitude()+0.0013, getLongitude()+0.0003))
//                .title("Others"));
//
//        googleMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fuel_marker))
//                .position(new LatLng(getLatitude()+0.1008, getLongitude()+0.009))
//                .title("Others"));
//
//        googleMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fuel_marker))
//                .position(new LatLng(getLatitude()+0.0118, getLongitude()+0.0019))
//                .title("Others"));
//
//        googleMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fuel_marker))
//                .position(new LatLng(getLatitude()+0.0028, getLongitude()+0.0029))
//                .title("Others"));

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonGarages:

                System.out.print("");
                new GetGarages().execute();


                break;

            case R.id.buttonFuel:
                new GetFuel().execute();

                break;

            case R.id.imgMore:
                Intent intent = new Intent(MainActivity.this,MoreActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        GarageDetailFragment garageDetailFragment=new GarageDetailFragment();
        FragmentManager fragmentManager=getFragmentManager();
        garageDetailFragment.show(fragmentManager,"");

        return false;
    }



    private class GetGarages extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            //  pDialog.setTitle("Loading");
            pDialog.setMessage(" Loading..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("in bg", "");
            String url=Config.FIREBASE_URL_GETGARAGE;


            HttpURLConnection con=null;
            URL obj = null;

            try {
                obj = new URL(url);
                con = (HttpURLConnection) obj.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            int responseCode = 0;
            try {
                responseCode = con.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }


            BufferedReader in = null;
            StringBuffer response=null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //  Log.e("Response json",":"+response);

            String response_data=response.toString();
            //print result
            //  Log.e("Resp Res",":"+response.toString());

            return response_data;

        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);



            JSONArray jsonArray;
            try {
               // obj=new JSONObject(res);
                jsonArray=new JSONArray(res);
          //      jsonArray=obj.getJSONArray("garages");

                pDialog.dismiss();
                System.out.print(res);

            //    garages = new ArrayList<Garage>();
            //   garages = Arrays.asList(gson.fromJson(String.valueOf(jsonArray), Garage[].class));

                    garageInfo = new ArrayList<GarageInfo>();
                   garageInfo = Arrays.asList(gson.fromJson(String.valueOf(jsonArray), GarageInfo[].class));
                System.out.print(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            addGarages(garageInfo);




        }
    }

    private class GetFuel extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            //  pDialog.setTitle("Loading");
            pDialog.setMessage("Loading..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("in bg", "");
            String url=Config.FIREBASE_URL_GETFUEL;


            HttpURLConnection con=null;
            URL obj = null;

            try {
                obj = new URL(url);
                con = (HttpURLConnection) obj.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            int responseCode = 0;
            try {
                responseCode = con.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }


            BufferedReader in = null;
            StringBuffer response=null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //  Log.e("Response json",":"+response);

            String response_data=response.toString();
            //print result
            //  Log.e("Resp Res",":"+response.toString());

            return response_data;

        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);



            JSONArray jsonArray;
            try {
                obj=new JSONObject(res);
                jsonArray=obj.getJSONArray("garages");

                pDialog.dismiss();
                System.out.print(res);

                garages = new ArrayList<Garage>();
                garages = Arrays.asList(gson.fromJson(String.valueOf(jsonArray), Garage[].class));
                System.out.print(res);
                //  res_vStatus=obj.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            showFuelStations(garages);




        }
    }
}
