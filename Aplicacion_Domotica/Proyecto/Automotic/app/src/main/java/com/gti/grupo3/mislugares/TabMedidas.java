package com.gti.grupo3.mislugares;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;


public class TabMedidas extends Fragment {

    private LineChart chart;
    private ArrayList<Integer> pesos;
    private List<Entry> entries;
    private String SERVICE_ID = "com.gti.grupo3.mislugares";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION=1;
    private static final String TAG = "Mobile:";
    //private Sincronizador hilo1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.medidas, container, false);
        LinearLayout layoutPeso = (LinearLayout) rootView.findViewById(R.id.layoutPeso);
        LinearLayout layoutAltura = (LinearLayout) rootView.findViewById(R.id.layoutAltura);
        LinearLayout layoutIMC = (LinearLayout) rootView.findViewById(R.id.layoutIMC);

        CardView sincro =  rootView.findViewById(R.id.card_sincro);
        sincro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSync();
            }
        });


        layoutPeso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(rootView.getContext(),"Toast por defecto", Toast.LENGTH_SHORT).show();
                startGrafica(0);
            }
        });


        layoutAltura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(rootView.getContext(),"Toast por defecto", Toast.LENGTH_SHORT).show();
                startGrafica(1);
            }
        });


        layoutIMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(rootView.getContext(),"Toast por defecto", Toast.LENGTH_SHORT).show();
                startGrafica(2);
            }
        });

        return rootView;
    }

    protected void startGrafica(int cual) {

        /*final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.grafica);

        TextView texto = (TextView) dialog.findViewById(R.id.nombreMedida);
        ImageView image = (ImageView) dialog.findViewById(R.id.cerrar);*/

        //Dependiendo de que card hayamos clicado aparecera un texto u otro
        switch (cual) {
            case 0:
                //texto.setText(R.string.str_peso);
                Intent i = new Intent(getContext(),GraficaActivity.class);
                i.putExtra("Magnitud","peso");
                startActivity(i);
                break;
            case 1:
                //texto.setText(R.string.str_altura);
                Intent f = new Intent(getContext(),GraficaActivity.class);
                f.putExtra("Magnitud","altura");
                startActivity(f);
                break;
            case 2:
                //texto.setText(R.string.str_imc);
                Intent g = new Intent(getContext(),GraficaActivity.class);
                g.putExtra("Magnitud","imc");
                startActivity(g);
                break;
        }

        //dialog.show();

       /* image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/


    }

    private void toast(String mensaje){
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    public void startSync(){
        Log.i(TAG,"Se ejecuta startSync()");
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"No tenfo permisos");
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        } else {
            Log.i(TAG,"Tengo permisos already");
            if(!isMyServiceRunning(SincroService.class)){
                Intent i = new Intent(getActivity(), SincroService.class);
                getActivity().startService(i);
            }
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode,
                                                     String permissions[], int[] grantResults) {
        Log.i(TAG,"Se ejecuta onRequest");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(!isMyServiceRunning(SincroService.class)){
                        Intent i = new Intent(getActivity(), SincroService.class);
                        getActivity().startService(i);
                    }

                Log.i(TAG,"Permiso concedido");
                } else {
                    Log.i(TAG,"Permiso denegado");
                    toast("Necesito permisos para realizar esta accion");
                }
                return;
            }
        }
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }




}