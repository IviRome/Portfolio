class SensorPresencia{
private:
  int PirPin; //Pin del M5/ESP32 asociado al sensor
  String laEstancia; //Estancia de la casa donde se encuentra
//-----------------------------------------------------------------
public:
  SensorPresencia(int pin, String estancia){
    laEstancia = estancia;
    PirPin = pin;
    pinMode(PirPin, INPUT); //Inicializa el pin como receptor
  }//constructor
//-----------------------------------------------------------------
  bool presencia(){
    if(digitalRead(PirPin)==1){
      return true;
    }//if
    return false;
  }//detectarPresencia
//-----------------------------------------------------------------
  void setEstancia(String nueva){
    this.laEstancia = nueva;
  }
//-----------------------------------------------------------------
  String getEstancia(){
    return this.laEstancia;
  }
  //-----------------------------------------------------------------
};//Clase
