package com.gti.grupo3.mislugares;

import java.util.Calendar;
import java.util.Date;

public class Tratamiento {

    private int id_tratamiento;
    private String medicamento;
    private int duracion;
    private int tomas_diarias;
    private int num_pastillas_por_caja;  // O sobres, ampollas…
    private int dosis_por_toma;
    private Date fecha_inicio;
    private Date fecha_fin;
    private boolean desayuno;
    private boolean comida;
    private boolean cena;
    private String info;

    public Tratamiento(){};

    public Tratamiento(int id, String nombre, int duracion,
                       int tomas_diarias, int dosis_por_toma,
                       boolean desayuno, boolean comida,
                       boolean cena, String info){
        this.id_tratamiento = id;
        this.medicamento = nombre;
        this.duracion = duracion;
        this.tomas_diarias = tomas_diarias;
        this.dosis_por_toma = dosis_por_toma;
        this.desayuno = desayuno;
        this.comida = comida;
        this.cena = cena;
        this.fecha_inicio = new Date();
        // La duración está en semanas por lo que multiplicamos por 7
        // para tener los días de tratamiento y poder añadirselos a la fecha actual para tener la fecha final del tratamiento de manera automática
        this.fecha_fin = sumaDias(this.fecha_inicio,duracion * 7);
        this.info = info;
    };

    public Tratamiento (String nombre, int duracion, int tomas, int dosis, boolean desayuno, boolean comida, boolean cena){
        this.id_tratamiento = (int) Math.random();
        this.medicamento = nombre;
        this.duracion = duracion;
        this.tomas_diarias = tomas;
        this.dosis_por_toma = dosis;
        this.desayuno = desayuno;
        this.comida = comida;
        this.cena = cena;
        this.fecha_inicio = new Date();
        // La duración está en semanas por lo que multiplicamos por 7
        // para tener los días de tratamiento y poder añadirselos a la fecha actual para tener la fecha final del tratamiento de manera automática
        this.fecha_fin = sumaDias(this.fecha_inicio,duracion * 7);
        this.info = "";
    }

    public int getId_tratamiento() {
        return id_tratamiento;
    }

    public void setId_tratamiento(int id_tratamiento) {
        this.id_tratamiento = id_tratamiento;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getTomas_diarias() {
        return tomas_diarias;
    }

    public void setTomas_diarias(int tomas_diarias) {
        this.tomas_diarias = tomas_diarias;
    }

    public int getNum_pastillas_por_caja() {
        return num_pastillas_por_caja;
    }

    public void setNum_pastillas_por_caja(int num_pastillas_por_caja) {
        this.num_pastillas_por_caja = num_pastillas_por_caja;
    }

    public int getDosis_por_toma() {
        return dosis_por_toma;
    }

    public void setDosis_por_toma(int dosis_por_toma) {
        this.dosis_por_toma = dosis_por_toma;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public boolean isDesayuno() {
        return desayuno;
    }

    public void setDesayuno(boolean desayuno) {
        this.desayuno = desayuno;
    }

    public boolean isComida() {
        return comida;
    }

    public void setComida(boolean comida) {
        this.comida = comida;
    }

    public boolean isCena() {
        return cena;
    }

    public void setCena(boolean cena) {
        this.cena = cena;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    //Método auxiliar para sumar dias aun objeto Date
    public static Date sumaDias(Date fecha, int dias){
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DAY_OF_YEAR, dias);
        return cal.getTime();
    }
}
