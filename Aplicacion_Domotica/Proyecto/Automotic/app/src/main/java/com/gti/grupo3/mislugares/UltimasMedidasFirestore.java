package com.gti.grupo3.mislugares;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

class UltimasMedidasFirestore implements UltimasMedidasAsinc {
    private CollectionReference ultimasMedidas;
    public UltimasMedidasFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ultimasMedidas = db.collection("ultimasMedidas");
    }
    public void elemento(String id, final UltimasMedidasAsinc.EscuchadorElemento escuchador) {
        ultimasMedidas.document(id).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UltimaMedida ultimaMedida = task.getResult().toObject(UltimaMedida.class);
                            escuchador.onRespuesta(ultimaMedida);
                        } else {
                            Log.e("Firebase", "Error al leer", task.getException());
                            escuchador.onRespuesta(null);
                        }
                    }
                });
    }
    public void anyade(UltimaMedida ultimaMedida) {
        ultimasMedidas.document().set(ultimaMedida); //o ultimasMedidas.add(ultimaMedida);
    }
    public String nuevo() {
        return ultimasMedidas.document().getId();
    }
    public void borrar(String id) {
        ultimasMedidas.document(id).delete();
    }
    public void actualiza(String id, UltimaMedida ultimaMedida) {
        ultimasMedidas.document(id).set(ultimaMedida);
    }
    public void tamanyo(final EscuchadorTamanyo escuchador) {
        ultimasMedidas.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            escuchador.onRespuesta(task.getResult().size());
                        } else {
                            Log.e("Firebase","Error en tamanyo",task.getException());
                            escuchador.onRespuesta(-1);
                        }
                    }
                });
    }
}