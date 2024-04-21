//31/12/2021 Diagramma delle classi e nuove strutture con json incorporato

//TODO:
//Errore json }}
//Tumbu negativo

#include "Cannas.h"
#include "ElFileSystem.h"
#include "samples.h"

#include <GyverOLED.h>
#include <Entropy.h>
#include "Communicator.h"

#ifndef functions_h
  #include "functions.h"
#endif

#ifndef info_h
  #include "info.h"
#endif

extern ElFileSystem efs;
extern Communicator* com;
extern  Cuntzertu* c;         //Solo per MIDI :( 

//////
    boolean scala[7]= {true,true,false,true,true,true,false};
    
    byte crais_ms_fio[5] ={9,11,12,13,14};
    byte crais_md_fio[5] ={11,14,15,16,17};

    byte crais_ms_po[5] ={11,12,13,14,15};
    byte crais_md_po[5] ={14,15,16,17,18};

    byte crais_md_fiu[5] ={16,18,19,20,21};
    
    byte crais_ms_med[5] ={7,8,9,10,11};
    byte crais_md_med[5] ={11,13,14,15,16};


/////
//Limin,zero
uint8_t sulProgZData[6][2]={{5,10},{7,20},{10,30},{13,40},{17,50},{20,60}};

//H,M,L
uint8_t sulProgSData[6][3]={{15,20,25},{10,15,20},{20,25,30},{25,32,40},{30,40,50},{35,47,60}};

/////// BIQUAD

Biquad::Biquad(): freq(1000), q(1), type('N'), bqStage(0) {}

void Biquad::deserialize(JsonObject jo) {
  this->freq = jo["freq"];
  this->q = jo["q"];
  const char* t = jo["type"];
  if (t) this->type = *(t);
}

void Biquad::serialize(JsonObject jo) {
  jo["freq"] = freq;
  jo["q"] = q;
  jo["type"] = String(type);
}

void Biquad::parse(String string) {   // (int)freq (float)q (char) type
  String sParams[3];
  StringSplit(string, ' ', sParams, 3);

  this->freq = sParams[0].toInt();
  this->q = sParams[1].toFloat();
  this->type = sParams[2][0];

  sync();
}

void Biquad::setFreq(uint16_t freq) {
  this->freq = freq;
}
uint16_t Biquad::getFreq() {
  return freq;
}

void Biquad::setQ(float q) {
  this->q = q;
}
float Biquad::getQ() {
  return q;
}

void Biquad::setType(char type) {
  this->type = type;
}
char Biquad::getType() {
  return type;
}

void Biquad::setBQ(AudioFilterBiquad* bq, uint8_t stage) {
  this->bq = bq;
  this->bqStage = stage;
}
AudioFilterBiquad* Biquad::GetBQ() {
  return bq;
}

void Biquad::sync() {
  sync(this->freq);
}
void Biquad::sync(uint16_t freq) {
  switch (type) {
    case 'H':
      bq->setHighpass(bqStage, freq, q);
      break;
    case 'B':
      bq->setBandpass(bqStage, freq, q);
      break;
    case 'L':
      bq->setLowpass(bqStage, freq, q);
      break;
    case 'h':
      bq->setHighShelf(bqStage, freq,q, .7);
      break;
    case 'l':
      bq->setLowShelf(bqStage, freq,q, .7);
      break;
    default:
      bq->setCoefficients(bqStage, bqAllPass);
  }
}

/////// CRAI


Crai::Crai() : vol(1), volA(1), fini(1), duty(0.4), puntu(10) {
  }

void Crai::deserialize(JsonObject jo) {
  this->vol = jo["vol"];
  this->volA = jo["volA"];
  this->fini = jo["fini"];
  this->duty = jo["duty"];
  this->puntu = jo["puntu"];
  this->bq.deserialize(jo["bq"]);
}
void Crai::serialize(JsonObject jo) {
  jo["vol"] = vol;
  jo["volA"] = volA;
  jo["fini"] = fini;
  jo["puntu"] = puntu;
  jo["duty"] = duty;
  bq.serialize(jo.createNestedObject("bq"));
}

void Crai::parse(String string) {   
  String s;
  String sParams[3];
  StringSplitFirst(&string, ' ', &s);
  switch (s[0]) {
    case 'F':
      bq.parse(string);
      break;
    case 'v':
      StringSplit(string, ' ', sParams, 2);
      this->vol = sParams[0].toFloat();
      this->volA = sParams[1].toFloat();
      break;
    case 'p':
      StringSplit(string, ' ', sParams, 1);
      this->puntu = sParams[0].toInt();
      break;
    case 'f':
      StringSplit(string, ' ', sParams, 1);
      this->fini = sParams[0].toFloat();
      break;
    case 'd':
      StringSplit(string, ' ', sParams, 1);
      this->duty = sParams[0].toFloat();
      break;

    default:
      com->msgError(String("Unknown Crai command: ")+s[0]);
  }
}

void Crai::setVol(float vol) {
  this->vol = vol;
}
float Crai::getVol() {
  return vol;
}

void Crai::setVolA(float volA) {
  this->volA = volA;
}
float Crai::getVolA() {
  return volA;
}

void Crai::setFini(float fini) {
  this->fini = fini;
}
float Crai::getFini() {
  return fini;
}

void Crai::setDuty(float duty) {
  this->duty = duty;
}
float Crai::getDuty() {
  return duty;
}

void Crai::setPuntu(uint8_t puntu) {
  this->puntu = puntu;
}
uint8_t Crai::getPuntu() {
  return puntu;
}
Biquad* Crai::getBQ() {
  return &bq;
}

/////// CANNA

// TODO : ObFact!!!!!!!!


Canna::Canna(uint8_t nc): volArm(1), strb(1), sonu(1), port(10),timbru(0),ncrais(nc) { 
      crais = (Crai *)malloc(sizeof(Crai) * nc);  
    }

void Canna::deserialize(JsonObject jo) {
  this->volArm = jo["volArm"];
  this->strb = jo["strb"];
  this->sonu = jo["sonu"];
  this->port = jo["port"];
  if (jo.containsKey("obFactDuty")) { //V1.1
    this->obFactDuty = jo["obFactDuty"];
    this->obFactVol = jo["obFactVol"];
    this->timbru = jo["timbru"];
  }
  
  this->bqStat.deserialize(jo["bqStat"]);
  this->bqDinF.deserialize(jo["bqDinF"]);
    
  for (int i = 0; i <ncrais; i++) {
    this->crais[i].deserialize(jo["crais"][i]);// + String(i)]);
  }
}
void Canna::serialize(JsonObject jo) {
  jo["volArm"] = volArm;
  jo["strb"] = strb;
  jo["sonu"] = sonu;
  jo["port"] = port;
  jo["obFactDuty"]=obFactDuty; 
  jo["obFactVol"]=obFactVol; 
  jo["timbru"] = timbru;
  bqStat.serialize(jo.createNestedObject("bqStat"));
  bqDinF.serialize(jo.createNestedObject("bqDinF"));
  JsonArray c = jo.createNestedArray("crais");
  for (int i = 0; i <ncrais; i++) {
    crais[i].serialize(c.createNestedObject());
  }
}

void Canna::parse(String string) {  
  String s;
  String sParams[4];
  if (string.length()<1) return;
   StringSplitFirst(&string, ' ', &s);
  
  switch (s[0]) {
    case 'F':
      bqStat.parse(string);
      break;
    case 'D':
      bqDinF.parse(string);
      break;
    case 'C':
      crais[(byte)(s[1]-'0')].parse(string);
      playCrai(craiAct);
      break;
    case 'v':
      StringSplit(string, ' ', sParams, 1);
      this->volArm = sParams[0].toFloat();
      break;
    case 'o':
      StringSplit(string, ' ', sParams, 2);
      this->obFactDuty = sParams[0].toFloat();
      this->obFactVol = sParams[1].toFloat();
      break;
    case 'c':
      StringSplit(string, ' ', sParams, 1);
      this->sonu = sParams[0].toInt();
      setSonu(sonu);
      break;
    case 's':
      StringSplit(string, ' ', sParams, 2);
      this->strb = sParams[0].toInt();
      this->port = sParams[1].toInt();
      break;
    case 't':
      StringSplit(string, ' ', sParams, 1);
      this->timbru = sParams[0].toInt();
      break;
    case 'p':   
      playCrai(string.toInt());    
      break;
    case 'm':   
      StringSplit(string, ' ', sParams, 4);
      setMIDI(sParams[0].toInt(),sParams[1].toInt() ,sParams[2].toInt(),sParams[3].toInt());
      break;
    case '?':
      com->msgInfo(info_C);
      break;
    default:
      com->msgError(String("Unknown Canna command: ")+s[0]);
  }
}


void Canna::setVolArm(float volArm) {
  this->volArm = volArm;
}
float Canna::getVolArm() {
  return volArm;
}

void Canna::setStrb(uint8_t strb) {
  this->strb = strb;
}
uint8_t Canna::getStrb() {
  return strb;
}

void Canna::setSonu(uint8_t sonu) {
  this->sonu = sonu;

  switch (sonu) {
    case 0:
      waveAmplitude=.5;
      synth->begin(0, Cuntzertu::getBaseFreq(), WAVEFORM_BANDLIMIT_PULSE);
      break;
    case 1:
    case 2:
    case 3:
      waveAmplitude=.5;
      synth->begin(0, Cuntzertu::getBaseFreq(), WAVEFORM_TEST2);
      synth->pulseFact(3-sonu);
      break;
    default:
      waveAmplitude=.6;
      synth->arbitraryWaveform(waves[sonu - 4], 650.0);
      synth->begin(0, Cuntzertu::getBaseFreq(), WAVEFORM_ARBITRARY);
  }
}
uint8_t Canna::getSonu() {
  return sonu;
}

void Canna::setPort(uint8_t port) {
  this->port = port;
}
uint8_t Canna::getPort() {
  return port;
}

void Canna::sync() {
  
  //// Synth
  setSonu(sonu);
  
  //// BQ
  bqStat.sync();
  bqDinF.sync();

  for (int i = 0; i <ncrais; i++) {
    crais[i].getBQ()->sync();
  }
   playCrai(craiAct);
  
}
////////

void Canna::setSynth(AudioSynthWaveform2* s) {
  this->synth = s;
}
Biquad* Canna::getBiquad() {
  return &bqStat;
}

void Canna::setMixer(AudioMixer4* mixer) {
  this->mixer = mixer;
}

void Canna::setBiquads(AudioFilterBiquad* stat, AudioFilterBiquad* din) {
  bqStat.setBQ(stat, 0);
  bqDinF.setBQ(din, 1);

  for (int i = 0; i < ncrais; i++) {
    crais[i].getBQ()->setBQ(din, 0);
  }
}

void Canna::playCrai(uint8_t crai) {
  Canna::playCrai(crai,0);
}

void Canna::playCrai(uint8_t crai, uint8_t hexcrai) {
  
  bool oberta=obertura(hexcrai);

  lastHCrai=hexcrai;

  //duty
  dutyDest = crais[crai].getDuty()*pow(2,(float)timbru/20);
  if (dutyDest>0.5) dutyDest=0.5;
  
  if ((oberta)&&(obFactDuty!=0)) dutyDest*=obFactDuty;
  dutyF = dutyAct / dutyDest;
  
  //freq
  freqDest = Cuntzertu::calcFrequenza(crais[crai].getPuntu())* crais[crai].getFini();  

  freqF =freqAct / freqDest;
  
  //vol
  volDest = crais[crai].getVol();
  if ((oberta)&&(obFactVol!=0)) volDest*=obFactVol;

  volAct*=0.8;
  
  if (volDest==0) volDest=0.001; 
  
  volF = volAct / volDest;
  
  //bq
  ffreqDest = crais[crai].getBQ()->getFreq();
  ffreqF = ffreqAct / ffreqDest;

  port_count=0;

  usbMIDI.sendNoteOff(crais[craiAct].getPuntu()+transposition, velocity, channel);
  usbMIDI.sendNoteOn(crais[crai].getPuntu()+transposition, velocity, channel);
  craiAct = crai;
}

uint8_t Canna::getCraiAct() {
  return craiAct;
}
uint8_t Canna::getHCraiAct() {
  return lastHCrai;
}
bool Canna::obertura(uint8_t b) {
  b = ~b & 0xf;
  if ((b&8)&&(b&4)) return true;
  if ((b&4)&&(b&2)) return true;
  if ((b&2)&&(b&1)) return true;
  return false;
}

void Canna::mute(bool mute) {
  if (mute) {
    synth->amplitude(0);
  } else {
    synth->amplitude(waveAmplitude);
  }
}
/*
float Canna::frequenza(uint8_t nota) {

    
    return baseFreq*(pow(2,nota/12))*naturale[nota%12];
}
*/

void Canna::setCrais(byte* crais) {
    for (int i=0;i<=4;i++)
      {
        this->crais[i].setPuntu(crais[i]);
      }
}

void Canna::update(float mod,float sulv, float sulf, float sulff) {


  float mfact=(1+mod*((float)strb/1000));

  sulf= sulf*(2+(crais[craiAct].getPuntu()-crais[0].getPuntu()))*5; 

  if ((port==0)||(port_count%port==0)) {
  
    //duty
    dutyAct = dutyDest * dutyF/(sulv*sulv);
  
    //vol
    volAct = volDest * volF;
       
    //bq
    ffreqAct = ffreqDest * ffreqF;
      
    //freq
    freqAct = freqDest * freqF;
  }
  synth->pulseWidth(dutyAct*mfact);
  synth->frequency(freqAct*mfact+sulf);
  mixer->gain(BQ0, mfact*volAct * crais[craiAct].getVolA()*sulv);
  mixer->gain(BQ1, mfact*volAct * (4 - crais[craiAct].getVolA())*volArm*sulv);
  crais[craiAct].getBQ()->sync(ffreqAct*mfact+sulff); 
  
  
 
  if ((port==0)||(port_count%port==0)) {
      
    if (volF>1.01) {
      //dutyF = pow(dutyF, 0.4);
      freqF = pow(freqF, 0.4);
      volF = pow(volF, 0.8);
      ffreqF = pow(ffreqF, 0.6);
    }
    else {
      //dutyF = sqrt(dutyF);
      if (freqF<0.99) freqF = sqrt(freqF);
      else freqF=1;
      if (volF<0.99) volF = pow(volF, 0.9);
      else volF=1;
      if (ffreqF<0.99) ffreqF = pow(ffreqF, 0.7);
      else ffreqF=1;
    }
    if ((dutyF<=0.99)||(dutyF>=1.01)) dutyF = sqrt(dutyF);
    else dutyF=1;
    //ffreqF = sqrt(ffreqF);
  }
  port_count++;
}

void Canna::setMIDI(uint8_t channel, uint8_t velocity, uint8_t transposition, uint8_t mode) {
  this->channel=channel;
  this->velocity=velocity;
  this->transposition=transposition;
  this->mode=mode;
}
void Canna::setMIDI(uint8_t transposition) {
  this->transposition=transposition;
}

/////////

float Cuntzertu::freq;
float Cuntzertu::acordadura[13];

const float naturale[13] = {1, 1.0666666667 , 1.125, 1.2, 1.25, 1.3333333333, 1.40625, 1.5, 1.6, 1.6666666667, 1.8, 1.875, 2};
const float temperata[13] = {1, 1.0594630944, 1.1224620483,1.189207115,1.2599210499,1.3348398542,1.4142135624,1.4983070769,1.587401052,1.6817928305,1.7817974363,1.8877486254,2};
const float pitagorica[13] = {1,1.0678710938,1.125,1.1851851852,1.265625,1.3333333333,1.423828125,1.5,1.6018066406,1.6875,1.7777777778,1.8984375,2};


Cuntzertu::Cuntzertu(): nome("VOID"), descr(""), vol(1), cuntz(0), mod(0), puntu(7), fini(1), volT(1), bilT(0), volMs(1), bilMs(0), volMd(1), bilMd(0) {
    setAcordadura(0);
  }

void Cuntzertu::deserialize(Stream* s) {
  StaticJsonDocument<4000> doc;
  deserializeJson (doc, *s);
  JsonObject jo = doc.as<JsonObject>();
  deserialize(jo);
}
void Cuntzertu::deserialize(const char* s) {
  StaticJsonDocument<4000> doc;
  deserializeJson (doc, s);
  JsonObject jo = doc.as<JsonObject>();
  deserialize(jo);
}

void Cuntzertu::deserializeFunction(const char* s,int num) {
  StaticJsonDocument<2000> doc;
  deserializeJson (doc, s);
  JsonObject jo = doc.as<JsonObject>();
  for (int i = 0; i <100; i++) {
    this->funtzioni[num][i]=jo["function"][i];
  }
}

void Cuntzertu::readJSONPrefs(Stream* s) {
  StaticJsonDocument<1000> doc;
  deserializeJson (doc, *s);
  JsonObject jo = doc.as<JsonObject>();
  setVol(jo["vol"]);
  setSulProgZ(jo["sprogZ"]);
  setSulProgS(jo["sprogS"]);
  /*} else {
    setSulSens(jo["ssens"]);
    setSulLimin(jo["slim"]);
    setSulZero(jo["szero"]);
  }*/
  setFilterMode(jo["filterMode"]);
  setGateMode(jo["gateMode"]);
  setPreferredCuntz(jo["preferredCuntz"]);
  
  setVerb(jo["revVol"],jo["revDamp"],jo["revRoom"]);
    
}
void Cuntzertu::writeJSONPrefs(Stream* s) {
  DynamicJsonDocument jo(1500);
  jo["vol"] = vol;
  
  //jo["ssens"]=ssens;
  //jo["slim"]=slim;
  //jo["szero"]=szero;
  jo["sprogZ"]=sulProgZ;
  jo["sprogS"]=sulProgS;
  jo["gateMode"]=gateMode;
  jo["filterMode"]=filterMode;

  jo["revVol"] = revVol;
  jo["revDamp"] = revDamp;
  jo["revRoom"] = revRoom;
  jo["preferredCuntz"]=preferredCuntz;
  
  //lBq.serialize(jo.createNestedObject("bq"));   //nd'abastat unu
  serializeJson(jo, *s);
  s->println();
  s->flush();
}

void Cuntzertu::deserialize(JsonObject jo) {
  
  strncpy(this->nome, jo["nome"], 32);
  strncpy(this->descr, jo["descr"], 64);
 
  this->cuntz = jo["cuntz"];
  this->mod = jo["mod"];
  this->puntu = jo["puntu"];
  this->fini = jo["fini"];
  this->volT = jo["volT"];
  this->bilT = jo["bilT"];
  this->volMs = jo["volMs"];
  this->bilMs = jo["bilMs"];
  this->volMd = jo["volMd"];
  this->bilMd = jo["bilMd"];
  this->vers = jo["vers"];
/*
  if ((jo.containsKey("slim"))&&(jo["slim"]!=0)) {
    this->ssens = jo["ssens"];
    this->slim = jo["slim"];
    this->szero = jo["szero"];
  }
  */
/*
  if (jo.containsKey("revVol")) {
    this->revVol = jo["revVol"];
    this->revDamp = jo["revDamp"];
    this->revRoom = jo["revRoom"];
  }
*/
  if (jo.containsKey("num_acordadura")) {
    this->num_acordadura = jo["num_acordadura"];
  }
  
  //this->lBq.deserialize(jo["bq"]);
  //this->rBq.deserialize(jo["bq"]);
  
  this->tumbu.deserialize(jo["tumbu"]);
  this->mancs.deserialize(jo["mancs"]);
  this->mancd.deserialize(jo["mancd"]);
}

void Cuntzertu::serialize(Stream* s) {
  DynamicJsonDocument jo(4000);
  //JsonObject jo=doc.as<JsonObject>();

  jo["nome"] = nome;
  jo["descr"] = descr;
  
  jo["cuntz"] = cuntz;
  jo["mod"] = mod;
  jo["puntu"] = puntu;
  jo["num_acordadura"] = num_acordadura;
  jo["fini"] = fini;
  jo["volT"] = volT;
  jo["bilT"] = bilT;
  jo["volMs"] = volMs;
  jo["bilMs"] = bilMs;
  jo["volMd"] = volMd;
  jo["bilMd"] = bilMd;
/*
  jo["revVol"] = revVol;
  jo["revDamp"] = revDamp;
  jo["revRoom"] = revRoom;
  */
  jo["vers"] = vers;

/*
  jo["slim"]=slim;
  jo["szero"]=szero;

  */
  //lBq.serialize(jo.createNestedObject("bq"));   //nd'abastat unu

  tumbu.serialize(jo.createNestedObject("tumbu"));
  mancs.serialize(jo.createNestedObject("mancs"));
  mancd.serialize(jo.createNestedObject("mancd"));

  serializeJson(jo, *s);
  s->println();
  s->flush();
}
void Cuntzertu::syncBT0(Stream* s) {
  send_f(s,(float)cuntz);
  send_f(s,gateMode);
  send_f(s,gateMode);
}

void Cuntzertu::syncBT1(Stream* s) { 
  send_f(s,vol);
  send_f(s,volT);
  send_f(s,volMs);
  send_f(s,volMd);
 
}
void Cuntzertu::syncBT2(Stream* s) {
  send_f(s,fini);
  send_f(s,(float)puntu);
}

void Cuntzertu::send_f(Stream* s, float arg)
{
  // get access to the float as a byte-array:
  byte * data = (byte *) &arg; 

  // write the data to the serial
  s->write (data, sizeof (arg));
}

void Cuntzertu::parse(String string) {
  String s;
  String sParams[3];
  StringSplitFirst(&string, ' ', &s);

  switch (s[0]) {
    case 'T':
      tumbu.parse(string);
      break;
    case 'S':
      mancs.parse(string);
      break;
    case 'D':
      mancd.parse(string);
      break;
    case 'F':
      if (s[1]!='p') {
        lBq.parse(string);
        rBq.parse(string);
        filterMode=255;
      } else {
        StringSplit(string, ' ', sParams, 1);
        setFilterMode(sParams[0].toInt());
      }
      break;
    case 'n':   //Nome
      setNome(string);
      break;
    case 'u':   //Nome
      timerRoutine();
      break;
    case 'd':   //Descrizione
      setDescr(string);
      break;
    case 'p':   //puntu
      StringSplit(string, ' ', sParams, 1);
      setPuntu(sParams[0].toInt());
      break;
    case 'a':   //acordadura
      StringSplit(string, ' ', sParams, 1);
      num_acordadura=sParams[0].toInt();
      setAcordadura(num_acordadura);   
      setPuntu(puntu);  
      break;
    case 'f':   //fini
      StringSplit(string, ' ', sParams, 1);
      setFini(sParams[0].toFloat());    
      break;
    case 'c':   //cuntz e mod
      StringSplit(string, ' ', sParams, 2);
      this->cuntz = sParams[0].toInt();
      this->mod = sParams[1].toInt();
      calcCrais(this->cuntz,this->mod);
      break;
    case 'v':   //volumi
      StringSplit(string, ' ', sParams, 2);
      switch (s[1]) {
        case 'c':   //cuntz
          setVol(sParams[0].toFloat());
          break;
        case 't':   //cuntz
          setVolBilT(sParams[0].toFloat(), sParams[1].toFloat());
          break;
        case 's':   //cuntz
          setVolBilMs(sParams[0].toFloat(), sParams[1].toFloat());
          break;
        case 'd':   //cuntz
          setVolBilMd(sParams[0].toFloat(), sParams[1].toFloat());
          break;
        default:
          com->msgError(String("!Unknown command: v")+s[0]);
      }
      break;
    case 's':   //sulidu
      StringSplit(string, ' ', sParams, 3);
      slim=sParams[0].toInt();
      ssens=sParams[1].toInt();
      szero=sParams[2].toInt();
      break;

    case 'z':   //sulidu prog
      StringSplit(string, ' ', sParams, 2);
      setSulProgS(sParams[0].toInt());
      setSulProgZ(sParams[1].toInt());
      break;

    case 'Z':   //sulidu prog
      if (string.length()<16) {
        sulFuntz=false;
      }  else {
        int num=s[1]-'0';
        deserializeFunction(string.c_str(),num);
        sulFuntz=true;
      }
         
      break;
      
    case 'r':   //reverb
      StringSplit(string, ' ', sParams, 3);
      setVerb(sParams[0].toFloat(),sParams[1].toFloat(),sParams[2].toFloat());
      break;
    case 'J':
      com->setWarningEnabled(false);
      Serial.print("?");
      serialize(&Serial);
      com->setWarningEnabled(true);
      break;
    case 'P':
      com->setWarningEnabled(false);
      Serial.print("?P");
      writeJSONPrefs(&Serial);
      com->setWarningEnabled(true);
      break;
    case 'B':
      serialize(&Serial5);
      Serial5.println();
      break;
    case 'b':
      StringSplit(string, ' ', sParams, 1);
      if (sParams[0].toInt()==1) syncBT1(&Serial5);
      if (sParams[0].toInt()==2) syncBT2(&Serial5);
      break;
    case '?':
        com->msgInfo(info_B);
        break;
    default:
      com->msgError(String("Unknown command: ")+s[0]);
  }
  
}

void Cuntzertu::setAcordadura(uint8_t num) {
  float *data;
  
  switch (num) {
    case 1:
      data=&temperata[0];
    break;
    case 2:
      data=&pitagorica[0];
    break;
    case 0:  
    default:
      data=&naturale[0];
    break;
  }

  //Serial.println(num);
  for (int i=0;i<13;i++)
  {
    acordadura[i]=*(data+i);
  }
}

void Cuntzertu::setNome(String nome) {
  strncpy(this->nome, nome.c_str(), 32);
  this->nome[31] = '\0';
}
char* Cuntzertu::getNome() {
  return nome;
}

void Cuntzertu::setDescr(String descr) {
  strncpy(this->descr, descr.c_str(), 64);
  this->descr[63] = '\0';
}
char* Cuntzertu::getDescr() {
  return descr;
}

void Cuntzertu::setVol(float vol) {
  this->vol = vol;

  this->lOut->gain(vol);
  this->rOut->gain(vol);
}

float Cuntzertu::getVol() {
  return vol;
}
float Cuntzertu::getVolMs() {
  return volMs;
}
float Cuntzertu::getVolMd() {
  return volMd;
}
float Cuntzertu::getVolT() {
  return volT;
}

float Cuntzertu::getBilMs(){
  return bilMs;
}
float Cuntzertu::getBilMd(){
  return bilMd;
}
float Cuntzertu::getBilT(){
  return bilT;
}

void Cuntzertu::setVolBilT(float volT, float bilT) {
  if (volT<0) volT=0;
  if (volT>2) volT=2;
  if (bilT<-1) bilT=-1;
  if (bilT>1) bilT=1;
  
  this->volT = volT;
  this->bilT = bilT;

  this->lMix->gain(tumbuMixChan, volT * (1 - bilT) / 2);
  this->rMix->gain(tumbuMixChan, volT * (1 + bilT) / 2);
}

void Cuntzertu::setVolBilMs(float volMs, float bilMs) {
  if (volMs<0) volMs=0;
  if (volMs>2) volMs=2;
  if (bilMs<-1) bilMs=-1;
  if (bilMs>1) bilMs=1;
  
  this->volMs = volMs;
  this->bilMs = bilMs;

  this->lMix->gain(mancsMixChan, volMs * (1 - bilMs) / 2);
  this->rMix->gain(mancsMixChan, volMs * (1 + bilMs) / 2);
}

void Cuntzertu::setVolBilMd(float volMd, float bilMd) {
  if (volMd<0) volMd=0;
  if (volMd>2) volMd=2;
  if (bilMd<-1) bilMd=-1;
  if (bilMd>1) bilMd=1;
  
  this->volMd = volMd;
  this->bilMd = bilMd;

  this->lMix->gain(mancdMixChan, volMd * (1 - bilMd) / 2);
  this->rMix->gain(mancdMixChan, volMd * (1 + bilMd) / 2);
}

void Cuntzertu::setFini(float fini) {
  if (fini<0.94) fini=0.94;
  if (fini>1.06) fini=1.06;
  this->fini = fini;
  syncFreq();
  
  tumbu.playCrai(tumbu.getCraiAct());
  mancs.playCrai(mancs.getCraiAct());
  mancd.playCrai(mancd.getCraiAct());
}

float Cuntzertu::getFini() {
  return fini;
}

uint8_t Cuntzertu::getCuntz() {
  return cuntz;
}
uint8_t Cuntzertu::getModal() {
  return mod;
}
void Cuntzertu::setPuntu(uint8_t puntu) {
  this->puntu = puntu%MAX_PUNTU;
  syncFreq();
  tumbu.playCrai(tumbu.getCraiAct());
  mancs.playCrai(mancs.getCraiAct());
  mancd.playCrai(mancd.getCraiAct());

  tumbu.setMIDI(24+puntu);
  mancs.setMIDI(24+puntu);
  mancd.setMIDI(24+puntu);

}

void Cuntzertu::setCuntz(uint8_t cuntz) {
   this->cuntz=cuntz%MAX_CUNTZ;
   calcCrais(getCuntz(),getModal());
}
void Cuntzertu::setModal(uint8_t mod) {
   if (mod==255) mod=MAX_MOD-1;
   this->mod=mod%MAX_MOD;
   calcCrais(getCuntz(),getModal());
}

uint8_t Cuntzertu::getPuntu() {
  return puntu;
}

uint8_t Cuntzertu::getSulSens() {
  return (int)ssens;
}

uint8_t Cuntzertu::getSulZero() {
  return szero;
}

uint8_t Cuntzertu::getSulLimin() {
  return slim;
}

uint8_t Cuntzertu::getSulProgZ() {
  return sulProgZ;
}
uint8_t Cuntzertu::getSulProgS() {
  return sulProgS;
}

void Cuntzertu::setSulSens(uint8_t sens) {
  ssens=sens;
  //TODO: 
  sulProgZ=0;
  sulProgS=0;
}

void Cuntzertu::setSulZero(uint8_t zero) {
  szero=zero;
  sulProgZ=0;
  sulProgS=0;
}
void Cuntzertu::setSulLimin(uint8_t limin) {
  slim=limin;
  sulProgZ=0;
  sulProgS=0;
}

void Cuntzertu::setSulProgZ(uint8_t num) {
  sulProgZ=num;
  slim=sulProgZData[num][0];
  szero=sulProgZData[num][1];

}
void Cuntzertu::setSulProgS(uint8_t num) {
  sulProgS=num;
  ssens=sulProgSData[sulProgZ][num];
  /*Serial.println(slim);
  Serial.println(szero);
  Serial.println(ssens);*/
}

uint8_t Cuntzertu::getFilterMode() {
  return filterMode;
}

void Cuntzertu::setFilterMode(uint8_t fm) {
  filterMode=fm%3;
  lBq.parse(filtrus[filterMode]);
  rBq.parse(filtrus[filterMode]);
}

void Cuntzertu::setVerb(float vol, float damp, float room) {
    if (vol<0) vol=0;
  if (vol>0.3) vol=0.3;

  if (damp<0) damp=0;
  if (damp>1) damp=1;

  if (room<0) room=0;
  if (room>0.5) room=0.5;

  revVol=vol;
  revDamp=damp;
  revRoom=room;
  
  lRev->damping(damp);
  rRev->damping(damp);

  lRev->roomsize(room);
  rRev->roomsize(room);

  lMix->gain(REVERB,vol);
  rMix->gain(REVERB,vol); 
}
float Cuntzertu::getVerbVol() {
  return revVol;
}
float Cuntzertu::getVerbDamp() {
  return revDamp;
}
float Cuntzertu::getVerbRoom() {
  return revRoom;
}

///////////

void Cuntzertu::setBiquads(AudioFilterBiquad* left, AudioFilterBiquad* right) {
  lBq.setBQ(left, 0);
  rBq.setBQ(right, 0);
}

void Cuntzertu::setOutAmps(AudioAmplifier* lOut, AudioAmplifier* rOut) {
  this->lOut = lOut;
  this->rOut = rOut;
}

void Cuntzertu::setCannaMixers(AudioMixer4* lMix, AudioMixer4* rMix) {
  this->lMix = lMix;
  this->rMix = rMix;
}

void Cuntzertu::setReverbs(AudioEffectFreeverb* lRev, AudioEffectFreeverb* rRev) {
  this->lRev = lRev;
  this->rRev = rRev;
}

void Cuntzertu::beginTimer() {
  timer.begin([this] { timerRoutine(); }, 700);
  timer.priority(255);
}

void Cuntzertu::timerRoutine() {
  
  tumbu.update(avgRnd,sulv,sulf,sulff);
  mancs.update(avgRnd,sulv,sulf,sulff);
  mancd.update(avgRnd,sulv,sulf,sulff);

  //Random
  if (tmrCount%30==0) {
    float rnd = Entropy.rnorm(0, 2);
    const float fact = 0.95;
  
    if ((rnd * rnd) > 1) avgRnd=avgRnd* fact + (1 - fact) * rnd / 2;
  }

  
  //Sulidu
  if (tmrCount%10==0) {  

    if (mancs.getHCraiAct()==0) gateMs++;
    else gateMs=0;

    if (mancd.getHCraiAct()==0) gateMd++;
    else gateMd=0;

    avgSulidu=(avgSulidu*2+sulidu)/3; //Media 3 a 1  

    if (((avgSulidu>slim)&&suling)||(avgSulidu>slim+5)) {             
            if (suling==false) sulcount++;
            suling=true;
            if (gateMode!=GATEMODE_ONOFF) {
                
                mute(false);
                
                float sul=(avgSulidu-szero)/ssens;
                float asul=abs(sul);
               
              if (sulFuntz) {
                  int avg=(int)avgSulidu;
                  sulv=funtzioni[0][avg]+1;
                  sulf=funtzioni[1][avg];
                  sulff=funtzioni[2][avg]+1;
              } else {
                  sulf=(sul*(1-sef)/(sef+1-2*sef*asul))*sff;
                  sulv=(sul*(1-sev)/(sev+1-2*sev*asul)+1)*sfv;
                  sulff=(sul*(1-seff)/(seff+1-2*seff*asul)+1)*sfff;
              }
              
            }
        } else {
          suling=false;
          sulf=0;         
          sulff=1;
        }

        switch (gateMode) {
          case GATEMODE_CRAIS:
            if ((gateMs>120)&&(gateMd>120)) {            
              gateMs=121;
              gateMd=121;
              mute(true);
            }
            if ((gateMs==0)||(gateMd==0)) {   
              sulv=1; 
              mute(false);
            }
          break;
          case GATEMODE_ONOFF:
            if (sulcount%2) {
              mute(false);
              sulv=1;
            } else {
              mute(true);
            }
          break;
        case GATEMODE_SUL:
          if (!suling) mute(true);
        break;
        case GATEMODE_CS:
           if (gateMs>120) {            
              gateMs=121;
              mancs.mute(true);
              tumbu.mute(true);
            }
            if (gateMd>120) {            
              gateMd=121;
              mancd.mute(true);
            }
            if (!suling) mute(true);
        break;
        case GATEMODE_NO:
        default:
            sulv=1; 
            mute(false);            
        break;       
      }
  }
  tmrCount++;
}

void Cuntzertu::setGateMode(uint8_t mode){
  gateMode=mode%GATEMODEMAX; 
  sulcount=0;
  gateMs=0;
  gateMd=0;

}

uint8_t Cuntzertu::getGateMode(){
  return gateMode;
}

void Cuntzertu::sync() {

  muteOut (true);
  // Volumi

  lMix->gain(tumbuMixChan, volT * (1 - bilT) / 2);
  rMix->gain(tumbuMixChan, volT * (1 + bilT) / 2);

  lMix->gain(mancsMixChan, volMs * (1 - bilMs) / 2);
  rMix->gain(mancsMixChan, volMs * (1 + bilMs) / 2);

  lMix->gain(mancdMixChan, volMd * (1 - bilMd) / 2);
  rMix->gain(mancdMixChan, volMd * (1 + bilMd) / 2);

  // BQ
  
  lBq.sync();
  rBq.sync();
 
  //Acordadura
  setAcordadura(num_acordadura);
  syncFreq();

  tumbu.sync();
  mancs.sync();
  mancd.sync(); 

  muteOut(false);
}

void Cuntzertu::setSul(float sul) {
  if (sul>110) sul=0;
  this->sulidu=sul;
}

void Cuntzertu::mute(bool mute) {  
  tumbu.mute(mute);
  mancs.mute(mute);
  mancd.mute(mute);
}
void Cuntzertu::muteOut(bool mute) { 
  if (mute) {
    this->lOut->gain(0);
    this->rOut->gain(0);
  } else {
    setVol(getVol());
  }

}

void Cuntzertu::syncFreq() {
  freq = 55.0f*this->getFini();
  freq=calcFrequenza(puntu + 3);
}
float Cuntzertu::calcFrequenza(uint8_t nota) {
   float f=freq;
   while (nota>=244) { //Tumbu neg
    nota+=12;
    f=f/2;
   }

   return f*(pow(2,nota/12))*acordadura[nota%12];  

}
float Cuntzertu::getBaseFreq() {
  return freq;
}

void Cuntzertu::setPreferredCuntz(uint8_t num) {
  this->preferredCuntz=num;
}
uint8_t Cuntzertu::getPreferredCuntz() {
  return this->preferredCuntz;
}

void Cuntzertu::calcCrais(uint8_t cuntz, uint8_t modal) { 
      
      byte mancosa[5] ={0,1,2,3,4};
      byte mancosedda[5] ={0,1,2,3,4};
      
      switch (cuntz) {
      case 0://Fiorassiu
        memcpy(&mancosa, &crais_ms_fio,5);
        memcpy(&mancosedda, &crais_md_fio,5);
        break;
      case 1://punt'e organu
        memcpy(&mancosa, &crais_ms_po,5);
        memcpy(&mancosedda, &crais_md_po,5);
        break;
      case 2://mediana
        memcpy(&mancosa, &crais_ms_med,5);
        memcpy(&mancosedda, &crais_md_med,5);
        break;
      case 3://mediana a pipia
        memcpy(&mancosa, &crais_ms_med,5);
        memcpy(&mancosedda, &crais_md_fio,5);
        break;
      case 4://Fiuda
        memcpy(&mancosa, &crais_ms_med,5);
        memcpy(&mancosedda, &crais_ms_fio,5);
        break;
      case 5://Spinellu
        memcpy(&mancosa, &crais_md_po,5);
        memcpy(&mancosedda, &crais_md_med,5);
        break;
      case 6://Spinellu a pipia
        memcpy(&mancosa, &crais_md_po,5);
        memcpy(&mancosedda, &crais_md_fio,5);
        break;
      case 7://mediana farsa
        memcpy(&mancosa, &crais_ms_po,5);
        memcpy(&mancosedda, &crais_md_med,5);
        break;
      case 8://samponnia
        memcpy(&mancosa, &crais_ms_po,5);
        memcpy(&mancosedda, &crais_md_fio,5);
        break;
      case 9://moriscu
        memcpy(&mancosa, &crais_md_med,5);
        memcpy(&mancosedda, &crais_ms_po,5);
        break;
      case 10://para
        memcpy(&mancosa, &crais_md_po,5);
        memcpy(&mancosedda, &crais_md_po,5);
        break;
      case 11://fiuddedda
        memcpy(&mancosa, &crais_md_po,5);
        memcpy(&mancosedda, &crais_md_fiu,5);
        break;
      default:
        break;
      }

      for (int i=0;i<=4;i++)
      {
        mancosa[i]=diatToCroma(mancosa[i],modal);
        mancosedda[i]=diatToCroma(mancosedda[i],modal);
      }

      mancs.setCrais(mancosa);
      mancd.setCrais(mancosedda);
}

byte Cuntzertu::diatToCroma(byte diat, uint8_t modal) {
      byte out=0;
            
      for (int i=0;i<diat;i++)
      {
        if (scala[(i+modal)%7]) out+=2;
        else out+=1;
      }
      
      return out;
    }
