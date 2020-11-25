package com.gti.grupo3.mislugares;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopServiceReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 333;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context,SincroService.class);
        context.stopService(service);
        Log.i(context.toString(),"Se ha detenido el proceso");
    }
}
