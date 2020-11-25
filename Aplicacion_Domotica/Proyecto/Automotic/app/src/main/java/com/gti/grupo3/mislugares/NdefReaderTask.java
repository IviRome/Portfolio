package com.gti.grupo3.mislugares;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NdefReaderTask extends AsyncTask<Tag, Void, String> {

    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);


        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e("gti", "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

    }

    protected void onPostExecute(final String result) {


        if (result != null) {

            mostrarTratamiento();
            Tratamiento tr = Tratamientos.crearTratamiento(result);

            NfcActivity.nombre_med.setText(tr.getMedicamento());
            NfcActivity.tratamiento_med.setText("" + tr.getDuracion() + " SEMANAS");
            NfcActivity.tomas_txt.setText("Tomas por día: " + tr.getTomas_diarias());
            NfcActivity.mas_info.setText(tr.getInfo());
            NfcActivity.fecha.setText(TIME_FORMAT.format(tr.getFecha_inicio()));

            NfcActivity.add_tratamiento.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    Tratamientos.guardarTratamiento(result);

                }
            });


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

            NfcActivity.mTextView.setText("Información de la etiqueta : \n" + result);


        }

    }


    public static void mostrarTratamiento(){

        NfcActivity.card_tomas.setVisibility(View.VISIBLE);
        NfcActivity.info_mas.setVisibility(View.VISIBLE);
        NfcActivity.info_etiqueta.setVisibility(View.VISIBLE);
        NfcActivity.divider.setVisibility(View.VISIBLE);
        NfcActivity.divider3.setVisibility(View.VISIBLE);
        NfcActivity.divider5.setVisibility(View.VISIBLE);
        NfcActivity.add_tratamiento.setVisibility(View.VISIBLE);
        NfcActivity.nombre_med.setVisibility(View.VISIBLE);
        NfcActivity.img_med.setVisibility(View.VISIBLE);

        NfcActivity.gif_info_medicacion.setVisibility(View.GONE);
        NfcActivity.info_text.setVisibility(View.GONE);

    }


}