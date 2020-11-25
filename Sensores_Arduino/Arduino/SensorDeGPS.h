#include <TinyGPS++.h>
#include <SoftwareSerial.h>

class SensorDeGPS
{
  private :
    int elPinRX;
    int elPinTX;
    int elPinIni;
    long laVelocidad;
    TinyGPSPlus miGPS;
    SoftwareSerial miUART;


  public :
    //------------------------------------------------------------------------------
    //                              CONSTRUCTOR
    //------------------------------------------------------------------------------

    SensorDeGPS (int pinRX, int pinTX, int pinIni, long velo):
      elPinRX (pinRX),
      elPinTX (pinTX),
      elPinIni (pinIni),
      laVelocidad (velo),
      miUART (pinRX, pinTX)

    {
      Serial.begin(9600);
      miUART.begin(laVelocidad); // Inicializar la comunicación con el GPS

      pinMode(elPinIni, OUTPUT);
      alternar(); // Pulso para encender el GPS


    }

    //------------------------------------------------------------------------------
    //                               MÉTODOS
    //------------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    void alternar() //Función que ejecuta el apagado y encendido del GPS mediante un pulso.
    {
      digitalWrite(elPinIni, LOW);
      delay(200);
      digitalWrite(elPinIni, HIGH);
      delay(200);
      digitalWrite(elPinIni, LOW);
    }
    //------------------------------------------------------------------------
    void leerDeLaUARTMientrasEsteDisponible (unsigned long milisegundos)
    {
      unsigned long start = millis();
      do
      {
        while (miUART.available())
        {
          miGPS.encode(miUART.read());  // leemos del gps
        }
      } while (millis() - start < milisegundos);

    }
    //-------------------------------------------------------------------------
    double dimeAltitud ()
    {
      double altitud;

      altitud = (miGPS.altitude.meters());

      return altitud;

    }
    //-----------------------------------------------------------------------

    double dimeLatitud ()
    {

      double latitud;

      latitud = (miGPS.location.lat(), 6);

      return latitud;


    }
    //-----------------------------------------------------------------------

    double dimeLongitud ()
    {


      double longitud;

      longitud = (miGPS.location.lng(), 6);

      return longitud;


    }

    //----------------------------------------------------------------------
    double dimeVelocidad ()
    {


      double velocidadDeMovimiento;

      velocidadDeMovimiento = miGPS.speed.kmph();

      return velocidadDeMovimiento;


    }


    //--------------------------------------------------------------------------
    int dimeDia ()
    {
      int dia;

      dia = miGPS.date.day();

      return dia;

    }

    //--------------------------------------------------------------------------
    int dimeMes()
    {
      int mes;

      mes = miGPS.date.month();

      return mes;

    }
    //--------------------------------------------------------------------------
    int16_t dimeAnyo()
    {
      int16_t anyo;

      anyo = miGPS.date.year();

      return anyo;

    }
    //--------------------------------------------------------------------------
    int dimeHoras()
    {
      int horas;

      horas = miGPS.time.hour();

      return horas;
      
      }
     //--------------------------------------------------------------------------
     int dimeMinutos()
     {
      int minutos;

      minutos = miGPS.time.minute();

      return minutos;
      
      }
      //--------------------------------------------------------------------------
      int dimeSegundos()
      {
        int segundos;

        segundos = miGPS.time.second();

        return segundos;
       
        }
       //--------------------------------------------------------------------------
    int satelitesQueMandanSenyal () //Función para mostrar la búsqueda de satélites
    {

      int satelitesEncontrados;

      satelitesEncontrados = miGPS.satellites.value();

    
      return satelitesEncontrados;

    }
    //-------------------------------------------------------------------------
    bool sonValidosLosDatos()
    {
      bool validos = miGPS.location.isValid();
      return validos;
    }
    //-------------------------------------------------------------------------

};//Clase GPS

//CLASE     NOMBRE  pinRX   pinTX   pinIniciación  velocidad
SensorDeGPS GPS1   ( 12,    13,     15,            4800);


void MostrarGPS (double lati, double longi, double alti, int dia, int mes, int16_t anyo, int hora, int minuto) {

  Serial.print("Fecha y hora: ");
  Serial.print(dia); Serial.print("/"); Serial.print(mes); Serial.print("/"); Serial.println(anyo);
  Serial.println(hora); Serial.print(":"); Serial.println(minuto);

  Serial.print("Posición: ");
  Serial.print(lati); Serial.print("º "); Serial.print(longi); Serial.print("º ");
  Serial.print(alti); Serial.println("m"); Serial.println(" ");
 
}//MostrarGPS


