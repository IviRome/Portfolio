package com.gti.grupo3.mislugares;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditarTratamiento extends AppCompatActivity {

    private FloatingActionButton add_form_tratamiento;

    private TextView nombre_med;
    private TextView dosis_med;
    private TextView tomas_med;
    private TextView duracion_med;
    private CheckedTextView desayuno_med;
    private CheckedTextView comida_med;
    private CheckedTextView cena_med;
    private Tratamiento tr;
    private long id;
    private String _id;
    private boolean editar = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_tratamiento);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNope);
        }
        final AlertDialog.Builder med_repe = new AlertDialog.Builder(this);
        add_form_tratamiento = findViewById(R.id.add_form_tratamiento);
        desayuno_med = findViewById(R.id.check_desayuno);
        comida_med = findViewById(R.id.check_comida);
        cena_med = findViewById(R.id.check_cena);
        nombre_med = findViewById(R.id.nombre_form_med);
        dosis_med = findViewById(R.id.dosis_form_med);
        duracion_med = findViewById(R.id.duracion_form_med);
        tomas_med = findViewById(R.id.tomas_form_med);

        if (getIntent().getExtras() != null) {

            editar = true;
            Bundle extras = getIntent().getExtras();
            id = extras.getLong("id", -1);
            tr = TabMedicacion.adaptador.getItem((int) id);
            _id = TabMedicacion.adaptador.getKey((int) id);

            nombre_med.setText(tr.getMedicamento());
            dosis_med.setText("" + tr.getDosis_por_toma());
            duracion_med.setText("" + tr.getDuracion());
            tomas_med.setText("" + tr.getTomas_diarias());

            checkTomas(desayuno_med, tr.isDesayuno());
            checkTomas(comida_med, tr.isComida());
            checkTomas(cena_med, tr.isCena());

        }


        checkTomas(desayuno_med, false);
        checkTomas(comida_med, false);
        checkTomas(cena_med, false);

        add_form_tratamiento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                String nombre = nombre_med.getText().toString();
                int dosis = Integer.parseInt(dosis_med.getText().toString());
                int tomas = Integer.parseInt(tomas_med.getText().toString());
                int duracion = Integer.parseInt(duracion_med.getText().toString());
                boolean desayuno = desayuno_med.isChecked();
                boolean comida = comida_med.isChecked();
                boolean cena = cena_med.isChecked();
                Tratamiento tr = new Tratamiento(nombre, duracion, tomas, dosis, desayuno, comida, cena);

                if (!editar){ // Si es un tratamiento nuevo
                    TabMedicacion.tratamientos.anyade(tr);
                    finish();
                }

                else {

                    TabMedicacion.tratamientos.actualiza(_id, tr);
                    finish();

                }

            }

        });

    }


    public void checkTomas(final CheckedTextView check, boolean isCheck){

        if (check != null) {
            check.setChecked(isCheck);
            check.setCheckMarkDrawable(check.isChecked() ? R.drawable.checked : R.drawable.no_checked);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check.setChecked(!check.isChecked());
                    check.setCheckMarkDrawable(check.isChecked() ? R.drawable.checked : R.drawable.no_checked);

                }
            });
        }

    }


}
