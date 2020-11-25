package com.gti.grupo3.mislugares;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gti.grupo3.mislugares.pojo.Casa;
import com.gti.grupo3.mislugares.pojo.Sensor;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static raquel.example.com.comun.Mqtt.broker;
import static raquel.example.com.comun.Mqtt.clientId;
import static raquel.example.com.comun.Mqtt.qos;
import static raquel.example.com.comun.Mqtt.topicRoot;



public class MainActivity extends AppCompatActivity implements MqttCallback {

    private RecyclerView recyclerView;
    public static SharedPreferences sharedPreferences;
    private LinearLayoutManager layoutManager;
    public static UltimasMedidasAsinc ultimasMedidas;
    public static AdaptadorFirestoreUI adaptador;//falta
    public String lugarUltimaMedida;
    public int cambioFoto = 0;
    public static FirebaseFirestore db;
    final static int RESULTADO_NUEVO_MEDICAMENTO = 1;
    static RecyclerView recyclerTratamientos;
    static ImageView recargar;
    static ImageView graficaLocalizacion;
    private static final int SOLICITUD_LLAMADA = 0;


    // Mqtt
    private MqttClient client;
    private String TAG = "mqtt";
    private String gas = "gas";
    private String abierta = "ABIERTA";
    private String cerrada = "CERRADA";
    private String temperatura = "temperatura";
    private String borrar = "borrar";
    private String borrarAlertas = "alertas/BORRAR";
    private String magnetico = "magnetico";
    private String alertagas = "alerta gas";
    private String luz = "RESULT";
    private String off = "{'POWER':'OFF'}";
    private String on = "{'POWER':'ON'}";
    private String emergencia = "emergencia";
    private Context context;


    // notificación
    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;

    // alertas
    private LinearLayout linearGas;
    private LinearLayout linearAbierta;
    private LinearLayout linearTemperatura;
    private LinearLayout linearTemperaturaB;
    private TextView textoOk;
    private ImageView imageOk;

    private int visiAbi;
    private int visiGas;
    private int visiTemp;
    private int visiTempB;

private float avisoTempAlta;
private float avisoTempBaja;

    //raq

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /*
      The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNope);
        }

        avisoTempAlta = Float.parseFloat(sharedPreferences.getString("notiTemperatura","40"));
        avisoTempBaja = Float.parseFloat(sharedPreferences.getString("notiTemperaturaBaja","15"));
        db = FirebaseFirestore.getInstance();
        guardarLocalizaciones(db);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        context = getApplicationContext();


       /*

        try {

            guardarValoresPuerta();

        } catch (ParseException e) {

            e.printStackTrace();
        }

        */

        //setUpTabMedicamentos(); // Botones para la Tab de Medicación
        ultimasMedidas = new UltimasMedidasFirestore();//Firebase
        setContentView(R.layout.tabs);
        recyclerTratamientos = (RecyclerView) findViewById(R.id.lista_tratamientos);

        //raq
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        //raq

        //DB

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // DB ULTIMOS PAI
        //9iaWNTMHIlXqMAVoFe5Ter8r1La2



        CollectionReference datosColeccionPAI = db.collection("users").document(LoginActivity.usuario.getUid()).collection("PAI");

        Query queryPAI = datosColeccionPAI.orderBy("fecha", Query.Direction.DESCENDING).limit(1);

        queryPAI.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@android.support.annotation.Nullable QuerySnapshot value,
                                @android.support.annotation.Nullable FirebaseFirestoreException e) {
                Log.d("PAI", "HOLA");
               /*
                if (e != null) {

                    Log.w("kk", "Listen failed.", e);
                    return;
                }


                */

                TextView textoPeso = findViewById(R.id.textoPeso);
                TextView textoFechaPeso = findViewById(R.id.fechaPeso);
                TextView textoAltura = findViewById(R.id.textoAltura);
                TextView textoFechaAltura = findViewById(R.id.fechaAltura);
                TextView textoImc = findViewById(R.id.textoImc);
                TextView textoFechaImc = findViewById(R.id.fechaImc);

                for (QueryDocumentSnapshot doc : value) {
                    Log.d("PAI", "ALGO?");
                    if (doc.get("peso") != null) {
                        Log.d("PAI", doc.get("peso").toString());
                        textoPeso.setText(doc.get("peso").toString() + " Kg");
                    } else {
                        Log.d("PAI", "Error1");
                        textoPeso.setText("Error");
                    }

                    if (doc.get("altura") != null) {
                        Log.d("PAI", doc.get("altura").toString());
                        textoAltura.setText(doc.get("altura").toString() + " cm");
                    } else {
                        Log.d("PAI", "Error2");
                        textoPeso.setText("Error");
                    }

                    if (doc.get("imc") != null) {
                        Log.d("PAI", doc.get("imc").toString());
                        textoImc.setText(doc.get("imc").toString());
                    } else {
                        Log.d("PAI", "Error3");
                        textoPeso.setText("Error");
                    }
                    if (doc.get("fecha") != null) {
                        String fechaS = doc.get("fecha").toString();
                        Log.d("PAI", fechaS);
                        try{
                            long fechaL = Long.parseLong(fechaS);
                            Date date = new Date(fechaL);
                            DateFormat df = new SimpleDateFormat("dd/MM/yy");
                            String fecha = df.format(date);

                            textoFechaPeso.setText(fecha);
                            textoFechaAltura.setText(fecha);
                            textoFechaImc.setText(fecha);
                        } catch(Exception o) {
                            Log.e("Error", "Error: " + o.getMessage());
                        }

                    } else {
                        Log.d("PAI", "Error4");
                        textoFechaPeso.setText("Error");
                        textoFechaAltura.setText("Error");
                        textoFechaImc.setText("Error");
                    }
                }
            }
        });


        //DB ULTIMOS PAI

        // DB ULTIMO LUGAR

        CollectionReference datosColeccion = db.collection("ultimasMedidas");


        Query query = datosColeccion.orderBy("hora", Query.Direction.DESCENDING).limit(1);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@android.support.annotation.Nullable QuerySnapshot value,
                                @android.support.annotation.Nullable FirebaseFirestoreException e) {
               /* if (e != null) {
                    Log.w("kkkkkkk", "Listen failed.", e);
                    return;
                } */

                ImageView imagen = findViewById(R.id.imageView);

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("lugar") != null) {
                        Log.d("kkkkkkk", doc.get("lugar").toString());
                        if (doc.get("lugar").toString().equals("Comedor")) {
                            Log.d("foto", "1");
                            imagen = findViewById(R.id.imageView);
                            imagen.setImageResource(R.drawable.plano_vivienda);
                        } else {
                            if (doc.get("lugar").toString().equals("Cocina")) {
                                Log.d("foto", "2");
                                imagen.setImageResource(R.drawable.cocina);
                            } else {
                                Log.d("foto", "3");
                                imagen.setImageResource(R.drawable.terraza_hab2);
                            }
                        }
                    }
                }
            }
        });

        // /DB ULTIMO LUGAR


        //Mqtt Conexión

        try {
            Log.i(TAG, "Conectando al broker " + broker);
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            // Mqtt paso 1 conexión
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "WillTopic", "App desconectada".getBytes(),
                    qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar.", e);
        }

        //Paso 2:
        try {
            Log.i(TAG, "Suscrito a " + topicRoot + "#");
            client.subscribe(topicRoot + "#", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(TAG, "Error al suscribir.", e);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.preferencias) {
            lanzarPreferencias(null);
            return true;
        }

        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }


        if (id == R.id.home) {
            lanzarHome(null);
            return true;
        }

        if (id == R.id.usuario) {
            Intent intent = new Intent(this, UsuarioActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }


    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }

    public void lanzarHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void lanzarMedidas(View view) {
        Intent i = new Intent(this, Medidas.class);
        startActivity(i);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(final String topic, MqttMessage message) throws Exception {

        final String payload = new String(message.getPayload());
        Log.d(TAG, "Recibiendo: " + topic + " -> " + payload);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                visibles();
                if (topic.contains(gas) && payload.contains(alertagas)) {
                    Toast.makeText(MainActivity.this, "ALERTA: " + "  " + payload, Toast.LENGTH_LONG).show();
                    notificacionGas();
                    linearGas.setVisibility(View.VISIBLE);
                    textoOk.setVisibility(View.INVISIBLE);
                    imageOk.setVisibility(View.INVISIBLE);
                }
                if (payload.contains(abierta) && topic.contains(magnetico)) {
                    Toast.makeText(MainActivity.this, "ALERTA: " + "  " + payload, Toast.LENGTH_LONG).show();
                    notificacionAbierta();
                    linearAbierta.setVisibility(View.VISIBLE);
                    textoOk.setVisibility(View.INVISIBLE);
                    imageOk.setVisibility(View.INVISIBLE);
                }
                if (payload.contains(cerrada)) {
                    Toast.makeText(MainActivity.this, "ALERTA: " + "  " + payload, Toast.LENGTH_LONG).show();
                    notificacionCerrada();
                    linearAbierta.setVisibility(View.GONE);
                    saberVisi();
                    if (visiAbi == View.GONE && visiGas == View.GONE && visiTemp == View.GONE && visiTempB == View.GONE) {
                        textoOk.setVisibility(View.VISIBLE);
                        imageOk.setVisibility(View.VISIBLE);
                    }
                }

                if (topic.contains(temperatura) && Float.parseFloat(payload)>avisoTempAlta) {
                    Toast.makeText(MainActivity.this, "ALERTA: " + "temperatura alta " + payload + " ºC", Toast.LENGTH_LONG).show();
                    notificacionTemperatura();
                    linearTemperatura.setVisibility(View.VISIBLE);
                    linearTemperaturaB.setVisibility(View.GONE);
                    textoOk.setVisibility(View.INVISIBLE);
                    imageOk.setVisibility(View.INVISIBLE);
                } else if ((topic.contains(temperatura) && Float.parseFloat(payload) < avisoTempBaja)) {
                    Toast.makeText(MainActivity.this, "ALERTA: " + "temperatura baja " + payload + " ºC", Toast.LENGTH_LONG).show();
                    notificacionTemperatura();
                    linearTemperaturaB.setVisibility(View.VISIBLE);
                    linearTemperatura.setVisibility(View.GONE);
                    textoOk.setVisibility(View.INVISIBLE);
                    imageOk.setVisibility(View.INVISIBLE);
                } else if ((topic.contains(temperatura) && Float.parseFloat(payload) < avisoTempAlta && Float.parseFloat(payload) > avisoTempBaja)) {
                    linearTemperaturaB.setVisibility(View.GONE);
                    linearTemperatura.setVisibility(View.GONE);
                }

                if (topic.contains(borrarAlertas)) {

                    Toast.makeText(MainActivity.this, "Aviso " + payload, Toast.LENGTH_LONG).show();
                    visibles();

                    linearGas.setVisibility(View.GONE);
                    linearAbierta.setVisibility(View.GONE);
                    linearTemperatura.setVisibility(View.GONE);
                    linearTemperaturaB.setVisibility(View.GONE);
                    textoOk.setVisibility(View.VISIBLE);
                    imageOk.setVisibility(View.VISIBLE);

                   /*

                    try {
                        client.disconnect();
                        client.connect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }


                */}

                if (topic.contains(luz)) {
                    Log.d("diana", payload);
                    Toast.makeText(MainActivity.this, "Aviso luz en " + payload, Toast.LENGTH_LONG).show();
                    if (CasaActivity.lucesCasa.isChecked()) {
                        CasaActivity.lucesCasa.setChecked(false);
                    }

                    else {
                        CasaActivity.lucesCasa.setChecked(true);
                    }

                }


                if (topic.contains(emergencia)) {
                    Log.d("diana", payload);
                    Toast.makeText(MainActivity.this, "Aviso luz en " + payload, Toast.LENGTH_LONG).show();
                    llamadaEmergencia();
                }
            }

        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

        Log.d(TAG, "Entrega completa");
    }


    public void visibles() {
        linearGas = findViewById(R.id.linearGas);
        linearAbierta = findViewById(R.id.linearAbierta);
        linearTemperatura = findViewById(R.id.linearTemperatura);
        linearTemperaturaB = findViewById(R.id.linearTemperaturaB);
        textoOk = findViewById(R.id.txtOk);
        imageOk = findViewById(R.id.imageOk);
    }

    public void saberVisi() {
        visiAbi = linearAbierta.getVisibility();
        visiGas = linearGas.getVisibility();
        visiTemp = linearTemperatura.getVisibility();
        visiTempB = linearTemperaturaB.getVisibility();
    }

    public void borrarAbierta(View view) {
        visibles();
        saberVisi();
        notificationManager.cancel(NOTIFICACION_ID);
        linearAbierta.setVisibility(View.GONE);
        if (visiGas == View.GONE && visiTemp == View.GONE && visiTempB == View.GONE) {
            textoOk.setVisibility(View.VISIBLE);
            imageOk.setVisibility(View.VISIBLE);
        }

    }

    public void borrarGas(View view) {
        visibles();
        saberVisi();
        notificationManager.cancel(2);
        linearGas.setVisibility(View.GONE);
        if (visiAbi == View.GONE && visiTemp == View.GONE && visiTempB == View.GONE) {
            textoOk.setVisibility(View.VISIBLE);
            imageOk.setVisibility(View.VISIBLE);
        }
    }

    public void borrarTemperatura(View view) {
        visibles();
        saberVisi();
        notificationManager.cancel(3);
        linearTemperatura.setVisibility(View.GONE);
        if (visiAbi == View.GONE && visiGas == View.GONE && visiTempB == View.GONE) {
            textoOk.setVisibility(View.VISIBLE);
            imageOk.setVisibility(View.VISIBLE);
        }
    }

    public void borrarTemperaturaB(View view) {
        visibles();
        saberVisi();
        notificationManager.cancel(3);
        linearTemperaturaB.setVisibility(View.GONE);
        if (visiAbi == View.GONE && visiGas == View.GONE && visiTemp == View.GONE) {
            textoOk.setVisibility(View.VISIBLE);
            imageOk.setVisibility(View.VISIBLE);
        }
    }

    public void borrarTodo(View view) {
        visibles();
        notificationManager.cancel(NOTIFICACION_ID);
        notificationManager.cancel(2);
        notificationManager.cancel(3);

        linearGas.setVisibility(View.GONE);
        linearAbierta.setVisibility(View.GONE);
        linearTemperatura.setVisibility(View.GONE);
        linearTemperaturaB.setVisibility(View.GONE);
        textoOk.setVisibility(View.VISIBLE);
        imageOk.setVisibility(View.VISIBLE);

        if (visiAbi == View.GONE && visiGas == View.GONE && visiTemp == View.GONE && visiTempB == View.GONE) {
            textoOk.setVisibility(View.VISIBLE);
            imageOk.setVisibility(View.VISIBLE);
        }

        try {
            if (!client.isConnected()) {
                client.reconnect();
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

        //raq

    /*
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TabHome tab1 = new TabHome();
                    return tab1;
                case 1:
                    TabMedidas tab2 = new TabMedidas();
                    return tab2;
                case 2:
                    TabMedicacion tab3 = new TabMedicacion();
                    return tab3;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Medidas";
                case 2:
                    return "Medicion";
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULTADO_NUEVO_MEDICAMENTO) {

            new AlertDialog.Builder(this).setTitle("INFORMACION").setIcon(R.drawable.icon_pill)
                    .setMessage("Tratamiento guardado correctamente.")
                    .setPositiveButton("CERRAR", null).show();

        }


    }

    public void lanzarNuevoTratamiento(View view) {
        Intent i = new Intent(this, EditarTratamiento.class);
        startActivityForResult(i, RESULTADO_NUEVO_MEDICAMENTO);
    }


    public static void guardarLocalizaciones(FirebaseFirestore db) {

        CollectionReference ultimas = db.collection("ultimasMedidas");
        Date date = new Date();
        ultimas.add(new UltimaMedida((date.getTime() - (3600000 * 10)), "Comedor"));
        ultimas.add(new UltimaMedida((date.getTime() - (3600000 * 11)), "Habitación Principal"));
        ultimas.add(new UltimaMedida((date.getTime() - (3600000 * 15)), "Comedor"));
        ultimas.add(new UltimaMedida((date.getTime() - (3600000 * 5)), "Baño"));
        ultimas.add(new UltimaMedida((date.getTime() - (3600000 * 13)), "Comedor"));
        ultimas.add(new UltimaMedida((date.getTime() - (3600000 * 23)), "Habitación Principal"));
        ultimas.add(new UltimaMedida((date.getTime() - (3600000 * 25)), "Cocina"));

    }


    public void lanzarGrafica(View view) {
        Intent i = new Intent(this, GraficaLocalizaciones.class);
        startActivity(i);
    }

    public void lanzarCasa(View view) {
        Intent i = new Intent(this, CasaActivity.class);
        startActivity(i);
    }


    public void lanzarNfc(View view) {
        Intent i = new Intent(this, NfcActivity.class);
        startActivity(i);
    }

    public void lanzarTemperaturas(View view){
        Intent i = new Intent(this,GraficaTemperaturaActivity.class);
        startActivity(i);
    }


  /* private void setUpTabMedicamentos(){

       //============== BOTONES DE NUEVO Y ESCANEAR TRATAMIENTO DE LA TAB DE MEDICAMENTOS

       nuevo_tratamiento = (FloatingActionButton) findViewById(R.id.nuevo_tratamiento);
       scan_tratamiento = (FloatingActionButton) findViewById(R.id.escanear_medicamento);



       scan_tratamiento.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view) {
               lanzarNfc(null);
           }
       });

                    }

   }

   */

    public void guardarValoresPuerta() throws ParseException {

        CollectionReference sensores = db.collection("casas").document("TidMWm88xQHPR1SlauU5").collection("sensores");
        sensores.add(crearApertura("2018-11-28 09:04:23", 1));
        sensores.add(crearApertura("2018-11-28 09:04:25", 0));
        sensores.add(crearApertura("2018-11-28 11:04:23", 1));
        sensores.add(crearApertura("2018-11-28 11:04:25", 0));
        sensores.add(crearApertura("2018-11-28 15:04:23", 1));
        sensores.add(crearApertura("2018-11-28 15:04:25", 0));
        sensores.add(crearApertura("2018-11-28 20:04:23", 1));
        sensores.add(crearApertura("2018-11-28 20:04:25", 0));
        sensores.add(crearApertura("2018-11-27 10:04:23", 1));
        sensores.add(crearApertura("2018-11-27 10:04:25", 0));
        sensores.add(crearApertura("2018-11-27 16:04:25", 1));
        sensores.add(crearApertura("2018-11-27 16:04:25", 0));

    }

    public Sensor crearApertura(String hora, int valor) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = sdf.parse(hora);
        Sensor s = new Sensor("Recibidor", "Magnetico", fecha, 0);
        return s;


    }




        public void notificacionGas() {
            Date date = new Date();
            long hora = date.getTime();
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CANAL_ID, "Mis Notificaciones",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Descripcion del canal");
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("ALERTA Gas")
                            .setWhen(hora)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setContentText("Se ha detectado Gas en la cocina");
            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), 0);
            notificacion.setContentIntent(intencionPendiente);
            notificationManager.notify(2, notificacion.build());
        }

        public void notificacionAbierta() {
            Date date = new Date();
            long hora = date.getTime();
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CANAL_ID, "Mis Notificaciones",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Descripcion del canal");
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("ALERTA Puerta abierta")
                            .setWhen(hora)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setContentText("La puerta de entrada se ha abierto. Cierre después de entrar o salir.");
            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), 0);
            notificacion.setContentIntent(intencionPendiente);
            notificationManager.notify(NOTIFICACION_ID, notificacion.build());
        }

        public void notificacionCerrada() {
            Date date = new Date();
            long hora = date.getTime();
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CANAL_ID, "Mis Notificaciones",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Descripcion del canal");
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("AVISO Puerta cerrada")
                            .setWhen(hora)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setContentText("La puerta de entrada se ha cerrado");
            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), 0);
            notificacion.setContentIntent(intencionPendiente);
            notificationManager.notify(NOTIFICACION_ID, notificacion.build());
        }

        public void notificacionTemperatura() {
            Date date = new Date();
            long hora = date.getTime();
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CANAL_ID, "Mis Notificaciones",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Descripcion del canal");
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("AVISO Temperatura anormal")
                            .setWhen(hora)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setContentText("La temperatura supera el rango ideal para vivir");
            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), 0);
            notificacion.setContentIntent(intencionPendiente);
            notificationManager.notify(3, notificacion.build());
        }


        public void llamadaEmergencia() {

            DocumentReference docRef = db.collection("casas").document("TidMWm88xQHPR1SlauU5");

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     String telefono = "+34";
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Casa u = document.toObject(Casa.class);

                            if (u.getTelefonoEmergencia() != ""){
                                telefono += u.getTelefonoEmergencia();


                            }

                            telefono += "112";


                        } else {

                            telefono += "112";

                        }


                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + telefono));
                            //callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            if (callIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(callIntent);

                            } else {
                                Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
                            }

                        }


                        else {
                            solicitarPermiso(Manifest.permission.CALL_PHONE, "Debes dasrme permiso para que pueda llamar", SOLICITUD_LLAMADA, MainActivity.this);
                            llamadaEmergencia();
                                }

                    }
                }

            });

        }



    //METODO PARA SOLICITAR PERMISO

    public static void solicitarPermiso(final String permiso, String justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)){
            new AlertDialog.Builder(actividad)
                    .setIcon(R.drawable.phone)
                    .setTitle("SOLICITUD DE PERMISO")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }}) .show();
        } else { ActivityCompat.requestPermissions(actividad,
                new String[]{permiso}, requestCode);
        } }



}



