package com.gti.grupo3.mislugares;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AdaptadorFirestoreUI extends FirestoreRecyclerAdapter<UltimaMedida, AdaptadorFirestoreUI.ViewHolder> {
    protected View.OnClickListener onClickListener;
    public static String lugarUltimaMedida="";


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView hora, lugar;

        public ViewHolder(View itemView) {
            super(itemView);
            hora = (TextView) itemView.findViewById(R.id.hora);
            lugar = (TextView) itemView.findViewById(R.id.lugar);
        }
    }

    public AdaptadorFirestoreUI(
            @NonNull FirestoreRecyclerOptions<UltimaMedida> options) {
        super(options);
    }

    @Override public AdaptadorFirestoreUI.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elementos, parent, false);
        return new ViewHolder(view);
    }

    @Override protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UltimaMedida ultimaMedida) {
        Log.e("Sea", "que?");
        personalizaVista(holder, ultimaMedida);
        holder.itemView.setOnClickListener(onClickListener);
        if (position == 0) {
            lugarUltimaMedida = ultimaMedida.getLugar();
        }
    }

    public static String getLugarUltimaMedida(){
        return lugarUltimaMedida;
    }


    public static void personalizaVista(ViewHolder holder, UltimaMedida ultimaMedida) {
        long getHora = System.currentTimeMillis();
        String getHoraS = String.valueOf(getHora);
        Log.e("Hora",getHoraS);

        long date = ultimaMedida.getHora();
        String tiempo = tiempoTrasncurrido(date);

        DateFormat df = new SimpleDateFormat("   HH:mm:ss  dd/MM/yy");
        String fecha = df.format(date);
        holder.hora.setText(fecha);//esto era ultimaMedida.getHora()
        Log.e("Lugar",ultimaMedida.getLugar());
        holder.lugar.setText(ultimaMedida.getLugar());
    }


    public static String tiempoTrasncurrido(long fecha){

        Date fechaActual = new Date();

        long l2 = fechaActual.getTime();
        long l1 = fecha;
        long diff = l2 - l1;

        long secondInMillis = 1000;
        long minuteInMillis = secondInMillis * 60;
        long hourInMillis = minuteInMillis * 60;
        long dayInMillis = hourInMillis * 24;

        long elapsedDays = diff / dayInMillis;
        diff = diff % dayInMillis;
        long elapsedHours = diff / hourInMillis;
        diff = diff % hourInMillis;
        long elapsedMinutes = diff / minuteInMillis;
        diff = diff % minuteInMillis;
        long elapsedSeconds = diff / secondInMillis;

        if (elapsedDays == 0 && elapsedHours == 0) {
            return " hace " + elapsedMinutes + " minutos";
        }

        return " hace " + elapsedDays + " dias y " + elapsedHours + " horas";
    }
}