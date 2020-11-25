#include "HX711.h"
#include "soc/rtc.h"

#define DOUT  2
#define CLK  5

HX711 balanza(DOUT, CLK);

const int EchoPin = 21;
const int TriggerPin = 22;
int altura = 0;

void setup() {
 Serial.begin(115200);
 pinMode(TriggerPin, OUTPUT);
 pinMode(EchoPin, INPUT);

  rtc_clk_cpu_freq_set(RTC_CPU_FREQ_80M); 
  Serial.print("Lectura del valor del ADC:  ");
  Serial.println(balanza.read());
  Serial.println("No ponga ningun  objeto sobre la balanza");
  Serial.println("Destarando...");
  balanza.set_scale(24000); //La escala por defecto es 1 47945/2KG = 23972
  balanza.tare(20);  //El peso actual es considerado Tara.
  //Serial.println("Coloque un peso conocido:"); //CODIGO CALIBRADO
  Serial.println("Lista para pesar");

}

void loop() {
  
 if (Serial.available() > 0) {
  char command = (char) Serial.read();
  if(command == 'A'){ 
     altura = distancia(TriggerPin, EchoPin);
     Serial.print(altura); 
     Serial.print(",");
     Serial.println(balanza.get_units(20),3);
    }
   else {
      Serial.print("La letra no es correcta: "+command);     
    }
 
  }

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
 altura = 200-distanciaCm;
 return altura;
}

int calculoAltura(int distanciaTotal){
  //Tendremos como referencia que el sensor está a 2 metros del suelo
  return 200-distanciaTotal;
}
