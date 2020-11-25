package com.gti.grupo3.mislugares;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class GraficaTemperaturaActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private int TAM = 10;
    private LineChart chart;
    private ArrayList<String> labels;
    private ArrayList<Entry> entries;
    public static FirebaseFirestore db;
    private TextView mensajeCarga;
    private ProgressBar carga;
    private ImageView cerrar;
    private String colorGraficas;
    private TextView textMin;
    private TextView texMax;
    private TextView textMedia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.grafica_temperatura);
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppThemeNope);
        } else {
            setTheme(R.style.AppTheme);
        }
        setTitle("Automotic");
        try {
            TAM = Integer.parseInt(sharedPreferences.getString("numeroMedidasAMostrar", "10"));
            colorGraficas = sharedPreferences.getString("coloresParaGraficas", "#f50d00");
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            TAM = 5;
            colorGraficas = "#f50d00";
        }

        chart = findViewById(R.id.grafica2);
        mensajeCarga = findViewById(R.id.mensajeCarga2);
        carga = findViewById(R.id.progressBar2);
        cerrar = findViewById(R.id.cerrar2);
        cerrar.setVisibility(View.VISIBLE);
        textMin = findViewById(R.id.textMinim);
        texMax = findViewById(R.id.textMaxim);
        textMedia =  findViewById(R.id.textMedia);
        textMedia.setVisibility(View.INVISIBLE);
        textMin.setVisibility(View.INVISIBLE);
        texMax.setVisibility(View.INVISIBLE);

        labels = new ArrayList<>();
        entries = new ArrayList<>();

        chart.setVisibility(View.INVISIBLE);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();

        try {
            CollectionReference temperaturas = db.collection("lecturasClimatizacion")
                    .document("TidMWm88xQHPR1SlauU5")
                    .collection("temperatura");

            Query query = temperaturas.orderBy("fecha", Query.Direction.DESCENDING).limit(TAM);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    float average= 0;
                    if (e != null) {
                        mensajeCarga.setText("Error cargando datos");
                        carga.setVisibility(View.INVISIBLE);
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
                        int b = 0;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            entries.add(new Entry(b, doc.getLong("valor").floatValue()));
                            labels.add(b, simpleDateFormat.format(new Date(doc.getLong("fecha").longValue())));
                            b++;
                        }
                    }

                    if (entries.size() == 0) {
                        mensajeCarga.setText("Parece que no hay valores en la base de datos");
                        carga.setVisibility(View.INVISIBLE);
                    } else {

                        Entry entriesReverse[] = new Entry[entries.size()];
                        String labelsReverse[] = new String[labels.size()];
                        for (int i = 0; i < entries.size(); i++) {
                            average+=entries.get(i).getY();
                            entriesReverse[entries.size() - i - 1] = entries.get(i);
                            labelsReverse[labels.size() - i - 1] = labels.get(i);
                        }


                        for (int i = 0; i < entriesReverse.length; i++) {
                            entriesReverse[i].setX((float) i);
                        }

                        LineDataSet lineDataSet = new LineDataSet(Arrays.asList(entriesReverse), "ºC");
                        lineDataSet.setDrawFilled(true);
                        lineDataSet.setColor(Color.BLACK);
                        lineDataSet.setFillColor(Color.parseColor(colorGraficas));
                        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);


                        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsReverse));

                        chart.getXAxis().setLabelRotationAngle(45);
                        chart.getXAxis().setLabelCount(TAM, true);

                        LineData lineData = new LineData(lineDataSet);

                        texMax.setText("Máximo: " + String.valueOf(lineData.getYMax() + "º"));
                        textMin.setText("Mínimo: " + String.valueOf(lineData.getYMin() + "º"));
                        textMedia.setText("Media: " + String.valueOf(average/entries.size()) + "º");
                        textMin.setVisibility(View.VISIBLE);
                        texMax.setVisibility(View.VISIBLE);
                        textMedia.setVisibility(View.VISIBLE);

                        chart.setData(lineData);
                        chart.getDescription().setEnabled(false);
                        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                        chart.setBackgroundColor(Color.WHITE);
                        chart.invalidate();
                        mensajeCarga.setVisibility(View.INVISIBLE);
                        carga.setVisibility(View.INVISIBLE);
                        chart.setVisibility(View.VISIBLE);
                        cerrar.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            mensajeCarga.setText("Error obteniendo datos");
            carga.setVisibility(View.INVISIBLE);
        }
        super.onCreate(savedInstanceState);
    }
}
