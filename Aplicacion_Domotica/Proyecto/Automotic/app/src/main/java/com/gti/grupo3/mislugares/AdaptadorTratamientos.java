package com.gti.grupo3.mislugares;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AdaptadorTratamientos extends FirestoreRecyclerAdapter<Tratamiento, AdaptadorTratamientos.ViewHolder> {
    private ItemClickListener onItemClickListener;
    protected View.OnClickListener onClickListener;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private TextView duracion;
        private ProgressBar progreso_tratamiento;

        public ViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.item_nombre_med);
            duracion = (TextView) itemView.findViewById(R.id.item_duracion_med);
            progreso_tratamiento = (ProgressBar) itemView.findViewById(R.id.progressBar2);

        }
    }

    public void setItemClickListener(ItemClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    public AdaptadorTratamientos(
            @NonNull FirestoreRecyclerOptions<Tratamiento> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorTratamientos.ViewHolder holder, final int position, @NonNull Tratamiento tr) {
        personalizaVista(holder, tr);

        holder.itemView.setOnClickListener(onClickListener);

    }

    /*

    public void onClick(View view) {

        context = context = view.getContext();
        Intent detail = new Intent(context, NfcActivity.class);
        detail.putExtra("id", view.getId());
        context.startActivity(detail);

    }

    */


    @Override
    public AdaptadorTratamientos.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tratamiento_item, parent, false);

        return new AdaptadorTratamientos.ViewHolder(view);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick; }

    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }


    public static void personalizaVista(ViewHolder holder, Tratamiento tr) {

        holder.nombre.setText(tr.getMedicamento());
        Date fechaUltimaSincro = tr.getFecha_inicio();
        Date fechaActual = new Date();
        int dias = fechasDiferenciaEnDias(fechaUltimaSincro, fechaActual);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = df.format(tr.getFecha_fin());
        holder.duracion.setText(tr.getDosis_por_toma() + " pastillas al día hasta el " + fecha + "");
        int dur = tr.getDuracion() * 7;
        //int progreso = (100 * dias)/dur;
        int alea = (int) Math.floor(Math.random()*100+0);
        holder.progreso_tratamiento.setProgress(alea);
    }



    public static int fechasDiferenciaEnDias(Date fechaInicial, Date fechaFinal) {

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String fechaInicioString = df.format(fechaInicial);
        try {
            fechaInicial = df.parse(fechaInicioString);
        }
        catch (ParseException ex) {
        }

        String fechaFinalString = df.format(fechaFinal);
        try {
            fechaFinal = df.parse(fechaFinalString);
        }
        catch (ParseException ex) {
        }

        long fechaInicialMs = fechaInicial.getTime();
        long fechaFinalMs = fechaFinal.getTime();
        long diferencia = fechaFinalMs - fechaInicialMs;
        double dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));
        return ( (int) dias);
    }





}