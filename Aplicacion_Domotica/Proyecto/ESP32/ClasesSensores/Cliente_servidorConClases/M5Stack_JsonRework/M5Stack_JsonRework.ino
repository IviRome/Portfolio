#include "WiFi.h"
#include "AsyncUDP.h"
#include "HardwareSerial.h"
#include <ArduinoJson.h>
#define BLANCO 0XFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F
#include <M5Stack.h>
const int puerto = 4321;
const char * ssid = "Equipo3";
const char * password = "IvRaCarDi03";

char texto[200];
int hora;
//HardwareSerial Serial1(1);
boolean rec=0;
AsyncUDP udp;

void setup(){
  M5.begin();
  
  
   
  M5.Lcd.setTextSize(2); //Tamaño del texto
  Serial.begin(115200);
  //Inicializamos la segunda UART
  //Serial1.begin(115200,SERIAL_8N1,-1,-1);
  
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
    if (WiFi.waitForConnectResult() != WL_CONNECTED) {
      Serial.println("WiFi Failed");
      while(1) {
        delay(1000);
      }
  }
  
if(udp.listen(puerto)) {
Serial.print("UDP Listening on IP: ");
Serial.println(WiFi.localIP());
udp.onPacket([](AsyncUDPPacket packet) {
int i=200;
while (i--) {*(texto+i)=*(packet.data()+i);}
rec=1; //indica mensaje recibido
});
}
}
void loop()
{
  
if (rec){
  //Send broadcast
  rec=0; //mensaje procesado
  
  udp.broadcastTo("Recibido",puerto); //envia confirmacion
  udp.broadcastTo(texto,puerto); //y dato recibido
  
  hora=atol(texto); //paso de texto a entero
  StaticJsonBuffer<200> jsonBufferRecv; //definición del buffer para almacenar el objeto JSON, 200 máximo
  JsonObject& recibo = jsonBufferRecv.parseObject(texto); //paso de texto a formato JSON

  int altura = recibo[String ("Altura")]; //extraigo el dato "Segundo" del objeto "recibido" y lo almaceno en la

  //bool presencia =recibo[String ("Presencia")];
  
  int lengthPresencia = recibo["lengthPresencia"]; //Longitud de sensoresAlarmados
  
  const char * p1 = "Sensor1 Sin datos";  //Variables que almacenan el valor de presencia de ambos sensores de presencia
  const char * p2 = "Sensor2 Sin datos";

  
  switch(lengthPresencia) {
    case 0:
    p1="nadie";
    p2 = "nadie";
    break;
    case 1:
    p1 = recibo["sensoresAlarmados"][0];    //
    p2 = "nadie";                           // sensoresAlarmados es un array de strings contenido en el Json 
    break;                                  //  contiene unicamente las salas que dan positivo en presencia
    case 2:                                 //
    p1 = recibo["sensoresAlarmados"][0];
    p2 = recibo["sensoresAlarmados"][1];
    break;
    }



    LCD_Clear();
    
   String Hora = recibo["Hora"];
    M5.Lcd.println(Hora);
    M5.Lcd.println("----------------------");
    M5.Lcd.println(p1);                           //Localizaciones que dan positivo en presencia
    M5.Lcd.println(p2);
    M5.Lcd.println("----------------------");
    M5.Lcd.println("Altura: " + altura);
    
    
   // mandarRaspberry();
  } else {
    M5.Lcd.println("Actualmente no recibo ningún paquete");
    }
  LCD_Clear();
}

// Función para limpiar la pantalla M5
void LCD_Clear(){
  M5.Lcd.fillScreen(BLACK);
  M5.Lcd.setCursor(0, 0);
  M5.Lcd.setTextColor(WHITE);
}

void mandarRaspberry(){
   if (Serial.available() > 0) {
  char command = (char) Serial.read(); 
  
  switch (command) {
  case 'H':
  Serial.println("Hola Mundo"); 
  Serial.write("Hola Mundo");
  break;
  case 'D':
  Serial.println("Adios mundo");
  Serial.write("Adios mundo");
    break;
  } 
}

delay(500);
}
