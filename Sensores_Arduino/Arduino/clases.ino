#include <Wire.h>
#include <SFE_BMP180.h>
#include <Adafruit_ADS1015.h>
#include "sensores_ads1115.h"
#include "presion.h"
#include "acelerometro.h"
#include "thingspeak.h"
#include "SensorDeGPS.h"


void setup () {
  
  Serial.begin(9600);
  Serial.println("Inicializando...");
  configuracion_Wifi();
  configuracion_ads1115();
  configurar_Acelerometro();

}

void loop() {

// Lectura de los sensores analógicos
 int h = humedad1.medirHumedad();
 double t = temperatura1.medirTemperatura();
 int i = iluminacion1.medirIluminacion();
 int s = salinidad1.medirSalinidad();
 MostrarResultados (h, s, t, i);
 
// Lectura de presión
 double p = presion1.medirPresion();
 double a = presion1.medirAltitud();
 MostrarPresion (p, a);

// Lectura de GPS
 
 if (GPS1.sonValidosLosDatos() == true) {
      double latitud = GPS1.dimeLatitud();
      double longitud = GPS1.dimeLongitud();
      double altitud = GPS1.dimeAltitud();
      GPS1.leerDeLaUARTMientrasEsteDisponible(1000); 
      GPS1.alternar();
      
      int dia = GPS1.dimeDia(); 
      int mes = GPS1.dimeMes(); 
      int16_t anyo = GPS1.dimeAnyo(); 
      int hora = GPS1.dimeHoras(); 
      int minuto = GPS1.dimeMinutos(); 
      MostrarGPS (latitud, longitud, altitud, dia, mes, anyo, hora, minuto);
  }
 else {
      int satelites = GPS1.satelitesQueMandanSenyal();
  }

// Lectura de acelerómetro

   int WAKEON=MOVIMIENTO; //global

   //Lectura 
   int8_t Buf[14];
   float Valores[6];
   a1.I2Cread(MPU9250_ADDRESS, 0x3B, 14, &Buf[0]);// Almacenar valores leidos por el Acelerómetro (ax, ay, az, gx, gy, gz)
  
   //Mostrar valores 
   a1.ConvertirValoresAcelerometro(&Buf[0], &Valores[0]);


// Enviar todos los datos a ThingSpeak

EnviarDatos (h, i, t, s, p, WAKEON);

Retraso();
 
}
