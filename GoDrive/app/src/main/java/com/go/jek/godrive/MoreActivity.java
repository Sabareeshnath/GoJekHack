package com.go.jek.godrive;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
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
import java.util.List;

public class MoreActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, MqttCallback {


   // ToggleButton toggleButton;
    Switch aSwitch;
    SharedPreferences sharedpreferences;
    List<String> areaList = new ArrayList<>();

      /*MQTT Block*/

    String topic        = "GOJEKTOPIC";
    //  String content      = "Message from MqttPublishSample";
    int qos             = 2;
    String broker       = "tcp://52.59.15.99:1883";

    String clientId     = "GOJEKRECE";
    MqttClient sampleClient = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

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
        super.onResume();
        getMQTTData();
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

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("Receiveddd " + message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
