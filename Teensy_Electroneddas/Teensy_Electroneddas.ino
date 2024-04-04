/*
Required libraries: 

ArduinoJSON 
  https://arduinojson.org/

GyverOLED
  https://github.com/GyverLibs/GyverOLED


*/

#include <Audio.h>
#include <SPI.h>
#include <ArduinoJson.h>
#include <EEPROM.h>
#include <SerialFlash.h>

#include "Cannas.h"
#include "Display.h"
#include "ElFileSystem.h"

#ifndef functions_h
  #include "functions.h"
#endif

#ifndef info_h
  #include "info.h"
#endif

#include "Communicator.h"

#include "2SPISlave_T4/2SPISlave_T4.h"

#include <Entropy.h>

#include "sdios.h"

#define version "2.3.2"

//2.2   12/01/2024  

/*16/4/21
  */

//2.3.1 10/3/24 note negative

//2.3.1 4/4/24 transp midi

const char json[] = "{\"nome\":\"PO\",\"descr\":\"Sol\",\"vol\":0.5,\"cuntz\":1,\"mod\":0,\"puntu\":7,\"fini\":1.0,\"volT\":0.62,\"bilT\":-0.03,\"volMs\":0.98,\"bilMs\":0.0,\"volMd\":1.16,\"bilMd\":0.0,\"bq\":{\"freq\":0,\"q\":0.0,\"type\":\"N\"},\"tumbu\":{\"volArm\":0.9,\"strb\":6,\"sonu\":4,\"port\":10,\"bqStat\":{\"freq\":169,\"q\":1.678804,\"type\":\"H\"},\"bqDinF\":{\"freq\":0,\"q\":0.0,\"type\":\"N\"},\"crais\":[{\"vol\":1.0,\"volA\":1.0,\"fini\":1.0,\"duty\":0.05,\"puntu\":0,\"bq\":{\"freq\":301,\"q\":1.0,\"type\":\"N\"}}]},\"mancs\":{\"volArm\":1.56,\"strb\":2,\"sonu\":1,\"port\":10,\"bqStat\":{\"freq\":2089,\"q\":0.34673685,\"type\":\"B\"},\"bqDinF\":{\"freq\":1148,\"q\":0.3019952,\"type\":\"B\"},\"crais\":[{\"vol\":0.39,\"volA\":2.28,\"fini\":1.0,\"duty\":0.33,\"puntu\":7,\"bq\":{\"freq\":549,\"q\":1.0,\"type\":\"B\"}},{\"vol\":0.52,\"volA\":1.84,\"fini\":1.0,\"duty\":0.3,\"puntu\":9,\"bq\":{\"freq\":2137,\"q\":1.0,\"type\":\"B\"}},{\"vol\":0.62,\"volA\":1.4,\"fini\":0.9928057,\"duty\":0.28,\"puntu\":11,\"bq\":{\"freq\":1737,\"q\":1.0,\"type\":\"B\"}},{\"vol\":0.69,\"volA\":1.56,\"fini\":1.0,\"duty\":0.28,\"puntu\":12,\"bq\":{\"freq\":1348,\"q\":1.0,\"type\":\"B\"}},{\"vol\":0.69,\"volA\":1.6,\"fini\":0.99639636,\"duty\":0.33,\"puntu\":14,\"bq\":{\"freq\":933,\"q\":0.52480745,\"type\":\"B\"}}]},\"mancd\":{\"volArm\":1.62,\"strb\":5,\"sonu\":1,\"port\":10,\"bqStat\":{\"freq\":2089,\"q\":0.4365158,\"type\":\"B\"},\"bqDinF\":{\"freq\":1778,\"q\":1.0,\"type\":\"B\"},\"crais\":[{\"vol\":0.52,\"volA\":2.44,\"fini\":1.0,\"duty\":0.34,\"puntu\":12,\"bq\":{\"freq\":630,\"q\":1.0,\"type\":\"B\"}},{\"vol\":0.56,\"volA\":2.72,\"fini\":0.99639636,\"duty\":0.3,\"puntu\":14,\"bq\":{\"freq\":2238,\"q\":1.0,\"type\":\"B\"}},{\"vol\":0.64,\"volA\":1.32,\"fini\":1.0,\"duty\":0.24,\"puntu\":16,\"bq\":{\"freq\":1659,\"q\":0.91201085,\"type\":\"B\"}},{\"vol\":0.63,\"volA\":1.24,\"fini\":1.0036167,\"duty\":0.26,\"puntu\":17,\"bq\":{\"freq\":1584,\"q\":0.45708817,\"type\":\"B\"}},{\"vol\":0.65,\"volA\":1.56,\"fini\":1.0,\"duty\":0.35,\"puntu\":19,\"bq\":{\"freq\":1122,\"q\":0.44157046,\"type\":\"B\"}}]}}";



//Schema V1.0

#include "schema.h"



////// HARDWARE //////////////////////

#define CS_CON  2     //SPI CS
#define OFF_CON  38   //Todo

#define V_CONTR  10   //SPI Vcc


SPISlave_T4 <&SPI1, SPI_8_BITS> mySPI; 
;
////
volatile uint8_t lt_pkt[6]; // Buffer ricezione
volatile uint8_t lt_pr = 0; //
volatile uint8_t lt_pp = 0; //

uint8_t last_error = 0;
uint8_t last_error_mem = 0;
///

volatile long inact_timer = 0; //


////
float old_sul=0;
int sul_count=0;

//byte oldcrai[2];

////// gestione console //////
char ser_buffer[2000];
int ser_b_index = 0;

String command="";

//////
//Battery
  #define BATT  A8   //Todo
    float charge=4.2;
////// gestione Bluetooth //////
char cstring1[64];
byte addbuffer[64];
int cindex1 = 0;
bool cpronto1 = false;

bool bt_controller = true;
bool bt_control = false;

const bool BT_CRLF = true;
//////

//Gestione monitor
#define CRAIS 1
#define ESA   2
#define SUL   4
#define BT    8
#define CLIP  16

int mon_mode=0;

byte act_file=0; // Caricamento cuntzertu

// Timer monitor (clip)
IntervalTimer monitorTimer;

uint8_t cuntz_num=0;
#define MAX_CUNTZ_NUM 200

//

Cuntzertu* c; 

Display* d;

ElFileSystem* efs;

Communicator* com;

void setup() {

  // Pulsantis e led
  pinMode(PIN_PULS_OK, INPUT_PULLUP);
  pinMode(PIN_PULS_CANC, INPUT_PULLUP);

  pinMode(OFF_CON, OUTPUT);
  digitalWrite(OFF_CON, HIGH);

  pinMode(V_CONTR, OUTPUT);
  
  
  pinMode(LED_BUILTIN, OUTPUT);
  
  // SPI slave
  pinMode(CS_CON, OUTPUT); //CS
  digitalWrite(CS_CON, LOW);

  AudioMemory(20);
  Entropy.Initialize();

  Serial.begin(115200);   //USB
  

  com=new Communicator(&Serial);

  //Bluetooth
  //Serial5.begin(115200);    //BTooth
  //Serial5.begin(9600);
  //Serial5.addMemoryForRead(addbuffer,64);  // Per comunicare con la app

  digitalWrite(V_CONTR, HIGH);
  delay(100);
  digitalWrite(CS_CON,HIGH);
  
  delay(100);
 
  lMixer.gain(3,0.3);
  rMixer.gain(3,0.3);

  //mdMixer.gain(2,0.7);  //TEST
  //ldMancd.frequency(500);
  //ldMancd.resonance(1);
   
  mySPI.onReceive(receiving);
  mySPI.begin();

  c=new Cuntzertu();

  d=new Display();

  efs=new ElFileSystem();
  
  if (!efs->isMounted()) {
        com->msgError("SD Error");
        d->showLogo("SD ERR");
    } else {
        d->showLogo(version);
    }
  initCuntz();
 
  monitorTimer.begin(monitoring, 200000);
  monitorTimer.priority(255);
  
  digitalWrite(V_CONTR, HIGH);

  delay(500);
  digitalWrite(CS_CON,LOW);
  
  if (digitalRead(PIN_PULS_OK)==false) d->test();
  else d->initMenu();

  

}

void loop() {
  
  //USB Serial
  pollSerial();
  
  //BT Serial
  pollBT();
  
  //SPI
  pollSPI();

  while(usbMIDI.read()){}

  if (command.length()>0) {
    parse(command);
    command="";
  }
}

void pollSerial() {
   while (Serial.available() > 0) 
  {
    inact_timer = 0;
    
    char byteBuffer = Serial.read();  
    ser_buffer[ser_b_index++] = byteBuffer;

    if (ser_b_index>=2000) {
      com->msgError("Serial buffer full");
      ser_b_index=0;
    }
    
    if (byteBuffer == 10)
    {
      ser_buffer[--ser_b_index] = 0;        
      parse(ser_buffer);  //Generali
      ser_b_index = 0;
    }
  }
}

void pollSPI() {
    if (lt_pr != lt_pp) {
      inact_timer = 0;
      
      lt_pp++;
      uint8_t b = lt_pkt[lt_pp % 6];
    sona(b);
  } 
}
void pollBT() {
 /*
  if (!bt_control) {
  //Bluetooth
    while ((Serial5.available() > 0) && (!cpronto1))
    {
      char byteBuffer = Serial5.read();
      Serial.print("BTD> ");
      Serial.print(byteBuffer);
      if (!((cindex1==0)&&((byteBuffer==' ')||(byteBuffer==0)))) { //trim
        
         
        cstring1[cindex1++] = byteBuffer;
        if (byteBuffer == 10)
        {
          cstring1[--cindex1] = 0;
  
          if ((mon_mode&BT)||bt_controller) {
            Serial.print("BT> ");
            Serial.println(cstring1);
          }
          
          if (!bt_controller)  parse(cstring1);  //Generali
       
          cindex1 = 0;
          cpronto1 = false;
        }
      }
    }
*/
}

void sona(byte b) {  

      uint8_t canna = 0;
      uint8_t nota = 0;

      uint8_t hi_nibb=b&0xf0;
      uint8_t lo_nibb=b&0x0f;

      if (hi_nibb==0x70) { //Comando da controller
        int dir=0;
        if (lo_nibb==0) return;
        if (b&0x1) dir=1;
        if (b&0x8) dir=-1;

        if (!efs->isMounted()) return;
        do {
          cuntz_num=(cuntz_num+dir+MAX_CUNTZ_NUM)%MAX_CUNTZ_NUM;
        } while (!efs->cuntzFromFileJson(c,cuntz_num,true));
        c->setPreferredCuntz(cuntz_num);
        d->displayPage();
        d->update();
      
      } else {
        nota = byteToCrai(b, &canna);
               
        if (mon_mode&CRAIS)  {
          String can;
         
          if (canna==1) can="S"; 
          else can="D";
          
            String msg=can+":";
            char myHex[2] = "";
   
            if (mon_mode&ESA) ltoa(lo_nibb,myHex,16);
            else ltoa(nota,myHex,16);
            msg+=String(myHex);
            com->msgWarning(msg,true);
              
        }
       
        if (canna == 1) {
          c->mancs.playCrai(nota,lo_nibb);
        }
        if (canna == 2) {
          c->mancd.playCrai(nota,lo_nibb);
        }
        if (canna == 3) {           //Sys msg
          if (b==3) {
            last_error=0;   //Controller prontu (0x33)
            last_error_mem=0;
            com->msgOk("Controller prontu");
          }
          else {
            last_error=1+(b>>1);
            last_error_mem=last_error;
          }
        }
      }
}

void monitoring() {
  if ((mon_mode&CLIP)&&peak.available()) {    
    uint8_t lPeak=peak.read() * 100.0;
    if (lPeak>99) com->msgWarning("CLIP",false);
  }
  
  if (last_error!=0) {
    String msg="Controller err "+String(last_error);
    com->msgError(msg);
    last_error=0;
  }

  //Batteria
  int v=analogRead(BATT);
  float volt=v/155.152; //(v=x/1024*3,3*2);
  charge=(charge+volt)/2; //max=0,7

  d->refreshBattery();
  
  inact_timer++;

  if (inact_timer>2000) {
    //digitalWrite(OFF_CON,LOW);
    //delay(5000);
  }
  
}
float getCharge() {
  return charge;
}
uint8_t byteToCrai(uint8_t b, uint8_t* msg) {

  *msg = ((b >> 4) & 0x3) ^ 3;
  if ((b & 0x80) == 0x80) *msg = 0;

  b = ~b & 0xf;// & ~(c.cannas[*msg - 1].mask & 0xf);

  if (b & 8) return (4);
  if (b & 4) return (3);
  if (b & 2) return (2);
  if (b & 1) return (1);
  return (0);
}



void receiving() {

  if (mySPI.active()) {
    mySPI.pushr(0);
    uint16_t data = mySPI.popr();
    
    if (data < 128) {
        lt_pr++;
        lt_pkt[lt_pr % 6] = (uint8_t)data;
          
      /*
        if (recording) {
        nodas[pointer].nota=(uint8_t)d;
        nodas[pointer].time=(uint16_t)((millis()>>4)-startTime);

        pointer++;

        if (pointer==MAX_NODAS) recording=false;*/
    }
   else {
      float sul=(float)(data&0x7F);
      
      if (sul!=old_sul) {
        old_sul=sul;
        
        c->setSul(sul);    
        if (mon_mode&SUL) {
          com->msgWarning('s'+String(sul),true);
        }
      }
  }

}
}
void parse(String s) {
  s.trim();
  if (s.length()==0) return;
  if (s[0]!='E') {
    c->parse(s); //Cuntzertu
  }
  else {
    String sParams[5];
    StringSplit(s, ' ', sParams, 5);
    switch (sParams[1][0]) {
      case 'i':
        com->msgInfo(String("Id :")+EEPROM.read(0));
        com->msgInfo(String("Version :")+version);
        com->msgInfo(String("Audio memory: ")+AudioMemoryUsageMax());    
        com->msgInfo(String("Audio CPU max: ")+AudioProcessorUsageMax());
        break;
        /* Noise Test
      case 't':
        nMixer.gain(0,sParams[2].toFloat());
        nMixer.gain(1,sParams[3].toFloat());
        break;
      case 'f':
        bqNoise.setHighpass(0,sParams[2].toInt(),1);
        noise1.amplitude(sParams[3].toFloat());
        break;
        */
      case 'I':
        EEPROM.write(0,sParams[2].toInt()&0xff);
      break;
      case 'm':
        mon_mode=sParams[2].toInt();
        break;
      case 'B':
        //if (BT_CRLF)  Serial5.println(sParams[2]);
        //else Serial5.print(sParams[2]);
        break;
      case 'b':
        bt_control=sParams[2].toInt();
        break;
      case 'd':
        efs->listaFiles(false);
        break;
      case 'D':
        efs->listaFiles(true);
        break;
      case 'x':
        efs->deleteFile(sParams[2]);
        break;
      case 'e':
        efs->execute(sParams[2].toInt());
        break;
      case 'n':
        efs->newFile(sParams[2]);
        break;
      case 't':
        efs->torra(sParams[2].toInt());
        break;
      case 'g':
        c->setGateMode(sParams[2].toInt());
        break;
      case 's':
        efs->cuntzToFile(c,sParams[2].toInt());
        break;
      case 'l':
        efs->cuntzFromFileJson(c,sParams[2].toInt(),true);
        break;
      /*case 'L':
        ldMancd.frequency(sParams[2].toInt());
        ldMancd.resonance(sParams[3].toFloat());
        mdMixer.gain(2,sParams[4].toFloat());  //TEST
        break;
        */
      case 'f':
        //efs->cuntzFromFileFunction(c,sParams[2].toInt(),false);
        efs->cuntzFromFile(c,sParams[2],false,' ');
        break;
      case 'r':
        efs->cuntzFromFileJson(c,sParams[2].toInt(),false);
        break;
      case 'h':
        resetController();
        break;
      case 'z':
        efs->readPrefs(c);
        break;
      case 'p':
        efs->savePrefs(c);
        break;
      case 'c':
        delay(500);
        initCuntz();
        break;
      case 'u':
        d->displayPage();
        d->update();
        break;
      case 'w':
        if (sParams[2].toInt()==0) {
          d->setEnabled(false);        
        } else {
          d->setEnabled(true);        
        }
        break;
      case 'v':   //reverb 
       {
        float d=sParams[2].toInt()/10;
        float r=sParams[3].toInt()/10;
        freeverbR.roomsize(r);
        freeverbR.damping(d);
        freeverbL.roomsize(r);
        freeverbL.damping(d);
       }
      break;
      case 'Y': //restore
        restore();
      break;
      case 'N':
        playNoda();
        break;
      case '?':
        com->msgInfo(info_E);
        break;
      default:
        com->msgError(String("Unknown command: E ")+sParams[1][0]);
        break;
    }
  }
  //d->displayPage();
}

void initCuntz() {
  
  setCannas();
  efs->readPrefs(c);
  
  if (digitalRead(PIN_PULS_CANC)) {
    if (!efs->cuntzFromFileJson(c,c->getPreferredCuntz(),true)) {
       c->deserialize(json);
       c->sync();;
    } else {
      cuntz_num=c->getPreferredCuntz();
    }
  } else {
    c->deserialize(json);
    c->sync();
  }
  
  //c->setVerb(.1,.8,.1);

  c->tumbu.setMIDI(1,127,22+c->getPuntu(),0);
  c->mancs.setMIDI(2,127,22+c->getPuntu(),0);
  c->mancd.setMIDI(3,127,22+c->getPuntu(),0);

  c->tumbu.playCrai(0);
  c->mancs.playCrai(0);
  c->mancd.playCrai(0);

  c->beginTimer();
  
  playNoda();
  
  efs->execute(0);
  
  c->mute(true);
}
void setCannas() {
  c->setBiquads(&bqOutL, &bqOutR);
  c->setOutAmps(&ampL, &ampR);
  c->setReverbs(&freeverbL, &freeverbR);
  c->setCannaMixers(&lMixer, &rMixer);

  c->tumbu.setMixer(&tMixer);
  c->tumbu.setSynth(&tSynth);
  c->tumbu.setBiquads(&bqTumbuStat1, &bqTumbuStat2);

  c->mancs.setMixer(&msMixer);
  c->mancs.setSynth(&msSynth);
  c->mancs.setBiquads(&bqMancsStat, &bqMancsDin);

  c->mancd.setMixer(&mdMixer);
  c->mancd.setSynth(&mdSynth);
  c->mancd.setBiquads(&bqMancdStat, &bqMancdDin);
}

void playNoda() {
  int t=c->getGateMode();
  c->setGateMode(0);
  c->mute(false);
  delay(500);
  c->mancs.playCrai(4);
  c->mancd.playCrai(2);
  delay(100);
  c->mancd.playCrai(1);
  delay(900);
  c->mancs.playCrai(0);
  c->mancd.playCrai(0);
  delay(1000);
  c->mancs.playCrai(4);
  c->mancd.playCrai(4);
  c->setGateMode(t);
  com->msgOk("Noda");
}

void restore() {
    for (int i=0;i<16;i++) {
      efs->copy(220+i,i);
    }
    com->msgOk("Restored");
}

void resetController() {
  
  delay(100);
  digitalWrite(CS_CON,HIGH);
  delay(100);
  digitalWrite(CS_CON,LOW);
  delay(100);
}
