package com.gti.grupo3.myapplication;

public class Medidas {
    private double altura;
    private double peso;

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getImc() {
        return imc;
    }

    public void setImc(double imc) {
        this.imc = imc;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    private double imc;
    private long hora;


    public Medidas(){

    }

    public Medidas (String altura, String peso, long hora){
        this.altura=Double.parseDouble(altura);
        this.peso=Double.parseDouble(peso);
        this.imc=this.caluclarIMC();
        this.hora=hora;
    }

    public double caluclarIMC(){
        double imcD =(this.peso)/(Math.pow(this.altura,2));
        return imcD;
    }

}


