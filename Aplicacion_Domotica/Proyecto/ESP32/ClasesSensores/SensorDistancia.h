class SensorDistancia {
  private:
  int triggerPin;   //Pin del foco emisor
  int laAltura;     //Altura del sensor respecto al suelo
  int echoPin;      //Pin del foco receptor

//-----------------------------------------------------------------
  int calculoAltura(int distanciaTotal){
    return (laAltura - distanciaTotal);   //altura de la persona
    }
//-----------------------------------------------------------------
  public:
  SensorDistancia(int trigger, int echo, int altura) {
    triggerPin = trigger;
    laAltura = altura;
    echoPin = echo;

    pinMode(triggerPin, OUTPUT);//
    pinMode(echoPin, INPUT);    // Inicializa los pins como emisor y receptor

  }//constructor
//-----------------------------------------------------------------
  int distancia(){

    long duracion, distanciaCm, altura;
    digitalWrite(triggerPin, LOW); // Aseguramos señal baja al inicio
    delayMicroseconds(4);
    digitalWrite(triggerPin, HIGH); //Generamos pulso de 10us
    delayMicroseconds(10);
    digitalWrite(triggerPin, LOW);
    duracion = pulseIn(echoPin, HIGH); //Medimos la duracion del pulso
    distanciaCm = duracion * 10 / 292 / 2; //Convertimos a distancia
    return calculoAltura(distanciaCm);
  //  return altura;

    } //distancia
//-----------------------------------------------------------------
  };//Clase
