package com.gti.grupo3.mislugares;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class EditUsuario extends AppCompatActivity {

    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_usuario);


        db = FirebaseFirestore.getInstance();

        final TextView nombreE = (TextView) findViewById(R.id.editNombre);
        final TextView correoE = (TextView) findViewById(R.id.editCorreo);
        //final TextView telefonoE = (TextView) findViewById(R.id.editTelefono);
        final TextView afiliadosE = (TextView) findViewById(R.id.editAfiliados);
        Button guardar_usuario = findViewById(R.id.btn_guardar);


        DocumentReference docRef = db.collection("users").document(usuario.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usuario u = documentSnapshot.toObject(Usuario.class);

                nombreE.setText(u.getNombre());
                correoE.setText(u.getCorreo());
                //telefonoE.setText("" + u.getTelefono());
                afiliadosE.setText(u.getPermisos());
            }
        });

        guardar_usuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Usuario u = new Usuario();
                u.setNombre(nombreE.getText().toString());
                u.setCorreo(correoE.getText().toString());
                //u.setTelefono(telefonoE.getText().toString());
                u.setPermisos(afiliadosE.getText().toString());
                db.collection("users").document(usuario.getUid()).set(u);

                finish();
            }
        });

    } //onCreate

    @Override public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.menu_edit_usuario, menu);
        return true; /** true -> el menú ya está visible */ }

    @Override public boolean onOptionsItemSelected(MenuItem item) { int id = item.getItemId();

        if (id == R.id.cerrar_editar) {
            lanzarVolverUsuario(null);
            return true; }

        return super.onOptionsItemSelected(item);
    }
    public void lanzarVolverUsuario(View view) {
        finish();
    }


}
