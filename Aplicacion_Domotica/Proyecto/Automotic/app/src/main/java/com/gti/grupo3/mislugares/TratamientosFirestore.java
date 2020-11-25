package com.gti.grupo3.mislugares;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TratamientosFirestore implements TratamientosAsinc {

    private CollectionReference tratamientos;

    public TratamientosFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tratamientos = db.collection("users").document(LoginActivity.usuario.getUid()).collection("tratamientos");

    }

    public void elemento(String id, final EscuchadorElemento escuchador) {
        tratamientos.document(id).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Tratamiento tr = task.getResult().toObject(Tratamiento.class);
                            escuchador.onRespuesta(tr);
                        } else {
                            Log.e("Firebase", "Error al leer", task.getException());
                            escuchador.onRespuesta(null);
                        }
                    }
                });
    }


    public void anyade(Tratamiento tr) {
        tratamientos.document().set(tr); }

    public String nuevo() {
        return tratamientos.document().getId();
    }


    public void borrar(String id) { tratamientos.document(id).delete();
    }

    public void actualiza(String id, Tratamiento tr) { tratamientos.document(id).set(tr);
    }

    public void tamanyo(final EscuchadorTamanyo escuchador) { tratamientos.get().addOnCompleteListener(
            new OnCompleteListener<QuerySnapshot>() { @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { if (task.isSuccessful()) {
                escuchador.onRespuesta(task.getResult().size()); } else {
                Log.e("Firebase","Error en tamanyo",task.getException());
                escuchador.onRespuesta(-1);
            }
            } });
    } }
