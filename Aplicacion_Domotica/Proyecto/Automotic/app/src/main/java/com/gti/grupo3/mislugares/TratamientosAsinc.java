package com.gti.grupo3.mislugares;

public interface TratamientosAsinc { interface EscuchadorElemento{
    void onRespuesta(Tratamiento lugar); }
    interface EscuchadorTamanyo{
        void onRespuesta(long tamanyo);
    }
    void elemento(String id, EscuchadorElemento escuchador);
    void anyade(Tratamiento lugar);
    String nuevo();
    void borrar(String id);
    void actualiza(String id, Tratamiento lugar);
    void tamanyo(EscuchadorTamanyo escuchador); }