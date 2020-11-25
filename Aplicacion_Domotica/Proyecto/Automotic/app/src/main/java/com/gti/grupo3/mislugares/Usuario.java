package com.gti.grupo3.mislugares;

import android.net.Uri;

import java.util.Vector;

public class Usuario {
    private String nombre;
    private String correo;
    private long inicioSesion;
    private Uri urlFoto;
    private long telefono;
    private String uid;
    private String permisos;

    public Usuario () {}
    public Usuario (String nombre, String correo, long inicioSesion) { this.nombre = nombre;
        this.correo = correo;
        this.inicioSesion = inicioSesion;
    }
    public Usuario (String nombre, String correo) { this(nombre, correo, System.currentTimeMillis());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public Uri getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(Uri urlFoto) {
        this.urlFoto = urlFoto;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public long getInicioSesion() {
        return inicioSesion;
    }

    public void setInicioSesion(long inicioSesion) {
        this.inicioSesion = inicioSesion;
    }
}