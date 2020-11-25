package com.gti.grupo3.mislugares;

import java.util.ArrayList;
import java.util.List;

import com.gti.grupo3.mislugares.UltimaMedida;
import com.gti.grupo3.mislugares.UltimasMedidas;

public class UltimasMedidasVector implements UltimasMedidas {
    protected List<UltimaMedida> vectorUltimasMedidas = ejemploUltimasMedidas();
    public UltimasMedidasVector() {
        vectorUltimasMedidas = ejemploUltimasMedidas();
    }
    public UltimaMedida elemento(int id) {
        return vectorUltimasMedidas.get(id);
    }
    public void anyade(UltimaMedida ultimaMedida) {
        vectorUltimasMedidas.add(ultimaMedida);
    }
    public int nuevo() {
        UltimaMedida ultimaMedida = new UltimaMedida();
        vectorUltimasMedidas.add(ultimaMedida);
        return vectorUltimasMedidas.size()-1;
    }
    public void borrar(int id) {
        vectorUltimasMedidas.remove(id);
    }
    public int tamanyo() {

        return vectorUltimasMedidas.size();
    }
    public void actualiza(int id, UltimaMedida ultimaMedida) {
        vectorUltimasMedidas.set(id, ultimaMedida);
    }
    public static ArrayList<UltimaMedida> ejemploUltimasMedidas() {
        ArrayList<UltimaMedida> ultimasMedidas = new ArrayList<UltimaMedida>();
        ultimasMedidas.add(new UltimaMedida(12112018121212L,
                "Baño"));
        ultimasMedidas.add(new UltimaMedida(12112018111111L,
                "Comedor"));
        return ultimasMedidas;
    }
}
