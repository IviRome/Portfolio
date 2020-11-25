package com.gti.grupo3.mislugares;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gti.grupo3.mislugares.pojo.Sensor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static com.gti.grupo3.mislugares.MainActivity.db;

public class GraficaLocalizaciones extends AppCompatActivity {

    private TextView fecha;
    private ImageView btnFueraCasa;
    private ImageView localizacion_grafica;
    private PieChartView pieChartView;
    private TextView lbl_localizaciones;
    private final String idCasa = "TidMWm88xQHPR1SlauU5";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grafica_localizacion);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNope);
        }
        //graficaLocalizaciones();
        fecha = findViewById(R.id.lbl_fecha_grafica);

        //Colocamos la fecha en el textView correspondiente
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(" dd/MM/yy");
        String fechaString = df.format(date);
        fecha.setText(fechaString);
        lbl_localizaciones = findViewById(R.id.lbl_localizaciones);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();




        // Gráfica del tiempo fuera de casa
        btnFueraCasa = findViewById(R.id.fuera_de_casa_grafica);
        btnFueraCasa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                graficaCadaHabitacion();
                lbl_localizaciones.setText("TIEMPO EN CADA HABITACIÓN");
               Toast.makeText(getApplicationContext(), "Grafica tiempo en cada habitacion", Toast.LENGTH_SHORT).show();
            }
        });

        // Gráfica del tiempo fuera de casa
        localizacion_grafica = findViewById(R.id.localizacion_grafica);
        localizacion_grafica.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calcularTiempofueraDeCasa();
                lbl_localizaciones.setText("TIEMPO FUERA DE CASA");
                Toast.makeText(getApplicationContext(), "Grafica fuera de casa", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public long calcularPorcentaje(long tiempoHabitacion,long tiempoTotal){

        return (tiempoHabitacion * 100)/ tiempoTotal;

    }


    public void graficaCadaHabitacion(){

        db.collection("ultimasMedidas").orderBy("hora").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<UltimaMedida> lista = new ArrayList<>(); // En esta List irá toda la consulta a Firestore
                Set<String> habitaciones = new HashSet<>();

                if (task.isSuccessful()) { //Comprobamos si la consulta se ha realizado correctamente

                    for (QueryDocumentSnapshot document : task.getResult()) { // Iteramos por el resultado obtenido

                        UltimaMedida ultima = document.toObject(UltimaMedida.class);
                        lista.add(ultima); //Creamos un objeto UltimaMedida por cada iteración y lo guardamos en un List "lista"

                        //Creamos una lista con las habitaciones disponibles en la BBDD
                        habitaciones.add(ultima.getLugar());
                    }

                    Map<String, Long> valores = calcularTiempoVersion3(lista, habitaciones);
                    pintarGrafica(valores);


                }
            }
        });

    }

    public Map<String,Long> calcularTiempoVersion3(List<UltimaMedida> lista, Set<String>habitaciones) {
        Map<String, Long> tiempoEn = new HashMap<>();

        for (String habitación : habitaciones) {
            tiempoEn.put(habitación, (long) 0);
        }

        long tiempoAnt = lista.get(0).getHora();
        String habitacionAnt = "";

        for (UltimaMedida medida: lista) {

            long resta = (medida.getHora() - tiempoAnt);

            if (!habitacionAnt.equals(""))

            tiempoEn.put(habitacionAnt, tiempoEn.get(habitacionAnt) + resta);

            tiempoAnt = medida.getHora();

            habitacionAnt = medida.getLugar();

        }

        return tiempoEn;

    }

    void calcularTiempofueraDeCasa(){

        db.collection("casas").document("TidMWm88xQHPR1SlauU5").collection("sensores").orderBy("hora").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Sensor> lista = new ArrayList<>(); // En esta List irá toda la consulta a Firestore

                long tiempoTotal = 0;
                long tiempoFueraCasa = 0;
                long tiempoEnCasa = 0;
                long tiempoInicio = 0;
                long tiempofinal = 0;

                if (task.isSuccessful()) { //Comprobamos si la consulta se ha realizado correctamente

                    for (QueryDocumentSnapshot document : task.getResult()) { // Iteramos por el resultado obtenido

                        //Creamos un objeto UltimaMedida por cada iteración y lo guardamos en un List "lista"
                        Sensor ultima = document.toObject(Sensor.class);
                        lista.add(ultima);


                    }

                    tiempofinal = lista.get(lista.size() - 1).getHora().getTime();
                    tiempoInicio = lista.get(0).getHora().getTime();
                    tiempoTotal = tiempofinal - tiempoInicio;

                   boolean casaVacia = true;

                    for (int i = 0; i < lista.size(); i++){

                       if (lista.get(i).getValor() == 0 && lista.get(i) != lista.get(lista.size() - 1) && casaVacia){

                               long tiempo = lista.get(i + 1).getHora().getTime() - lista.get(i).getHora().getTime();
                               tiempoFueraCasa += tiempo;


                       }

                    }

                    tiempoEnCasa = tiempoTotal - tiempoFueraCasa;
                    Map<String, Long> porcentajes = new HashMap<>();
                    porcentajes.put("FUERA DE CASA", calcularPorcentaje(tiempoFueraCasa, tiempoTotal));
                    porcentajes.put("EN CASA", calcularPorcentaje(tiempoEnCasa, tiempoTotal));
                    pintarGrafica(porcentajes);



                }
            }
        });



    }

    void pintarGrafica(Map<String,Long>portcentajes){

        List<SliceValue> pieData = new ArrayList<>();
        PieChartView pieChartView = findViewById(R.id.piechart);
        PieChartData pieChartData = new PieChartData(pieData);

        for (Map.Entry<String, Long> entry : portcentajes.entrySet()) {
            String color = colorAlatorio();
            Log.d("color", "color: " + color);
            pieData.add(new SliceValue(entry.getValue(), Color.parseColor(color)).setLabel(entry.getKey()));

        };

        pieChartData.setHasLabels(true);
        pieChartView.setPieChartData(pieChartData);

    }

    // Funcion que genera un color aleatrio de una array dado
    String colorAlatorio(){

        //Creamos una array de colores para mostrar cada porcentaje  en un color distinto
        String[] colores = { "#33FFCC", "#66994D", "#B366CC", "#4D8000", "#B33300", "#CC80CC",
                "#66664D", "#991AFF", "#E666FF", "#4DB3FF", "#1AB399",
                "#E666B3", "#33991A", "#CC9999", "#B3B31A", "#00E680",
                "#4D8066", "#809980", "#E6FF80", "#1AFF33", "#999933",
                "#FF3380", "#CCCC00", "#66E64D", "#4D80CC", "#9900B3",
                "#E64D66", "#4DB380", "#FF4D4D", "#99E6E6", "#6666FF"};

        //generar un número aleatorio para elegir los colores de los porcentajes en las gráficas
        String colorAleatorio = colores[ (int)(Math.random()*colores.length)];

        return colorAleatorio;

    }




}
