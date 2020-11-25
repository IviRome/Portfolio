package com.gti.grupo3.mislugares;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class Medidas extends AppCompatActivity {

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medidas);
    



        // initialize our XYPlot reference:
        //plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values to plot:
        //final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
        //Number[] series1Numbers = {62.2, 62.3, 62.0, 61.9, 62.1,63.0};


        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        //XYSeries series1 = new SimpleXYSeries(
                //Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        //LineAndPointFormatter series1Format = new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);


        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        //series1Format.setInterpolationParams(
                //new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // add a new series' to the xyplot:
        /*plot.addSeries(series1, series1Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    */}

    @Override public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */ }

    @Override public boolean onOptionsItemSelected(MenuItem item) { int id = item.getItemId();

        if (id == R.id.preferencias) {
            lanzarPreferencias(null);
            return true; }

        if (id == R.id.acercaDe) { lanzarAcercaDe(null);
            return true; }

        /*if (id == R.id.medidas) { lanzarMedidas(null);
            return true; }*/

        if (id == R.id.home) { lanzarHome(null);
            return true; }

        if (id == R.id.usuario) {
            Intent intent = new Intent(this, UsuarioActivity.class);
            startActivity(intent);
        }

        /*if (id == R.id.medicacion) {
            Intent intent = new Intent(this, MedicacionActivity.class);
            startActivity(intent);
        }*/

        return super.onOptionsItemSelected(item); }

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    public void lanzarPreferencias(View view){
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }

    public void lanzarHome(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void lanzarMedidas(View view){
        Intent i = new Intent(this, Medidas.class);
        startActivity(i);
    }
}