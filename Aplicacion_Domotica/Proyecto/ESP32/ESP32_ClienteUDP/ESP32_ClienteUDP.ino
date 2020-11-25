#include "WiFi.h"
#include "AsyncUDP.h"
#include "TimeLib.h"
#include "time.h"
#include <ArduinoJson.h>
const char * ssid = "Equipo03";
const char * password = "IvRaCarDi03";

AsyncUDP udp;
StaticJsonBuffer<200> jsonBuffer; //tamaño maximo de los datos
JsonObject& envio = jsonBuffer.createObject(); //creación del objeto "envio"
const int EchoPin = 35;
const int TriggerPin = 23;
const int LEDPin= 13;
const int PIRPin= 2;
void setup() {
 Serial.begin(9600);
 pinMode(TriggerPin, OUTPUT);
 pinMode(EchoPin, INPUT);
 pinMode(LEDPin, OUTPUT);
 pinMode(PIRPin, INPUT);
 funcionSetup();
}

void loop() {
  //variables
  int altura = distancia(TriggerPin, EchoPin);
  String mostrarPresencia=detectarPresencia();
  char datosDeSensores[200];
  //Mostrar por el Monitor
 Serial.print("Distancia: ");
 Serial.println(distancia(TriggerPin, EchoPin));
 Serial.println(mostrarPresencia);
 //Procesado variables
 envio["Altura"]=altura;
 envio["Presencia"]=mostrarPresencia;
envio["Hora"] = "09:23:21";
 envio.printTo(datosDeSensores);
 //Enviar los datos por UDP
 udp.broadcastTo(datosDeSensores,4321);

 //Retraso
 delay(1000);
}
int distancia(int TriggerPin, int EchoPin) {
 long duracion, distanciaCm, altura;
 digitalWrite(TriggerPin, LOW); //nos aseguramos señal baja al principio
 delayMicroseconds(4);
 digitalWrite(TriggerPin, HIGH); //generamos pulso de 10us
 delayMicroseconds(10);
 digitalWrite(TriggerPin, LOW);
 duracion = pulseIn(EchoPin, HIGH); //medimos el tiempo del pulso
 distanciaCm = duracion * 10 / 292 / 2; //convertimos a distancia
 altura = calculoAltura(distanciaCm);
 return altura;
}
int calculoAltura(int distanciaTotal){
  //Tendremos como referencia que el sensor está a 2 metros del suelo
  return 200-distanciaTotal;
}

String detectarPresencia(){
  if(digitalRead(PIRPin)==1){
    return "Se ha detectado movimiento";
  }//if
  return "NADA";
 }//detectarPresencia

void funcionSetup(){
  WiFi.mode(WIFI_STA);
  WiFi.begin("Equipo3", "IvRaCarDi03");
  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
  Serial.println("WiFi Failed");
  while(1) {
  delay(1000);
    }
  }
  if(udp.listen(4321)) {
   Serial.print("UDP Listening on IP: ");
    Serial.println(WiFi.localIP());
    udp.onPacket([](AsyncUDPPacket packet) {
    Serial.write(packet.data(), packet.length());
    Serial.println(".");
  });
  }
}
