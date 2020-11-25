package com.gti.grupo3.mislugares.pojo;

public class MedidaPai {
    private Long fecha;
    private int peso;
    private int altura;
    private double imc;


    public MedidaPai(Long fecha, int peso, int altura) {
        this.fecha = fecha;
        this.peso = peso;
        this.altura = altura;
        this.imc = peso/Math.pow((float )altura/100,2);

    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public double getImc() {
        return imc;
    }

    public void setImc(double imc) {
        this.imc = imc;
    }
}
