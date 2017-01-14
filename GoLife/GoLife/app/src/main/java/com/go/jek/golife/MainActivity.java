package com.go.jek.golife;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, MqttCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    Button btnGarages;
    Button btnFuel;

    LinearLayout bookNow ;
    Button btnBookNow;


    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    /*MQTT Block*/

    String topic        = "GOJEKTOPIC";
  //  String content      = "Message from MqttPublishSample";
    int qos             = 2;
    String broker       = "tcp://52.59.15.99:1883";
    String clientId     = "GOJEK";
    MqttClient sampleClient = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookNow = (LinearLayout)findViewById(R.id.linearL);
        btnBookNow = (Button) findViewById(R.id.bookNow);
        btnGarages = (Button) findViewById(R.id.buttonGarages);
        btnFuel = (Button) findViewById(R.id.buttonFuel);

        btnGarages.setOnClickListener(this);
        btnFuel.setOnClickListener(this);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        bookNow.setVisibility(View.INVISIBLE);

        /*MQTT Initilaization*/


        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }




    }

    @Override
    protected void onResume() {
        super.onResume();

        getLocation();
        mapView.onResume();

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


    public double getLatitude() {

        if (location != null) {
            latitude = location.getLatitude();
        }


        return latitude;
    }


    public double getLongitude() {

        if (location != null) {
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
                    .title("Others"));


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


    public void showcabs() {

        bookNow.setVisibility(View.VISIBLE);
        googleMap.clear();
        new GetGarages().execute();

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cabs))
                .position(new LatLng(getLatitude() + 0.0023, getLongitude() + 0.0003))
                .title("Others"));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cabs))
                .position(new LatLng(getLatitude() + 0.1108, getLongitude() + 0.009))
                .title("Others"));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cabs))
                .position(new LatLng(getLatitude() + 0.0118, getLongitude() + 0.0019))
                .title("Others"));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cabs))
                .position(new LatLng(getLatitude() + 0.0028, getLongitude() + 0.0029))
                .title("Others"));

        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String content="Car Ride - Domlur";

                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(true);
                    System.out.println("Connecting to broker: "+broker);
                    sampleClient.connect(connOpts);

                    System.out.println("Connected");

                    System.out.println("Publishing message: "+content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    sampleClient.publish(topic, message);
                    System.out.println("Message published");

                    sampleClient.disconnect();
                    System.out.println("Disconnected");
                    //System.exit(0);

                } catch(MqttException me) {
                    System.out.println("reason "+me.getReasonCode());
                    System.out.println("msg "+me.getMessage());
                    System.out.println("loc "+me.getLocalizedMessage());
                    System.out.println("cause "+me.getCause());
                    System.out.println("excep "+me);
                    me.printStackTrace();
                }
            }
        });

    }

    public void showBikes() {


        bookNow.setVisibility(View.VISIBLE);
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));


        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
                .position(new LatLng(getLatitude() + 0.0053, getLongitude() + 0.0013))
                .title("Others"));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
                .position(new LatLng(getLatitude() + 0.1048, getLongitude() + 0.009))
                .title("Others"));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
                .position(new LatLng(getLatitude() + 0.0218, getLongitude() + 0.0019))
                .title("Others"));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
                .position(new LatLng(getLatitude() + 0.0128, getLongitude() + 0.0029))
                .title("Others"));

        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content="Bike Ride - Domlur";

                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                System.out.println("Connecting to broker: "+broker);
                try {
                    sampleClient.connect(connOpts);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                System.out.println("Connected");

                try {

                    System.out.println("Publishing message: "+content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    sampleClient.publish(topic, message);
                    System.out.println("Message published");

                    sampleClient.disconnect();
                    System.out.println("Disconnected");
                    //System.exit(0);

                } catch(MqttException me) {
                    System.out.println("reason "+me.getReasonCode());
                    System.out.println("msg "+me.getMessage());
                    System.out.println("loc "+me.getLocalizedMessage());
                    System.out.println("cause "+me.getCause());
                    System.out.println("excep "+me);
                    me.printStackTrace();
                }
            }
        });
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

        switch (view.getId()) {
            case R.id.buttonGarages:
                showcabs();
                break;

            case R.id.buttonFuel:
                showBikes();
                break;
        }

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


    private class GetGarages extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


}


