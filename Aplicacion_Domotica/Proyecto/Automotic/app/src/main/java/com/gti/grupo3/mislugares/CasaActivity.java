package com.gti.grupo3.mislugares;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gti.grupo3.mislugares.pojo.Casa;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.gti.grupo3.mislugares.MainActivity.db;
import static raquel.example.com.comun.Mqtt.broker;
import static raquel.example.com.comun.Mqtt.clientId;
import static raquel.example.com.comun.Mqtt.qos;
import static raquel.example.com.comun.Mqtt.topicRoot;


public class CasaActivity extends AppCompatActivity {
    private static final String TAG = "casaActivity";
    private FirebaseFirestore db;
    private TextView direccionCasa;
    private TextView propieratarioCasa;
    private ImageButton telefonoCasa;
    private ImageButton mapaCasa;
    private TextView listaResidentes;
    private TextView listahabitaciones;
    public static Switch lucesCasa;
    private LinearLayout linearHabitaciones;
    private LinearLayout linearEmergencias;

    // Mqtt
    private MqttClient client;

    public CasaActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNope);
        }
        setContentView(R.layout.activity_casa);
        db = FirebaseFirestore.getInstance();

        direccionCasa = findViewById(R.id.direccion_casa);
        propieratarioCasa = findViewById(R.id.propiertario_casa);
        telefonoCasa = findViewById(R.id.btn_telefono_casa);
        mapaCasa = findViewById(R.id.btn_mapa_casa);
        listaResidentes = findViewById(R.id.lista_residentes);
        listahabitaciones = findViewById(R.id.listahabitaciones);
        linearHabitaciones =  (LinearLayout) findViewById(R.id.linearHabitaciones);
        lucesCasa = findViewById(R.id.luces);
        linearEmergencias = findViewById(R.id.linearEmergencia);




        db.collection("casas").document("TidMWm88xQHPR1SlauU5").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            Casa c;

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                c = documentSnapshot.toObject(Casa.class);
                direccionCasa.setText(c.getDireccion() + ", " + c.getCiudad() + " (" + c.getProvincia() + ")");
                propieratarioCasa.setText("Propietario: " + c.getPropietario());


                for (int i = 0; i < c.getResidentes().size(); i++) {

                    DocumentReference residente = db.collection("users").document(c.getResidentes().get(i));
                    residente.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Usuario u = documentSnapshot.toObject(Usuario.class);


                            if (listaResidentes.getText().equals("RESIDENTES")){
                                listaResidentes.append("\n ");
                            }
                            listaResidentes.append(u.getNombre()+ "\n ");

                        }
                    });

                }

                listaHabitaciones();
                listaEmergencia();

                // BOTÓN PARA LLAMAR =========================================
                telefonoCasa.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_DIAL,
                                Uri.parse("tel:" + c.getTelefono())));

                    }
                });
                // BOTÓN PARA LLAMAR FIN =====================================


                // BOTÓN PARA MAPA ===========================================
                mapaCasa.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(c.getUrlMapa()))
                        );

                    }
                });
                // BOTÓN PARA MAPA FIN ============================================




                // BOTÓN PARA LAS LUCES ===========================================
                lucesCasa.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                      toggleSonoff();



                    }
                });
                // BOTÓN PARA LAS LUCES FIN =======================================
            }

        });

    }


    void toggleSonoff(){

        try {

            client = new MqttClient(broker, clientId, new MemoryPersistence());
            client.connect();

            try {

                MqttMessage message = new MqttMessage("TOGGLE".getBytes());
                message.setQos(qos);
                message.setRetained(false);
                client.publish("equipo3/practica/cmnd/POWER", message);

            } catch (MqttException e) {
                Log.e(TAG, "Error al publicar.", e);


            }

        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar.", e);
        }


    }

    void listaHabitaciones(){

        db.collection("ultimasMedidas").orderBy("hora").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("ResourceType")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<UltimaMedida> lista = new ArrayList<>(); // En esta List irá toda la consulta a Firestore
                final Set<String> habitaciones = new HashSet<>();

                if (task.isSuccessful()) { //Comprobamos si la consulta se ha realizado correctamente

                    for (QueryDocumentSnapshot document : task.getResult()) { // Iteramos por el resultado obtenido

                        UltimaMedida ultima = document.toObject(UltimaMedida.class);
                        habitaciones.add(ultima.getLugar());

                    }




                    for (String habitacion : habitaciones) {

                           LinearLayout layout = new LinearLayout(CasaActivity.this);
                            layout.setOrientation(LinearLayout.HORIZONTAL);

                            //Creamos el textView con el nombre de la habitación
                            TextView text = new TextView(CasaActivity.this);
                            text.setText(habitacion);

                        layout.addView(text);
                            // Añadimos el boton toggle al layout
                        Switch s = new Switch(CasaActivity.this);
                        s.setGravity(1);
                            layout.addView(s);

                            //Añadimos el layout a la card contenedora de las habitaciones
                             linearHabitaciones.addView(layout);
                    }
            }
        }

      });


  }

  void listaEmergencia(){

              LinearLayout layout = new LinearLayout(CasaActivity.this);
              layout.setOrientation(LinearLayout.HORIZONTAL);
              //Creamos el textView con el nombre de la habitación
              TextView text = new TextView(CasaActivity.this);
              text.setText("Manuel Martinez ( hijo )");
              TextView text1 = new TextView(CasaActivity.this);
              text1.setText("     654343232");
              layout.addView(text);
              layout.addView(text1);

              LinearLayout layout1 = new LinearLayout(CasaActivity.this);
              layout.setOrientation(LinearLayout.HORIZONTAL);
              //Creamos el textView con el nombre de la habitación
              TextView text2 = new TextView(CasaActivity.this);
              text2.setText("Adrian Martinez ( hijo )");
              TextView text3 = new TextView(CasaActivity.this);
              text3.setText("     654343898");
              layout1.addView(text2);
              layout1.addView(text3);

              TextView t = new TextView(CasaActivity.this);
              t.setText("CONTACTOS DE EMERGENCIA");
              t.setPadding(5,5,5,5);
              linearEmergencias.addView(t);
              linearEmergencias.addView(layout);
              linearEmergencias.addView(layout1);
  }
}
