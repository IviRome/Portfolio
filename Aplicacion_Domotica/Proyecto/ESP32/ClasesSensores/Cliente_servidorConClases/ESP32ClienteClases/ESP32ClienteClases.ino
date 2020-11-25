#include "WiFi.h"
#include "AsyncUDP.h"
#include "TimeLib.h"
#include "time.h"
#include <ArduinoJson.h>
#include "SensorDistancia.h"
#include "SensorPresencia.h"

//mqtt
#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>
//#include <DHT.h>

#define DHTPIN 2
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);
WiFiClient espClient;
PubSubClient client(espClient);

// IP del servidor
mqttClient.setServer(ws://broker.hivemq.com, 8000);
//mqtt

const char * ssid = "Equipo03";
const char * password = "IvRaCarDi03";
int puertoUDP = 4321;
String hora = "09:32:21";
//--------------------------------------------
AsyncUDP udp;
StaticJsonBuffer<200> jsonBuffer; //tamaño maximo de los datos
JsonObject& envio = jsonBuffer.createObject(); //creación del objeto "envio"
JsonArray& sensoresAlarmados = envio.createNestedArray("sensoresAlarmados"); //Un array en el JSON que numera los sensores de presencia
//que están "alarmados"

SensorDistancia sensorDistancia1(23,35,200); //En orden: triggerPin, echoPin, altura.
SensorPresencia sensorPresencia1(2,"El salon"); 
//SensorPresencia sensorPresencia2(15,"La cocina");

const char* ntpServer = "pool.ntp.org"; //Server para obtener la hora
const long gmtOffset_sec = 3600;  //Parámetros de zona horaria
const int daylightOffset_sec = 3600;

//Sensor de gas
uint8_t PinAnalogicoGas = 34;
uint8_t indice = 0;
int datos[200];
int limite = 200;


//--------------------------------------------

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(PinAnalogicoGas, INPUT);
  funcionSetup(); // Configuración de la conexion Wifi.
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer); //Configuración de obtención de hora
}
 //--------------------------------------------------
void loop() {
  char datosDeSensores[200];
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

  String hora1 = obtenerHora();

  //Guardado de variables
  int altura = sensorDistancia1.distancia();
  bool mostrarPresencia = sensorPresencia1.presencia();
  bool mostrarPresencia2 = randomBool();
  datos[indice] = analogRead(PinAnalogicoGas);

  //Procesado JSON
  envio["Altura"] = altura;
  int lengthPresencia = 0;


  if(mostrarPresencia){
    //envio["Presencia"] = mostrarPresencia;
     sensoresAlarmados.add(sensorPresencia1.getEstancia());
     lengthPresencia++;
    }

    if(mostrarPresencia2){
      sensoresAlarmados.add("La cocina");
     lengthPresencia++;
     }

     

  envio["Hora"] = hora1;
  envio["lengthPresencia"] = lengthPresencia;
  envio.printTo(datosDeSensores);

  //-------------------------------------

  if(datos[indice] > limite){
    Serial.print("CUIDAO");
    char buffer[10];
    dtostrf("Alerta GAS/HUMO",0, 0, buffer);
    client.publish("gas", buffer);
  }
  
  //-------------------------------------
  
  //Envio de datos por UDP
  udp.broadcastTo(datosDeSensores,puertoUDP);

  //Delay de programa
  indice++;

  if(indice == 200) {
    indice = 0; 
  }
  delay(1000);
}
//-------------------------------------------------
void funcionSetup(){
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid,password);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) { //Falla el intento de conexión
  Serial.println("WiFi Failed");
  while(1) {
  delay(500);
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

String obtenerHora(){
  struct tm timeinfo;
  if(!getLocalTime(&timeinfo)){
  Serial.println("Failed to obtain time");
  return "Error en obtención de hora";
  }
  return String(timeinfo.tm_hour) + ":" + String(timeinfo.tm_min) + ":" + String(timeinfo.tm_sec);

  }

  bool randomBool(){
    int i = random(0,2);
    if(i == 0) {
      return false;
      }
      return true;
    }
