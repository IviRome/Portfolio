// Creamos el objeto de presión
SFE_BMP180 pressure;

//----------------------------------------------------------------
//                          CLASE
//----------------------------------------------------------------
class SensorPresion {

  private: 
  
      int elPin;
      double altitud;
      double presion;
      char status;

          //----------------------------------------------------------------
          //----------------------------------------------------------------
          

          double medirTemperatura () {
             status = pressure.startTemperature();
             double temperatura;
             if (status != 0) {                                        // Coge el valor de la temperatura.
                delay(status);                                         // Se guarda la temperatura en la variable temperatura.
                status = pressure.getTemperature(temperatura);
                return temperatura;
             }//if
    
          }// medirTemperatura
          

          double obtenerPresion (double Temp) {
              double P;
              char status = pressure.getPressure(P,Temp);              // Se guarda la presión en la variable P.
              presion = P;
          }// obtenerPresion
          

  public: 

          //--------------------------------------------------
          //              CONSTRUCTOR
          //--------------------------------------------------

          SensorPresion (double altura, int pin) {
            if (pressure.begin()){
            Serial.println("BMP180 init success");
            }//if
            else {
            Serial.println("BMP180 init fail\n\n");
            while(1); // Pausa infinita.
            }//else
    
            altitud = altura;
            pin = elPin;
    
          }//constructor


          //--------------------------------------------------
          //              MEDIR PRESION
          //--------------------------------------------------

          int medirPresion () {
            
            double T = medirTemperatura();                               // Medimos la temperatura para luego obtener la presión
            char status = pressure.startPressure(elPin);
            if (status != 0) {
              delay(status);
              status = obtenerPresion(T);
            } //if
            return presion;
            
          }// medirPresion

          //--------------------------------------------------
          //              MEDIR ALTITUD
          //--------------------------------------------------

          double medirAltitud () {

            double presionMar = pressure.sealevel(presion,altitud);       //Medimos a altura del mar para calcular la altura
            double altura = pressure.altitude(presion,presionMar);        //Calculamos la altura a la que nos encontramos

            return altura;
            
          }// medirAltitud

}; // CLASE


//CLASE       NOMBRE    ALTITUD  PIN
SensorPresion presion1 (5,       3 );

//-------------------------------------------------------------------------------------

void MostrarPresion(double presion, double altura) {

  Serial.print("Presión: ");
  Serial.print(presion);
  Serial.println("mb");

  Serial.print("Altura: ");
  Serial.print(altura);
  Serial.println("m");
  Serial.println(" ");

 }//MostrarPresion
