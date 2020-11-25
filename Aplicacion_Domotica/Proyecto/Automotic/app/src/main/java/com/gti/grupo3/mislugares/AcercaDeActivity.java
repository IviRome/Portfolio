package com.gti.grupo3.mislugares;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AcercaDeActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {

            setTheme(R.style.AppThemeNope);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.acercade);
    }
}