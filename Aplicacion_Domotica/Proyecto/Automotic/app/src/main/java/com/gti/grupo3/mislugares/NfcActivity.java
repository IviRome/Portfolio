package com.gti.grupo3.mislugares;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.droidsonroids.gif.GifTextView;


public class NfcActivity extends AppCompatActivity {
    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "gti";
    static TextView mTextView;
    private NfcAdapter mNfcAdapter;
    static TextView fecha;
    static LinearLayout info_med;
    static TextView info_text;
    static GifTextView gif_info_medicacion;
    static TextView nombre_med;
    static TextView tratamiento_med;
    static ImageView img_med;
    static TextView dia_num;
    static TextView mediodia_num;
    static TextView noche_num;
    static TextView tomas_txt;
    static TextView mas_info;
    static FloatingActionButton add_tratamiento;
    static FloatingActionButton edit_tratamiento;
    static FloatingActionButton delete_tratamiento;
    static CardView card_tomas;
    static CardView info_mas;
    static CardView info_etiqueta;
    static View divider;
    static View divider3;
    static View divider5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("oscuro", false)) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeNope);
        }

        final AlertDialog.Builder resultado =  new AlertDialog.Builder(this);
        getReferencesLayout();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            mTextView.setText("");
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }


    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding requesting to stop the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }

        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.preferencias) {
            lanzarPreferencias(null);
            return true;
        }

        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }


        if (id == R.id.home) {
            lanzarHome(null);
            return true;
        }

        if (id == R.id.usuario) {
            Intent intent = new Intent(this, UsuarioActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }

    public void lanzarHome(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    public void mostrarTr(Tratamiento tr){

        NfcActivity.nombre_med.setText(tr.getMedicamento());
        NfcActivity.tratamiento_med.setText("" + tr.getDuracion() + " SEMANAS");
        NfcActivity.tomas_txt.setText("Tomas por día: " + tr.getTomas_diarias());
        NfcActivity.mas_info.setText(tr.getInfo());
//        NfcActivity.fecha.setText(TIME_FORMAT.format(tr.getFecha_inicio()));

        if (!tr.isDesayuno()) {
            NfcActivity.dia_num.setText("  -");
        } else {
            NfcActivity.dia_num.setText("" + tr.getDosis_por_toma());
        }

        if (!tr.isComida()) {
            NfcActivity.mediodia_num.setText("  -");
        } else {
            NfcActivity.mediodia_num.setText("" + tr.getDosis_por_toma());
        }

        if (!tr.isCena()) {
            NfcActivity.noche_num.setText("  -");
        } else {
            NfcActivity.noche_num.setText(" " + tr.getDosis_por_toma());
        }

        switch (tr.getId_tratamiento()) {
            case 1:
                NfcActivity.img_med.setImageResource(R.drawable.ibu2);
                break;
            case 2:
                NfcActivity.img_med.setImageResource(R.drawable.espididol);
                break;
            case 3:
                NfcActivity.img_med.setImageResource(R.drawable.amox);
                break;
            case 4:
                NfcActivity.img_med.setImageResource(R.drawable.cortafriol);
                break;
            default:
                NfcActivity.img_med.setImageResource(R.drawable.generica);

        }

    }

    private void getReferencesLayout(){

        fecha = (TextView) findViewById(R.id.fecha_lectura);
        info_med = (LinearLayout) findViewById(R.id.info_med);
        info_text = (TextView) findViewById(R.id.info_text);
        gif_info_medicacion = (GifTextView) findViewById(R.id.gif_info_medicacion);
        nombre_med = (TextView) findViewById(R.id.nombre_med);
        tratamiento_med = (TextView) findViewById(R.id.tratamiento_med);
        img_med = (ImageView) findViewById(R.id.img_med);
        dia_num = (TextView) findViewById(R.id.dia_num);
        mediodia_num = (TextView) findViewById(R.id.mediodia_num);
        noche_num = (TextView) findViewById(R.id.noche_num);
        tomas_txt = (TextView) findViewById(R.id.tomas_txt);
        mas_info = (TextView) findViewById(R.id.mas_info);
        add_tratamiento = (FloatingActionButton) findViewById(R.id.add_tratamiento);
        card_tomas = (CardView) findViewById(R.id.card_tomas);
        info_mas = (CardView) findViewById(R.id.info_mas);
        info_etiqueta = (CardView) findViewById(R.id.info_etiqueta);
        divider = (View) findViewById(R.id.divider);
        divider3 = (View) findViewById(R.id.divider3);
        divider5 = (View) findViewById(R.id.divider5);
        mTextView = (TextView) findViewById(R.id.tag_viewer_text);
    }



}





