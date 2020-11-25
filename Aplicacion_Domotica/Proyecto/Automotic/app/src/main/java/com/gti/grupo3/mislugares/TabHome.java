package com.gti.grupo3.mislugares;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TabHome extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    public static UltimasMedidasAsinc ultimasMedidas;
    public static AdaptadorFirestoreUI adaptador;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        Context context = rootView.getContext();
        Query query = FirebaseFirestore.getInstance()
                .collection("ultimasMedidas")
                .orderBy("hora", Query.Direction.DESCENDING)
                .limit(3);
        FirestoreRecyclerOptions<UltimaMedida> opciones = new FirestoreRecyclerOptions.Builder<UltimaMedida>().setQuery(query, UltimaMedida.class).build();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adaptador = new AdaptadorFirestoreUI(opciones);// mirar UltimasMedidasAsinc
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(context);
        //layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adaptador.startListening();


       /* recargar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        */

        return rootView;
    }




}
