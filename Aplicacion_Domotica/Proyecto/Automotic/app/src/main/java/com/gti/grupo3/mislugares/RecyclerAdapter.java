package com.gti.grupo3.mislugares;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by csa on 3/7/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHoder>{

    List<Tratamiento> list;
    Context context;

    public RecyclerAdapter(List<Tratamiento> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.tratamiento_item,parent,false);
        MyHoder myHoder = new MyHoder(view);


        return myHoder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {
        Tratamiento tr = list.get(position);
        holder.nombre.setText(tr.getMedicamento());
        holder.duracion.setText(tr.getDosis_por_toma() + " pastillas durante " + tr.getDuracion() + " semanas");

    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



        }

        return arr;

    }

    class MyHoder extends RecyclerView.ViewHolder{
        TextView nombre ,duracion;


        public MyHoder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.item_nombre_med);
            duracion = (TextView) itemView.findViewById(R.id.item_duracion_med);


        }
    }

}