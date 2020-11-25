//LIBRERIA HUMEDAD SALINIDAD TEMPERATURA LDR



//Creamos el objeto del ADS
Adafruit_ADS1115 ads1115(0x48);

//----------------------------------------------------------------
//                          CLASE HUMEDAD
//----------------------------------------------------------------


class SensorHumedad {
  
  private: 
 
     unsigned int elPin;
     int valorAire;
     int valorAgua;
     Adafruit_ADS1115 * pAds; //Declarar el puntero, la variable es de tipo Adafruit_ADS1115

          //----------------------------------------------------------------
          //----------------------------------------------------------------
         
          int16_t leerValorDeCanal (){
            int16_t lectura = (*pAds).readADC_SingleEnded(elPin);
            return lectura;
          }

  public: 

          //--------------------------------------------------
          //              CONSTRUCTOR
          //--------------------------------------------------
          SensorHumedad (unsigned int pin, int vAire, int vAgua, Adafruit_ADS1115 * p) {

          elPin = pin;

          valorAire = vAire;
          
          valorAgua = vAgua;

          pAds = p;

          }
          //---------------------------------------------------
          //              MEDIR HUMEDAD
          //---------------------------------------------------
          int medirHumedad(){
            
          int lectura = (*this).leerValorDeCanal ();
          
          int humedadPorcentaje = 100*valorAire/(valorAire-valorAgua)-lectura*100/(valorAire-valorAgua);

          if (humedadPorcentaje > 100) {
              humedadPorcentaje = 100;
             } else if (humedadPorcentaje < 0) {
              humedadPorcentaje = 0;
              }

          return humedadPorcentaje;

          }
           
         
}; // CLASS



//----------------------------------------------------------------
//                          CLASE TEMPERATURA
//----------------------------------------------------------------

class SensorTemperatura {
  
  private: 
 
     unsigned int elPin;
     double m = 4.096/32767;
     Adafruit_ADS1115 * pAds; //Declarar el puntero, la variable es de tipo Adafruit_ADS1115

          //----------------------------------------------------------------
          //----------------------------------------------------------------
         
          double leerValorDeCanal (){
            double lectura = (*pAds).readADC_SingleEnded (elPin);
            return lectura;
          }

  public: 

          //--------------------------------------------------
          //              CONSTRUCTOR
          //--------------------------------------------------
          SensorTemperatura (unsigned int pin, Adafruit_ADS1115 * p) {

          elPin = pin;
          pAds = p;

          }
          
          //---------------------------------------------------
          //              MEDIR TEMPERATURA
          //---------------------------------------------------
          double medirTemperatura(){
            
          double lectura = (*this).leerValorDeCanal ();
          double temperatura = (( m * lectura) - 0.79) / 0.034;
          return temperatura;
          
          }
           
         
}; // CLASS TEMP


//----------------------------------------------------------------
//                          CLASE ILUMINACION
//----------------------------------------------------------------


class SensorIluminacion {
  
  private: 
 
     unsigned int elPin;
     int max;
     int min; 
     Adafruit_ADS1115 * pAds; //Declarar el puntero, la variable es de tipo Adafruit_ADS1115

          //----------------------------------------------------------------
          //----------------------------------------------------------------
         
          int16_t leerValorDeCanal (){
            int16_t lectura = (*pAds).readADC_SingleEnded (elPin);
            return lectura;
          }

  public: 

          //--------------------------------------------------
          //              CONSTRUCTOR
          //--------------------------------------------------
          SensorIluminacion (unsigned int pin, int minimo, int maximo, Adafruit_ADS1115 * p) {

          elPin = pin;
          pAds = p;
          max = maximo;
          min = minimo;

          }
          
          //---------------------------------------------------
          //              MEDIR ILUMINACION
          //---------------------------------------------------
          int medirIluminacion(){
            
          int16_t lectura = (*this).leerValorDeCanal ();
          int iluminacionPorcentaje = map(lectura, min, max, 0 , 100);
          
          if (iluminacionPorcentaje > 100) {
            iluminacionPorcentaje = 100;
            } 
          else if (iluminacionPorcentaje < 0) {
            iluminacionPorcentaje = 0;
            }  

          return iluminacionPorcentaje;
          
          }
     
}; // class iluminacion

//----------------------------------------------------------------
//                          CLASE SALINIDAD
//----------------------------------------------------------------


class SensorSalinidad {
  
  private: 
 
     unsigned int elPin;
     unsigned int elPowerPin;
     int aguaSalada;
     int aguaDestilada;
     Adafruit_ADS1115 * pAds; //Declarar el puntero, la variable es de tipo Adafruit_ADS1115

          //----------------------------------------------------------------
          //----------------------------------------------------------------

          int16_t encender (){
            digitalWrite(elPowerPin, HIGH);             // Encendemos el sensor
          }

          int16_t apagar (){
            digitalWrite(elPowerPin, LOW);             // Encendemos el sensor
          }
         
          int16_t leerValorDeCanal (){
            int16_t lectura = (*pAds).readADC_SingleEnded (elPin);
            return lectura;
          }

  public: 

          //--------------------------------------------------
          //              CONSTRUCTOR
          //--------------------------------------------------
          SensorSalinidad (unsigned int pin, unsigned int power, int minimo, int maximo, Adafruit_ADS1115 * p) {
          
          pinMode(power, OUTPUT);
          elPin = pin;
          elPowerPin = power;
          pAds = p;
          aguaSalada = maximo;
          aguaDestilada = minimo;

          }
          
          //---------------------------------------------------
          //              MEDIR SALINIDAD
          //---------------------------------------------------
          int medirSalinidad(){

          (*this).encender();
          int16_t lectura = (*this).leerValorDeCanal (); // Encendemos y apagamos el sensor para medir el voltaje 
          (*this).apagar();
          int salinidadPorcentaje = map(lectura, aguaDestilada, aguaSalada, 0, 100); // Mapeamos para cambiar el voltaje a porcentaje
 
           if (salinidadPorcentaje < 0) {
              salinidadPorcentaje = 0;
               } 
           else if (salinidadPorcentaje > 100) {
              salinidadPorcentaje = 100;
              }

           return salinidadPorcentaje;

          }
     
}; // class salinidad

//----------------------------------------------------------------------------------
//----------------------------------------------------------------------------------

// CLASS      NOMBRE   PIN AIRE   AGUA 
SensorHumedad humedad1 (1, 20310, 12341, &ads1115);

// CLASS          NOMBRE       PIN 
SensorTemperatura temperatura1 (2, &ads1115);

// CLASS          NOMBRE       PIN MIN  MAX 
SensorIluminacion iluminacion1 (3, 200, 30627, &ads1115);

//CLASS         NOMBRE     PIN POWER MIN    MAX 
SensorSalinidad salinidad1 (0,  5,   20114, 22892, &ads1115);

//----------------------------------------------------------------------------------

//SETUP
void configuracion_ads1115(){
  
  ads1115.begin(); //Inicializa ads1115
  Serial.println("Ajustando la ganancia...");
  ads1115.setGain(GAIN_ONE);
  Serial.println("Tomando medidas del canal AIN0");
  Serial.println("Rango del ADC: +/- 4.096V (1 bit=2mV)");

}//configuracion_ads1115

//-------------------------------------------------------------------------------------
 void Retraso(){ //Espera 5 segundos para volver a medir
  delay(5000);
 }//Retraso
 
//-------------------------------------------------------------------------------------

void MostrarResultados(int humedad, int16_t salinity, double temperatura, int16_t iluminacion) {


  Serial.print("Humedad: (%): ");
  Serial.print(humedad);
  Serial.println("%");

  Serial.print("Salinidad: ");
  Serial.print(salinity);
  Serial.println("%");
  
  Serial.print("Temperatura: ");
  Serial.print (temperatura);
  Serial.println (" ºC");

  Serial.print("Iluminación: ");
  Serial.print(iluminacion);
  Serial.println("%");

 }//MostrarResultados


//-------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------
