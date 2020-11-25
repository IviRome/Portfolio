package com.gti.grupo3.mislugares;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class SincroService extends IntentService {

    private static final String SERVICE_ID = "Equipo3Service";
    private static final String TAG = "Mobile:";
    private final String channelId = "canal";
    private NotificationCompat.Builder notificacion;
    static  final int notificationId = 1;
   private NotificationManager notificationManager;

    public SincroService() {
        super("SincroService");
    }

    public void pararServicio() {
        this.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent ic, int flags, int idArranque) {
       // notificacionBuilder= new NotificationCompat.Builder(this,channelId);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId, "Servicio de Nearby",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notifica("Iniciada búsqueda de Raspberry");
        //notificationManager.notify(notificationId,notificacion.build());
        startDiscovery();
        Log.i(TAG,"arranca on startCommand");
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }




    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            stopDiscovery();
            Log.i(TAG, "Conectando..");
            notifica("Conectando con " + discoveredEndpointInfo.getEndpointName());
            Nearby.getConnectionsClient(getApplicationContext()).requestConnection("Equipo3Mobile", s, connectionLifecycleCallback)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Peticion de conexion enviada");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error Sincronizar 71", e);
                }
            });

        }


        @Override
        public void onEndpointLost(@NonNull String s) {
            Log.i(TAG, "Perdido el endpoint" + s);
            startDiscovery();
        }
    };

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(s, payloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
            switch (connectionResolution.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    sendUsuario(s, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    notifica("Sincronizando usuario");
                    Log.i(TAG, "Conectado con " + s);
                    disconnect(s);
                    Intent intentBorrar = new Intent(getApplicationContext(),StopServiceReceiver.class);
                    PendingIntent borrar = PendingIntent.getBroadcast(getApplicationContext(),333,intentBorrar,PendingIntent.FLAG_CANCEL_CURRENT);
                    NotificationCompat.Builder notifica = new NotificationCompat.Builder(getApplicationContext(),channelId).setContentText("Usuario de móvil y Things correctamene sincronizados")
                            .setContentTitle("Nearby Connections (Arrastra para eliminar)").setDeleteIntent(borrar).setSmallIcon(R.mipmap.ic_automotic);
                    notificationManager.notify(notificationId,notifica.build());
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    Log.i(TAG, "Conexion rechazada");
                    pararServicio();
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    Log.i(TAG, "Conexion perdida antes de ser aceptada");
                    pararServicio();
                    break;
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            Log.i(TAG, "Desoonexion del endpoint");
        }
    };

    public void startDiscovery() {
        Log.i(TAG, "Se ejecuta startDSicovery()");
        Nearby.getConnectionsClient(this).startDiscovery(SERVICE_ID, endpointDiscoveryCallback
                , new DiscoveryOptions(Strategy.P2P_STAR))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Buscando things");
                        notifica("Activado modo descubrimiento");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                pararServicio();
            }
        });
    }

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };

    //No nos interesa que siga descubriendo dispositivos si ya ha encontrado el nuestro
    private void stopDiscovery() {
        Nearby.getConnectionsClient(this).stopDiscovery();
        Log.i(TAG, "Detenido modo descubrimiento");
    }

    public void onDestroy() {
        stopDiscovery();
        notificationManager.cancel(notificationId);
        super.onDestroy();
    }

    private void disconnect(String s) {
        Nearby.getConnectionsClient(getApplicationContext()).disconnectFromEndpoint(s);
    }

    //Método que extrae el usuario de Firebase, lo codifica y lo envia a Things

    private void sendUsuario(String endpoint, String user) {

        Payload data = null;
        try {
            data = Payload.fromBytes(user.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            pararServicio();
        }
        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endpoint, data);
        notifica("Se ha enviado correctamente");

    }

    //Método auxiliar (el primcipal es notidica())
    private NotificationCompat.Builder actualizaNotificacion(String text){
        Intent intentBorrar = new Intent(this,StopServiceReceiver.class);
        PendingIntent borrar = PendingIntent.getBroadcast(this,333,intentBorrar,PendingIntent.FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(),channelId).setContentText(text)
                .setContentTitle("Nearby Connections (Toca para cancelar el proceso)").setOngoing(true).setSmallIcon(R.mipmap.ic_automotic).setContentIntent(borrar);
    }

    //Una manera rápida de actualizar el texto de la notificacion. Se usa varias veces en el código.
    private void notifica(String text){
        notificationManager.notify(notificationId,actualizaNotificacion(text).build());
    }
}
