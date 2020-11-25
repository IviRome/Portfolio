
#include <SSD1306.h>
#include <WiFi.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#define dhtPin 15
#define DHTTYPE DHT11
#include <MQTT.h>

const char * ssid = "Equipo3";
int MIN_ESPERA = 30;
const char * passwordWifi = "IvRaCarDi03";
const char broker[]= "iot.eclipse.org";

volatile bool errFlag = false;
volatile bool initFlag = false;
volatile float temperatura = NULL;

SSD1306 display(0x3c,5,4);
DHT dht(dhtPin, DHTTYPE); // Sensor de temperatura

WiFiClient client;
MQTTClient mqtt;

void setup() {
 Serial.begin(9600);
 
  dht.begin(); //Inicializar sensor
  
                                                        
  display.init();                                       //
  display.setColor(WHITE);                              //
  display.setTextAlignment(TEXT_ALIGN_CENTER);          //  Inicializar pantalla
  display.setFont(ArialMT_Plain_16);                    //
 display.drawString(64,20,String("Conectando..."));     //
 display.display();                                     //
 
 
  WiFi.begin(ssid, passwordWifi);

  while(WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Conectando a red Wifi...");
    }

    mqtt.begin(broker,client);
    
    while(!mqtt.connect("LolinEquipo3","try","try")){
      Serial.println("Conectando a broker...");
      delay(1000);
      }
    display.setFont(ArialMT_Plain_24);
    display.clear();
    
    xTaskCreate(publicarTemp,"Publicacion",2048,NULL,1,NULL);   
    //La tarea que maneja la publicacion de datos pot mqtt. Se ejecuta cada x tiempo, x siendo la variable MIN_ESPERA 
}

void loop() {
  delay(6000);
  mqtt.loop();
  errFlag = false;
  display.clear();
  temperatura = dht.readTemperature();

  if(isnan(temperatura)){
    errFlag = true;
    display.drawString(64,20,String("NaN"));
    display.display();
    return;
    }
    
    initFlag = true;
    display.drawString(65,20,String(temperatura) + " ºC");
    
    display.display();

}

void publicarTemp(void *pvParameters){
  for(;;){
    if(!errFlag && initFlag == true){
      mqtt.publish("equipo3/practica/temperatura",String(temperatura),2,0);
      }
    vTaskDelay(MIN_ESPERA*60000/portTICK_RATE_MS);
    }
  
  }


