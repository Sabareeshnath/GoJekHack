package com.go.jek.godrive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoreActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, MqttCallback {


   // ToggleButton toggleButton;
    Switch aSwitch;
    SharedPreferences sharedpreferences;
    List<String> areaList = new ArrayList<>();
    TextView txtExtreme;

    private MQTTReceiver mqttReceiver;

         /*MQTT Block*/

    String topic        = "GOJEKTOPIC";
    //  String content      = "Message from MqttPublishSample";
    int qos             = 2;
    String broker       = "tcp://52.59.15.99:1883";

    String clientId     = "GOJEKRECE";
    MqttClient sampleClient = null;

    String area;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        txtExtreme=(TextView)findViewById(R.id.extremeZoneName);

        mqttReceiver=new MQTTReceiver(new Handler(),this);

        aSwitch= (Switch) findViewById(R.id.switch1);



        aSwitch.setOnCheckedChangeListener(this);


        
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(b){


            editor.putString("inspection", "on");


        }else{
            editor.putString("inspection", "off");
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String restoredText = sharedpreferences.getString("inspection", null);
        if(restoredText!=null){
            if(restoredText.equals("on")){
                aSwitch.setChecked(true);
            }else{
                aSwitch.setChecked(false);
            }
        }


        getMQTTData();

        txtExtreme.setText(area);
        super.onResume();


    }

    private void getMQTTData() {

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        System.out.println("Connecting to broker: "+broker);
        try {
            sampleClient.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        System.out.println("Connected");

        sampleClient.setCallback(this);
        try {
            sampleClient.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("SubScribbedd");

        //Test

      /*  String content      = "Message from MqttPublishSample";

        System.out.println("Publishing message: "+content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        try {
            sampleClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Message published");

        try {
            sampleClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Disconnected");*/


    }

    private void startIntentService() {

        Intent intent = new Intent(this, FetchMQTTIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Config.RECEIVER,mqttReceiver );

        // Pass the location data as an extra to the service.
       // intent.putExtra(Config.LOCATION_DATA_EXTRA, mCurrentLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    public class MQTTReceiver extends ResultReceiver {

        private Context context;

        public MQTTReceiver(Handler handler, Context ctx) {
            super(handler);
            context=ctx;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

           // extremeAreas=resultData.getString("res");





            if (resultCode == 0) {


            }

        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

        @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("Receiveddd " + message);
        areaList.add(message.toString());
        int count= Collections.frequency(areaList,message.toString());
        if(count>=3){
            area=message.toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //stuff that updates ui

                    txtExtreme.append("\n"+area);

                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("area",area);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        area=savedInstanceState.getString("area");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }




}
