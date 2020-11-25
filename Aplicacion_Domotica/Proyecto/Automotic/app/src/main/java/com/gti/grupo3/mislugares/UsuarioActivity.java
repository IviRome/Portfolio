package com.gti.grupo3.mislugares;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class UsuarioActivity extends AppCompatActivity {
    final static int RESULTADO_EDITAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNope);
        }
        setContentView(R.layout.activity_usuario);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true; /** true -> el menú ya está visible */}

    @Override public boolean onOptionsItemSelected(MenuItem item) { int id = item.getItemId();

        if (id == R.id.editar) {
            lanzarEditUsuario(null);
            return true; }

        return super.onOptionsItemSelected(item);
    }

    public void lanzarEditUsuario(View view) {
        Intent i = new Intent(this, EditUsuario.class);
        startActivityForResult(i, RESULTADO_EDITAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULTADO_EDITAR) {
            new AlertDialog.Builder(this)
                    .setTitle("Usuario guardado")
                    .setIcon(R.drawable.ic_person_black_24dp)
                    .setMessage("Tus datos se han guardado correctamente")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }})
                    .show();
        }
    }

}