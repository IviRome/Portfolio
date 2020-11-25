package com.gti.grupo3.mislugares;

public interface UltimasMedidasAsinc {
    interface EscuchadorElemento{
        void onRespuesta(UltimaMedida ultimaMedida);
    }
    interface EscuchadorTamanyo{
        void onRespuesta(long tamanyo);
    }
    void elemento(String id, EscuchadorElemento escuchador);
    void anyade(UltimaMedida ultimaMedida);
    String nuevo();
    void borrar(String id);
    void actualiza(String id, UltimaMedida ultimaMedida);
    void tamanyo(EscuchadorTamanyo escuchador);
}
