package com.gti.grupo3.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SERVICE_ID = "Equipo3Service";
    private final String BALIZA = "Equipo3Things";

    private MqttClient mqttClient;

    public volatile String FirebaseUser = "KSX97Bn03TMLwiONkcPEWWLuZen2";
    public FirebaseFirestore db;
    private ArduinoUart uart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        try{
            mqttClient = new MqttClient(Mqtt.broker,Mqtt.clientId, new MemoryPersistence());
            mqttClient.connect();
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.i(TAG,"Conexion Mqtt perdida");
                    cause.fillInStackTrace();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if(topic == Mqtt.topicRoot + "temperatura") {
                        String temp = new String(message.getPayload());
                        Log.i(TAG,"Un mensaje: " + temp);
                        try{
                            float tmp = Float.parseFloat(temp);
                            long fecha = Calendar.getInstance().getTimeInMillis();
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("valor",tmp);
                            map.put("fecha",fecha);
                            db.collection("lecturasClimatizacion")
                                    .document("TidMWm88xQHPR1SlauU5")
                                    .collection("temperatura")
                                    .add(map);
                        } catch(Exception e) {
                            Log.e(TAG,"Error subiendo MQTT: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            mqttClient.subscribe(Mqtt.topicRoot + "temperatura",Mqtt.qos2);

        }catch(MqttException e){
            Log.e(TAG,e.getMessage());
        }
        lectorUart();

        /*Log.d(TAG, "Mandado a Arduino: A");
        uart.escribir("A");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String s = uart.leer();
        Log.d(TAG, "Recibido de Arduino: " + s);*/

        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Map<String, Object> datos = new HashMap<>(); datos.put("altura", s);
        //db.collection("coleccion").document("documento").set(datos);

        stopAdvertising();
        startAdvertising();

        }


    @Override
    protected void onDestroy() {
        stopAdvertising();
        try{
            mqttClient.disconnect();
            mqttClient.close();

        } catch(MqttException o){
            Log.i(TAG,o.getMessage());
        }
        finally {
            super.onDestroy();
        }

    }

    private void startAdvertising() {

        Nearby.getConnectionsClient(this)
                .startAdvertising(BALIZA, SERVICE_ID, mConnectionLifecycleCallback, new AdvertisingOptions(Strategy.P2P_STAR))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Activado modo anunciante");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error arrancanco el modo anunciante");
                        e.printStackTrace();
                        stopAdvertising();
                        startAdvertising();

                    }
                });
    }

    private void stopAdvertising() {
        Nearby.getConnectionsClient(this).stopAdvertising();
        Log.i(TAG, "Detenido el modo anunciante!");
    }


    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(
                        String endpointId, ConnectionInfo connectionInfo) {
                    // Aceptamos la conexión automáticamente en ambos lados.
                    Nearby.getConnectionsClient(getApplicationContext())
                            .acceptConnection(endpointId, mPayloadCallback);
                    Log.i(TAG, "Aceptando conexión entrante");
                    if (connectionInfo.getEndpointName() == "Equipo3Mobile") {
                        Log.i(TAG, "Es " + connectionInfo.getEndpointName());
                    } else {
                        /// Nearby.getConnectionsClient(getApplicationContext()).rejectConnection(endpointId);
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "Desconexión del endpoint, no se pueden " +
                            "intercambiar más datos.");
                    startAdvertising();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution connectionResolution) {
                    switch (connectionResolution.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            Log.i(TAG, "Conectado exitosamente con " + endpointId);
                            stopAdvertising();
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            Log.i(TAG, "Conexion rechazada");
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            Log.i(TAG, "COnexion perdida");
                            break;
                        case ConnectionsStatusCodes.API_CONNECTION_FAILED_ALREADY_IN_USE:
                            stopAdvertising();
                            startAdvertising();
                    }

                }
            };

    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            String mensaje = new String(payload.asBytes());
            Log.i(TAG, "Transferencia desde " + endpointId + ", con mensaje: " + mensaje);
            //lectorUart.setUserId(mensaje);
        }

        @Override
        public void onPayloadTransferUpdate(String s, PayloadTransferUpdate payloadTransferUpdate) {

        }
    };

    protected void disconnect(String endpointId) {
        Nearby.getConnectionsClient(this).disconnectFromEndpoint(endpointId);
        Log.i(TAG, "Desconectado de " + endpointId);
    }


    public void lectorUart() {
        new Thread((new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"Empieza el hilo secundario");
                ArduinoUart uart = new ArduinoUart("MINIUART", 115200);
                while (true) {

                    uart.escribir("A");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "Error en sleep()", e);
                    }
                    String s = uart.leer();
                    String sAP[] = s.split(",");
                    if (sAP.length > 1) {
                        //Fecha
                        Date date = new Date();
                        Long dateL = date.getTime();
                        Medidas medidas = new Medidas(sAP[0], sAP[1], dateL);

                        Log.d(TAG, "Recibido de Arduino: " + medidas.getAltura() + " y " + medidas.getPeso());
                        db.collection("users").document(FirebaseUser).collection("PAI").add(medidas);
                    }
                }
            }
        })).start();
    }



}
