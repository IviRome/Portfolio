#include <WiFi.h>
#include <MQTT.h>

const char ssid[] = "Equipo3";
const char pass[] = "IvRaCarDi03";
const char broker[] = "iot.eclipse.org";
const int pinSensor = 26;
int cambiaEstado=0;

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
 //client.unsubscribe("equipo3/practica/#");
}
void messageReceived(String &topic, String &payload) {
 Serial.println("incoming: " + topic + " - " + payload);
}
void setup() {
 Serial.begin(115200);
 pinMode(pinSensor, INPUT_PULLUP);
 WiFi.begin(ssid, pass);
client.begin(broker, net);
 client.onMessage(messageReceived);
 connect();
}
void loop() {
 client.loop();
 delay(10); // <- Esperamos a que WiFi sea estable
 int value = digitalRead(pinSensor);
 
 if (!client.connected()) {
 connect();
 }
 
 // publicamos un mensaje cuando value cambie y cada segundo

    if (millis() - lastMillis > 1000) {
      lastMillis = millis();
if (value == LOW) {   
  if(cambiaEstado==0){
      Serial.println("puerta cerrada");
      client.publish("equipo3/practica/magnetico", "CERRADA");
      cambiaEstado=1;
  } 
} else {
  if(cambiaEstado==1){
    Serial.println("puerta abierta");
    client.publish("equipo3/practica/magnetico", "ABIERTA");
    cambiaEstado=0;
  }
}
    }
}
