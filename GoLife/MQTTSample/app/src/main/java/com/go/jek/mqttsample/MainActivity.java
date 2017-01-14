package com.go.jek.mqttsample;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements MqttCallback{


   // MqttClient client;

    String topic        = "MQTT Examples";
    String content      = "Message from MqttPublishSample";
    int qos             = 2;
    String broker       = "tcp://52.59.15.99:1883";
    /*String broker       = "tcp://192.168.0.167:1883";*/
    String clientId     = "JavaSample";
    MqttClient sampleClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }


        doDemo();


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

    public void doDemo() {
        try {


            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);

            System.out.println("Connected");
            //sampleClient = new MqttClient(broker, clientId);
           /* sampleClient.connect();*/
            sampleClient.setCallback(this);
            sampleClient.subscribe(topic);
            System.out.println("SubScribbedd");
           /* MqttMessage message = new MqttMessage();
            message.setPayload("A single message from my computer fff"
                    .getBytes());
            sampleClient.publish(topic, message);*/
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println(s+ " Received !!!!!!!!!");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
