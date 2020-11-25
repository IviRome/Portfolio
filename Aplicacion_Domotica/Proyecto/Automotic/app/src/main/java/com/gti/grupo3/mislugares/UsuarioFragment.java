package com.gti.grupo3.mislugares;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UsuarioFragment extends Fragment {
    String nombreU, correoU, telefonoU, uidU, proveedorU;


    @Override public View onCreateView(LayoutInflater inflador,
                                       ViewGroup contenedor, Bundle savedInstanceState) { View vista = inflador.inflate(R.layout.fragment_usuario,
            contenedor, false);

        Button cerrarSesion =(Button) vista.findViewById(R.id.btn_cerrar_sesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { AuthUI.getInstance().signOut(getActivity())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            Intent i = new Intent(getActivity(),LoginActivity.class); i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK); startActivity(i);
                            getActivity().finish();
                        }
                    });
            }
        });

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        final TextView nombre = (TextView) vista.findViewById(R.id.nombre);
        final TextView correo = (TextView) vista.findViewById(R.id.correo);
        final TextView telefono = (TextView) vista.findViewById(R.id.telefono);
        final  TextView afiliados = (TextView) vista.findViewById(R.id.afiliados);


        DocumentReference docRef = db.collection("users").document(usuario.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usuario u = documentSnapshot.toObject(Usuario.class);

                nombre.setText(u.getNombre());
                correo.setText(u.getCorreo());
                telefono.setText("" + u.getTelefono());
                afiliados.setText(u.getPermisos());
            }
        });


        // Inicialización Volley  (Hacer solo una vez en Singleton o Applicaction)
        RequestQueue colaPeticiones = Volley.newRequestQueue(getActivity() .getApplicationContext());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap); }
            public Bitmap getBitmap(String url) {
                return cache.get(url); }
        });

        // Foto de usuario

        Uri urlImagen = usuario.getPhotoUrl(); if (urlImagen != null) {

            ImageView foto_userpage = (ImageView) vista.findViewById(R.id.imagen);

            Picasso.with(getActivity()).load(urlImagen).into(foto_userpage);
        }





        return vista;
    }
}