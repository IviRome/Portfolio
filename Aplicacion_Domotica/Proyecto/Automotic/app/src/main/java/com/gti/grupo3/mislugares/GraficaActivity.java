package com.gti.grupo3.mislugares;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.annotation.Nullable;

public class GraficaActivity extends AppCompatActivity {
    private  int TAM = 10;
    private LineChart chart;
    private ArrayList<String> labels;
    private ArrayList<Entry> entries;
    public static FirebaseFirestore db;
    private TextView mensajeCarga;
    private String magnitud;
    private ProgressBar carga;
    private ImageView cerrar;
    private TextView nombre;
    private String nombreMagnitud;
    private String colorGraficas;




    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        setContentView(R.layout.grafica);
        Intent intent = getIntent();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNope);
        }

       try{
           TAM = Integer.parseInt(sharedPreferences.getString("numeroMedidasAMostrar","10"));
           colorGraficas = sharedPreferences.getString("coloresParaGraficas","#f50d00");
       } catch(Exception e) {
           Log.e("Error: ", e.getMessage());
           TAM = 5;
           colorGraficas = "#f50d00";
       }


        chart = (LineChart) findViewById(R.id.grafica);
        mensajeCarga = findViewById(R.id.mensajeCarga);
        carga = findViewById(R.id.carga);
        cerrar = findViewById(R.id.cerrar);

        nombre = findViewById(R.id.nombreMedida);
        magnitud = intent.getStringExtra("Magnitud");
        switch(magnitud){
            case "peso":
            nombre.setText("Peso (kg)");
            break;
            case "imc":
                nombre.setText("Índice de masa");
                break;
            case "altura":
                nombre.setText("Altura (cm)");
        }
        labels = new ArrayList<>();


        chart.setVisibility(View.INVISIBLE);
        nombre.setVisibility(View.INVISIBLE);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //BASE DE DATOS, OBTENCION DE LOS DATOS

        entries = new ArrayList<>();

        db = FirebaseFirestore.getInstance();




        try {
            CollectionReference datosColeccionPAI = db.collection("users").document(LoginActivity.usuario.getUid()).collection("PAI");


            Query queryPAI = datosColeccionPAI.orderBy("fecha", Query.Direction.DESCENDING).limit(TAM);
            queryPAI.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        mensajeCarga.setText("Error: " + e.getMessage());
                    } else {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
                        int b = 0;
                        try {
                            switch (magnitud) {

                                case "peso":
                                    nombreMagnitud = "Peso (Kg)";
                                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                                        entries.add(new Entry(b, doc.getLong(magnitud).intValue()));
                                        labels.add(b, simpleDateFormat.format(new Date(doc.getLong("fecha").longValue())));

                                        b++;

                                    }
                                    break;
                                case "altura":
                                    nombreMagnitud = "Altura (cm)";
                                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                        entries.add(new Entry(b, doc.getLong(magnitud).intValue()));
                                        labels.add(b, simpleDateFormat.format(new Date(doc.getLong("fecha").longValue())));

                                        b++;

                                    }
                                    break;
                                case "imc":
                                    nombreMagnitud = "Índice de masa";
                                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                                        entries.add(new Entry(b, doc.getLong(magnitud).floatValue()));
                                        labels.add(b, simpleDateFormat.format(new Date(doc.getLong("fecha").longValue())));

                                        b++;

                                    }
                                    break;
                            }
                        }catch (Exception peso){
                            mensajeCarga.setText("Error obteniendo valores");
                            carga.setVisibility(View.INVISIBLE);

                        }

                        if (entries.size() == 0) {
                            mensajeCarga.setText("Parece que no hay lecturas");
                            carga.setVisibility(View.INVISIBLE);
                        } else {
                            Entry entriesReverse[] = new Entry[entries.size()];
                            String labelsReverse[] = new String[labels.size()];
                            for (int i= 0; i<entries.size();i++) {
                                entriesReverse[entries.size()-i-1] = entries.get(i);
                                labelsReverse[labels.size()-i-1] = labels.get(i);
                            }

                            for(int i =0;i<entriesReverse.length;i++){
                                entriesReverse[i].setX((float)i);
                            }

                            LineDataSet lineDataSet = new LineDataSet(Arrays.asList(entriesReverse),magnitud);
                            lineDataSet.setDrawFilled(true);
                            lineDataSet.setColor(Color.BLACK);
                            lineDataSet.setFillColor(Color.parseColor(colorGraficas));
                            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);


                            chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsReverse));

                            chart.getXAxis().setLabelRotationAngle(45);
                            chart.getXAxis().setLabelCount(TAM,true);

                            LineData lineData = new LineData(lineDataSet);


                            chart.setData(lineData);
                            chart.getDescription().setEnabled(false);
                            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                            chart.setBackgroundColor(Color.WHITE);
                            chart.invalidate();
                            mensajeCarga.setVisibility(View.INVISIBLE);
                            carga.setVisibility(View.INVISIBLE);
                            chart.setVisibility(View.VISIBLE);
                            cerrar.setVisibility(View.VISIBLE);
                            nombre.setVisibility(View.VISIBLE);


                        }
                    }
                }
            });


        } catch (Exception e) {
            mensajeCarga.setText("Error obteniendo datos de Firestore: " + e.getMessage());
            carga.setVisibility(View.INVISIBLE);
        }







    }




}
