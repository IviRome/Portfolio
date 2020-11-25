package com.gti.grupo3.mislugares;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Tratamientos {
    static String id = "";
    static Tratamiento tr = new Tratamiento();

    static void guardarTratamiento(final String data) {

        Tratamiento tr = crearTratamiento(data);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference tratamientos =   db.collection("users").document(LoginActivity.usuario.getUid()).collection("tratamientos");

        String medicamento = tr.getMedicamento();

        tratamientos.whereEqualTo("medicamento", medicamento).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().size() == 0){


                                Log.d("bbdd", task.getResult().toString());
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Tratamiento tr = crearTratamiento(data);
                                CollectionReference tratamientos = db.collection("users").document(LoginActivity.usuario.getUid()).collection("tratamientos");
                                tratamientos.add(tr);
                                NfcActivity.nombre_med.setText("Tratamiento guardado");

                            }

                            else {
                                String nombre = NfcActivity.nombre_med.getText().toString();
                                NfcActivity.nombre_med.setText("Ya estás tomando " + NfcActivity.nombre_med.getText().toString());
                            }

                        }

                    }
        });

    }


    static Tratamiento crearTratamiento(final String data){

        String[] parts =  data.split(",");
        boolean desayuno = false;
        boolean comida = false;
        boolean cena = false;

        int id = Integer.parseInt(parts[0]);
        String nombre = parts[1];
        int dosis = Integer.parseInt(parts[2].trim());
        int tomas = Integer.parseInt(parts[3].trim());
        if (parts[4].equals("M")) desayuno = true;
        if (parts[5].equals("M")) comida = true;
        if (parts[6].equals("N")) comida = true;
        int duracion = Integer.parseInt(parts[7].trim()); //En semanas
        String mas_info = parts[8];


        return new Tratamiento (id, nombre, duracion, tomas, dosis, desayuno,comida, cena, mas_info);

    }

    }



