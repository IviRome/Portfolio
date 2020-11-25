package com.gti.grupo3.mislugares.pojo;

import com.gti.grupo3.mislugares.Usuario;

import java.util.List;

public class Casa {

    private String direccion;
    private String provincia;
    private String ciudad;
    private long telefono;
    private String propietario;
    private List<String> residentes;
    private String urlMapa;
    private String telefonoEmergencia;

    public Casa(){};

    public Casa(String direccion, String provincia, String ciudad, long telefono, String propietario, List<String> residentes, String telefonoEmergencia) {
        this.direccion = direccion;
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.propietario = propietario;
        this.residentes = residentes;
        this.telefonoEmergencia = telefonoEmergencia;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public void setTelefonoEmergencia(String telefonoEmergencia) {
        this.telefonoEmergencia = telefonoEmergencia;
    }

    public String getUrlMapa() {
        return urlMapa;
    }

    public void setUrlMapa(String urlMapa) {
        this.urlMapa = urlMapa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {

        long tlf = Long.parseLong(telefono);
        this.telefono = tlf;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public List<String> getResidentes() {
        return residentes;
    }

    public void setResidentes(List<String> residentes) {
        this.residentes = residentes;
    }
}
