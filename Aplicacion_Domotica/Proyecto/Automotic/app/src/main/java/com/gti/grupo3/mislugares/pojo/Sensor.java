package com.gti.grupo3.mislugares.pojo;

import java.util.Date;

public class Sensor {

    private String habitacion;
    private String tipo;
    private int valor;
    private Date hora;

    public Sensor(){};

    public Sensor(String habitacion, String tipo, Date hora, int valor) {
        this.valor = valor;
        this.habitacion = habitacion;
        this.tipo = tipo;
        this.hora = hora;
    }

    public int getValor() {
        return valor;
    }

    public void setCasa(int valor) {
        this.valor = valor;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(String habitacion) {
        this.habitacion = habitacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }
}
