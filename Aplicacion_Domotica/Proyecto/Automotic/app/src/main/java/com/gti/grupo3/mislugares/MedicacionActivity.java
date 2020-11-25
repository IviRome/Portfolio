package com.gti.grupo3.mislugares;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import pl.droidsonroids.gif.GifTextView;


public class MedicacionActivity extends AppCompatActivity {

    private LinearLayout info_med;
    private TextView info_text;
    private GifTextView gif_info_medicacion;
    private TextView nombre_med;
    private TextView tratamiento_med;
    private ImageView img_med;
    private TextView dia_num;
    private TextView mediodia_num;
    private TextView noche_num;
    private TextView tomas_txt;
    private TextView mas_info;
    private FloatingActionButton edit_tratamiento;
    private FloatingActionButton delete_tratamiento;
    private Tratamiento tr;
    private long id; // Posición en el RecycledView
    private String _id; //Clave del lugar
    final static int RESULTADO_EDITAR_MEDICAMENTO = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicacion);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppThemeNope);
        } else {
            setTheme(R.style.AppTheme);
        }

        //================ RECOGEMOS EL TRATAMIENTO A MOSTRAR
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        tr = TabMedicacion.adaptador.getItem((int) id);
        _id = TabMedicacion.adaptador.getKey((int) id);
        //================ RECOGEMOS EL TRATAMIENTO A MOSTRAR

        final AlertDialog.Builder resultadoBorrar = new AlertDialog.Builder(this);
        getReferenciasLayout();
        actualizarVistaTratamiento(tr);

        edit_tratamiento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarEdicionTratamiento(id);
            }
        });

        delete_tratamiento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                resultadoBorrar.setTitle("Eliminar tratamiento")
                        .setIcon(R.drawable.warning)
                        .setMessage("¿Estás seguro que quieres eliminar el tratamiento de " + nombre_med.getText().toString() + "?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                TabMedicacion.tratamientos.borrar(_id);
                                finish();
                            }})
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });


    }

    public void lanzarEdicionTratamiento(long id){
        Intent i = new Intent(this, EditarTratamiento.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    public void actualizarVistaTratamiento(Tratamiento tr){

        nombre_med.setText(tr.getMedicamento());
        if (tr.isDesayuno()){
            dia_num.setText("" + tr.getDosis_por_toma());
        }
        else {
            dia_num.setText(" - ");
        }
        if (tr.isComida()) {
            mediodia_num.setText("" + tr.getDosis_por_toma());
        }

        else {
            mediodia_num.setText(" - ");
        }

        if (tr.isCena()){
            noche_num.setText("" + tr.getDosis_por_toma());
        }

        else {
            noche_num.setText(" - ");
        }
        tomas_txt.setText("Tomas por día: " + tr.getTomas_diarias());
        tratamiento_med.setText(tr.getDuracion() + " semanas");
        mas_info.setText(tr.getInfo());

        //==========================  IMAGEN
        if (tr.getMedicamento().equals("IBUPROFENO (600 mg)") || tr.getId_tratamiento() == 1){
            img_med.setImageResource(R.drawable.ibu2);
        }


        else if (tr.getMedicamento().equals("Espididol") || tr.getMedicamento().equals("espididol") || tr.getId_tratamiento()== 2){
            img_med.setImageResource(R.drawable.espididol);
        }

        else if (tr.getMedicamento().equals("Amoxicilina") || tr.getMedicamento().equals("amoxicilina") || tr.getId_tratamiento()== 3){
            img_med.setImageResource(R.drawable.amox);
        }

        else if (tr.getMedicamento().equals("Cortafriol") ||  tr.getMedicamento().equals("cortafriol") || tr.getId_tratamiento()== 4){
            img_med.setImageResource(R.drawable.cortafriol);
        }

        else {
            img_med.setImageResource(R.drawable.generica);
        }
        //==========================  IMAGEN

    }

    public void getReferenciasLayout(){

        info_med = (LinearLayout) findViewById(R.id.info_med);
        info_text = (TextView) findViewById(R.id.info_text);
        gif_info_medicacion = (GifTextView) findViewById(R.id.gif_info_medicacion);
        nombre_med = (TextView) findViewById(R.id.nombre_med);
        tratamiento_med = (TextView) findViewById(R.id.tratamiento_med);
        img_med = (ImageView) findViewById(R.id.img_med);
        dia_num = (TextView) findViewById(R.id.dia_num);
        mediodia_num = (TextView) findViewById(R.id.mediodia_num);
        noche_num = (TextView) findViewById(R.id.noche_num);
        tomas_txt = (TextView) findViewById(R.id.tomas_txt);
        mas_info = (TextView) findViewById(R.id.mas_info);
        edit_tratamiento = (FloatingActionButton) findViewById(R.id.edit_tratamiento);
        delete_tratamiento = (FloatingActionButton) findViewById(R.id.delete_tratamiento);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULTADO_EDITAR_MEDICAMENTO) {

            new AlertDialog.Builder(this).setTitle("INFORMACION").setIcon(R.drawable.icon_pill)
                    .setMessage("Tratamiento actualizado correctamente correctamente.")
                    .setPositiveButton("CERRAR", null).show();

        }


    }

    public void lanzarEdicionTratamiento(View view){
        Intent i = new Intent(this, EditarTratamiento.class);
        i.putExtra("id", id);
        startActivityForResult(i, RESULTADO_EDITAR_MEDICAMENTO);
    }

}



