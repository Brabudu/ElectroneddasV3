#include "Recorder.h"
#include <Arduino.h>
#include "Communicator.h"
#include "Cannas.h"

extern Communicator* com;
extern Cuntzertu* c;

Recorder::Recorder() {
  stato=IDLE;
}

bool Recorder::isIdle() {
  return stato==IDLE;
}
bool Recorder::isRecording() {
  return stato==RECORD;
}
bool Recorder::isPlaying() {
  return stato==PLAY;
}

bool Recorder::isReady() {
  return stato==READY;
}

void Recorder::poke(bool stato) {
  if (isReady()&&!stato) startRecord(IMMEDIATE); //TODO 
  if (isRecording()&&stato) stop(); 
}

void Recorder::startRecord(uint8_t mode) {
  //todo
  if (mode==IMMEDIATE) stato=RECORD;
  if (mode==GATED) stato=READY;
  sample=0;
  size=0;
  startTime=millis();
  com->msgInfo("Rec ...");
}

void Recorder::startPlay(uint8_t mode) {
  //todo
  stato=PLAY;
  sample=0;
  startTime=millis();
  com->msgInfo("Play ...");
  c->mute(false);
}

void Recorder::stop() {
  //todo
  stato=IDLE;
  sample=0;
  com->msgInfo("STOP");
  com->msgInfo(size);
}

uint8_t Recorder::getSample() {
  if (sample>=size) {
    com->msgInfo("STOP");
    stato=IDLE;
    c->mute(true);
    return 0;
  }
  record s=registrazione[sample];
  if (s.time<(millis()-startTime)) {
    sample++;
    
    return s.nota;
  }
  return 0;
}

bool Recorder::putSample(uint8_t nota) {
  if (sample>=MAXSIZE) {
    stato=IDLE;
    size=MAXSIZE;
    Serial.println("end");
    return false;
  }
  record sam;
  sam.time=millis()-startTime;
  sam.nota=nota;
  registrazione[sample++]=sam;
  size=sample;
  return true;
}
