#include <WiFi.h>
#include <MQTT.h>

const char ssid[] = "Equipo3";
const char pass[] = "IvRaCarDi03";
const char broker[] = "iot.eclipse.org";
uint8_t PinAnalogico = 35;
int lectura;
int limite = 500;
int enviarAlerta=0;

WiFiClient net;
MQTTClient client;

unsigned long lastMillis = 0;

void connect() {
 Serial.print("checking wifi...");
 while (WiFi.status() != WL_CONNECTED) {
 Serial.print(".");
 delay(1000);
 }
 
 Serial.print("\nconnecting...");
 while (!client.connect("arduino", "try", "try")) {
 Serial.print(".");
 delay(1000);
 }
 
 Serial.println("\nconnected!");
 
 client.subscribe("equipo3/practica/#");
 //client.unsubscribe("<usuario>/practica/#");
}

void messageReceived(String &topic, String &payload) {
 Serial.println("incoming: " + topic + " - " + payload);
}

void setup() {
 Serial.begin(115200);
 WiFi.begin(ssid, pass);
 pinMode(PinAnalogico, INPUT);
 
 client.begin(broker,net);
 client.onMessage(messageReceived);
 
 connect();
}

void loop() {
 client.loop();
 delay(10); // <- Esperamos a que WiFi sea estable

 lectura = analogRead(PinAnalogico);
 
 if (!client.connected()) {
  connect();
 }
 
 // publicamos un mensaje cada vez que se detecten niveles anormales de GAS (500) y cada segundo
 if( lectura >= 500) {
  if(enviarAlerta==0){
  if (millis() - lastMillis > 1000) {
    lastMillis = millis();
    client.publish("equipo3/practica/saludo", "ALERTA GAS");
    enviarAlerta=1;
    }
   }
  }else {
      enviarAlerta=0;
    }
}
