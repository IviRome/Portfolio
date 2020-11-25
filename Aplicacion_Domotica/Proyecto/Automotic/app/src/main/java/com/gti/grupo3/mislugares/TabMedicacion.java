package com.gti.grupo3.mislugares;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class TabMedicacion extends Fragment {
    private RecyclerView recyclerTratamientos;
    public static AdaptadorTratamientos adaptador;
    private RecyclerView.LayoutManager layoutManager;
    final static int RESULTADO_NUEVO_MEDICAMENTO = 1;
    View view;
    View rootView;
    Context context;
    public static TratamientosAsinc tratamientos;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_medicacion, container, false);
        view = inflater.inflate(R.layout.activity_main, container, false);
        context = view.getContext();
        tratamientos = new TratamientosFirestore();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() .permitAll().build());



        Query query = FirebaseFirestore.getInstance().collection("users").document("9iaWNTMHIlXqMAVoFe5Ter8r1La2").collection("tratamientos");

        FirestoreRecyclerOptions<Tratamiento> opciones = new FirestoreRecyclerOptions.Builder<Tratamiento>().setQuery(query, Tratamiento.class).build();

        recyclerTratamientos = (RecyclerView) rootView.findViewById(R.id.lista_tratamientos);
        adaptador = new AdaptadorTratamientos(opciones);
        recyclerTratamientos.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(context);
        recyclerTratamientos.setLayoutManager(layoutManager);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MedicacionActivity.class);
                i.putExtra("id", (long) recyclerTratamientos.getChildAdapterPosition(v));
                startActivity(i);
            }
        });

        ItemTouchHelper.SimpleCallback deslizarParaEliminar = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(context, R.drawable.delete);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                initiated = true;
            }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //coger posición del elemento deslizado

                if (direction == ItemTouchHelper.LEFT) {    //si deslizas hacia la izquierda


                    AlertDialog.Builder builder = new AlertDialog.Builder(context); //confirmación de eliminación de tratamiento
                    builder.setTitle(" ATENCIÓN");
                    builder.setIcon(R.drawable.warning);
                    builder.setMessage("¿Estás seguro de eliminar este tratamiento?");//set message
                    builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() { // Al pulsar sobre el botón eliminar
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adaptador.notifyItemRemoved(position);    //Quitar elemento del recyclerView

                            Log.d("eliminar", viewHolder.toString());
                            int id = position;
                            String _id = TabMedicacion.adaptador.getKey((int) id);
                            tratamientos.borrar(_id);

                            return;
                        }
                    }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {  //No borramos el item del recycler view si hemos cancelado
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adaptador.notifyItemRemoved(position + 1);    // notifica al adaptador que el elemento ha sido eliminado en esa particular posición
                            adaptador.notifyItemRangeChanged(position, adaptador.getItemCount());   // notifica al adapatdor la posición del elemento que ha cambiado de posición
                            return;
                        }
                    }).show();  //muestra la alerta de confirmación de borrado
                }

            }



            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                // pintar el fondo
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // dibujar las posiciones del fondo
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX - 30, dY, actionState, isCurrentlyActive);
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(deslizarParaEliminar);
        itemTouchHelper.attachToRecyclerView(recyclerTratamientos); //añadir la acción de deslizar al recyclerview
        adaptador.startListening();

        return rootView;

    }


}
