//GND - GND
//GND - GND
//VCC - VCC
//SDA - Pin 2
//SCL - Pin 14
 
#include <Wire.h>
 
#define    MPU9250_ADDRESS            0x68//Acelerometro
 
#define    ACC_FULL_SCALE_2_G        0x00  
#define    ACC_FULL_SCALE_4_G        0x08
#define    ACC_FULL_SCALE_8_G        0x10
#define    ACC_FULL_SCALE_16_G       0x18

// Constantes y variables globales

const byte interruptPin = 4;
// declaramos como volatile la variable que cuenta el numero de interrupciones
// si no se hace asi el compilador podria pensar que esta variable no se utiliza nunca y la borrara
volatile byte MOVIMIENTO; // volatile porque puede cambiar en cualquier momento por motivos ajenos al SW

class Acelerometro {

  private:
    int X;
    int Y;
    int Z;

  public:
    Acelerometro (int x, int y, int z)
      :
        X (x),
        Y (y),
        Z (z)
    {
      
    }
      void I2Cread (uint8_t Address, uint8_t Register, uint8_t Nbytes, int8_t* Data) {//Acelerómetro, Registro, Número de Casillas, Lista
      Wire.beginTransmission(Address);//Se comunica con el Acelerómetro
      Wire.write(Register);//Registro 
      Wire.endTransmission();
 
      Wire.requestFrom(Address, Nbytes);//Número de bytes que el I2C le pide al Acelerómetro
      uint8_t index = 0;
      while (Wire.available())//Número de bytes disponibles (14)
      Data[index++] = Wire.read();//Lee los bytes
}
  void I2CwriteByte(uint8_t Address, uint8_t Register, int8_t Data){
   Wire.beginTransmission(Address);
   Wire.write(Register);
   Wire.write(Data);
   Wire.endTransmission();
}
 void ConvertirValoresAcelerometro(int8_t *p, float * q){
   int FS_ACC = 2;
   int FS_GYRO = 250;
   float z_offset=-0.07;
  //   // Convertir registros acelerometro
   X = (p[0] << 8 | p[1]);// H - L
   Y = (p[2] << 8 | p[3]);// H - L
   Z = (p[4] << 8 | p[5]);// H - L

   q[0] = X*FS_ACC/32768; //+ ax_offset;// Digital -> G
   q[1] = Y*FS_ACC/32768; //+ ay_offset;
   q[2] = Z*FS_ACC/32768 + z_offset;
 }
 void configurar(){
   I2CwriteByte(MPU9250_ADDRESS, 28, ACC_FULL_SCALE_2_G);// Acelerómetro, Registro para configurar Acelerómetro, Configurar fondo de escala (MAX) del Acelerómetro en 4G
   
   I2CwriteByte(0x68, 0x6B, 0x00);
   I2CwriteByte(0x68, 0x6C, 0x07);
   I2CwriteByte(0x68, 0x1D, 0x09);
   I2CwriteByte(0x68, 0x38, 0x40);
   I2CwriteByte(0x68, 0x69, 0xC0);
   I2CwriteByte(0x68, 0x1F, 0x1F); // Sensibilidad del sensor al movimiento
   I2CwriteByte(0x68, 0x1E, 0x04); // La frecuencia con que emite datos
   I2CwriteByte(0x68, 0x6B, 0x20);

 }
}; // clase

//----------------------------------------------------------------------------------

Acelerometro a1(0,0,0);

//----------------------------------------------------------------------------------

void handleInterrupt() {
  Serial.println("                  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!                    SE HA DETECTADO UN MOVIMIENTO BRUSCO EN EL SENSOR                    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!                  ");
  MOVIMIENTO = 1;
  MOVIMIENTO = 0;
}

void configurar_Acelerometro () {
  //configuramos el modo de PIN coomo PULLUP para que por defecto estare a nivel alto,
  // la interrupcion se producira a nivel bajo
  Serial.println("Configurando el dispositivo ");
  pinMode(interruptPin, INPUT_PULLUP);
  // Asociamos la interrupcion con el pin, con la funcion y con el umbral
  attachInterrupt(digitalPinToInterrupt(interruptPin), handleInterrupt, FALLING);
}
