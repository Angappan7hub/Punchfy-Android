package com.example.mqtt;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.mqtt.ViewModel.MainViewModel;
import com.example.mqtt.dependency.SessionManager;
import com.example.mqtt.model.AckModel;
import com.example.mqtt.model.SampleData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MqttHandler {
    private static final String TAG="MqttHandler";
    private MqttClient client;
    public MainViewModel mainViewModel;
    public  SessionManager sessionManager;
    public Context context;
public MqttHandler(){

}

    public MqttHandler(ViewModelStoreOwner owner) {
        if (owner instanceof Context) {
            // If owner is an instance of Context, use it as the context
            context = (Context) owner;
        } else {
            // If owner is not an instance of Context, try to get the context using getContext()
            context = getContextFromViewModelStoreOwner(owner);
        }

        // Initialize other variables or perform necessary setup
    }

    private Context getContextFromViewModelStoreOwner(ViewModelStoreOwner owner) {
        try {
            // Try to get context from the owner using reflection
            java.lang.reflect.Method method = owner.getClass().getMethod("getContext");
            return (Context) method.invoke(owner);
        } catch (Exception e) {
            // Handle any exceptions that might occur during reflection
            e.printStackTrace();
            return null;
        }
    }

//    public MqttHandler(ViewModelStoreOwner owner) {
//        // Initialize ViewModel using ViewModelProvider
//        context = owner instanceof Context ? (Context) owner : null;
//        mainViewModel = new ViewModelProvider(owner).get(MainViewModel.class);
//
//        // Now you can use myViewModel to access your data or operations
//    }

    public void connect(String brokerUrl, String clientId) {
        try {
            // Set up the persistence layer
            MemoryPersistence persistence = new MemoryPersistence();

            // Initialize the MQTT client
            client = new MqttClient(brokerUrl, clientId, persistence);

            // Set up the connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setKeepAliveInterval(30);
            connectOptions.setCleanSession(true);

            // Connect to the broker
            client.connect(connectOptions);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.v(TAG,cause.getLocalizedMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String messagePayload=new String( message.getPayload());
                    Log.v(TAG,"Payload size:"+messagePayload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.v(TAG,token.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(topic, mqttMessage);
//            client = mqtt.Client()
//            client.connect("localhost",1883,60)
//            client.publish("topic/test", "Hello world!");
//            client.disconnect();
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.v(TAG,cause.getLocalizedMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String messagePayload=new String( message.getPayload());
                    Log.v(TAG,"Payload size:"+messagePayload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.v(TAG,token.toString());
                    Log.d(TAG,token.toString());

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            //MqttConnectOptions connOpts = new MqttConnectOptions();
           // connOpts.setCleanSession(true);
           // client.connect();
            client.subscribe(topic,1);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(message);
                   // mainViewModel = new ViewModelProvider().get(MainViewModel.class);
                    Log.d("tag","message>>" + new String(message.getPayload()));
                    Log.d("tag","topic>>" + topic);
                    Log.i("tag","message>>" + new String(message.getPayload()));
                    Log.i("tag","topic>>" + topic);
                    String messagePayload=new String( message.getPayload());
                    if(topic.compareTo("mqtt/ack")==0){
                        if(!messagePayload.isEmpty()) {
                            Gson gson = new Gson();
                            AckModel ack = gson.fromJson(messagePayload, AckModel.class);

                           // mainViewModel.ackModel = ack;
                           // AckModel ackModelLists = mainViewModel.ackModel;
                            sessionManager= new SessionManager(context);
                            List<SampleData> jsonData= sessionManager.getData();
                            Gson gson1=new Gson();
                            String jsonData1=gson1.toJson(jsonData);

                            // Retrieve the stored string data
                            //    String jsonData = preferences.getString("data", "");
                            // SharedPreferences preferences = getContext().getSharedPreferences(context.getPackageName(),
                            //        Context.MODE_PRIVATE);
                            //String jsonData = preferences.getString("data", "");
                            Log.i("json",jsonData1);
                            System.out.print("JsonData "+jsonData1);
                            if(ack!=null){
                                String a=ack.response;
                                if (jsonData != null && jsonData.size()!=0) {
                                    for (int i = 0; i <jsonData.size(); i++) {
                                        if (jsonData.get(i).ack.compareTo(a) == 0) {
                                            sessionManager.clearSingleSampleData(jsonData.get(i));
                                        }
                                    }
                                }
                            }

                        }
                    }
                    Log.v(TAG,"Sub Top Msg:"+messagePayload);
                   // parseMqttMessage(new String(message.getPayload()));

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

    } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}