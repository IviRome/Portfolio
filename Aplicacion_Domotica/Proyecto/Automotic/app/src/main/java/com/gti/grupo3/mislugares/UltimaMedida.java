package com.gti.grupo3.mislugares;

public class UltimaMedida {


    private long hora;
    private String lugar;

    public UltimaMedida (long Hora, String Lugar){
        this.hora=Hora;
        this.lugar=Lugar;
    }

    public UltimaMedida() {
        //this.hora=System.currentTimeMillis();
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

}
