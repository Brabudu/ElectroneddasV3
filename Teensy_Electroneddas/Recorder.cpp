#include "Recorder.h"
#include <Arduino.h>
#include "Communicator.h"
#include "Cannas.h"
#include "ElFileSystem.h"

#ifndef functions_h
#include "functions.h"
#endif

extern Communicator* com;
extern Cuntzertu* c;
extern ElFileSystem* efs;

extern uint8_t cuntz_num;

//Messaggi:
//R[stato]
//stato: rec, stop, play, end, ready, 1...10


Recorder::Recorder()
  : size(0), stato(IDLE), rate(1), cuntz(0) {
}

bool Recorder::isIdle() {
  return stato == IDLE;
}
bool Recorder::isRecording() {
  return stato == RECORD;
}
bool Recorder::isPlaying() {
  return stato == PLAY;
}

bool Recorder::isReady() {
  return stato == READY;
}

void Recorder::setNome(String nome) {
  strncpy(this->nome, nome.c_str(), 32);
  this->nome[31] = '\0';
}
char* Recorder::getNome() {
  return nome;
}

void Recorder::poke(bool stato) {
  if (isReady() && !stato) startRecord(IMMEDIATE);  //TODO
  if (isRecording() && stato) stop();
}

void Recorder::startRecord(uint8_t mode) {
  //todo
  if (mode == IMMEDIATE) {
    stato = RECORD;
    com->msgWarning("Rrec");
  } else if (mode == GATED) {
    stato = READY;
    com->msgWarning("Rready");
  }
  sample = 0;
  size = 0;
  startTime = millis();
  cuntz = cuntz_num;
}

void Recorder::startPlay(uint8_t mode) {
  //todo
  stato = PLAY;
  sample = 0;
  startTime = millis();
  com->msgWarning("Rplay");
  c->mute(false);
}

void Recorder::stop() {
  //todo
  stato = IDLE;
  sample = 0;
  com->msgWarning("Rstop");
}

uint8_t Recorder::getSample() {
  if ((sample >= size) || (stato == IDLE)) {
    com->msgWarning("Rend");
    stato = IDLE;
    c->mute(true);
    return 0;
  }
  record s = registrazione[sample];
  if (s.time < ((millis() - startTime) * rate)) {
    sample++;

    return s.nota;
  }
  return 0;
}

bool Recorder::putSample(uint8_t nota) {
  if (sample >= MAXSIZE) {
    stato = IDLE;
    size = MAXSIZE;
    com->msgWarning("R10");
    return false;
  }
  record sam;
  sam.time = millis() - startTime;
  sam.nota = nota;
  registrazione[sample++] = sam;
  size = sample;
  if ((size % (MAXSIZE / 10)) == 0) com->msgWarning("R" + String(size / (MAXSIZE / 10)));
  return true;
}

void Recorder::setRate(float r) {
  rate = r;
}

void Recorder::serialize(Stream* s) {
  s->write(&cuntz, sizeof(uint8_t));
  s->write(nome, 32);
  s->write((uint8_t*)&registrazione, sizeof(record) * size);
}
void Recorder::deserialize(Stream* s, bool info) {
  cuntz = s->read();
  for (int i = 0; i < 32; i++) {
    nome[i] = (char)s->read();
  }
  uint8_t* punt = (uint8_t*)&registrazione;

  if (info) return;

  int len = 0;
  while (s->available()) {
    *punt = s->read();
    punt++;
    len++;
  }
  size = len / sizeof(record);
}

void Recorder::parse(String string) {
  String s;
  String sParams[3];
  StringSplitFirst(&string, ' ', &s);
  StringSplitFirst(&string, ' ', &s);

  StringSplit(string, ' ', sParams, 2);
  switch (sParams[0][0]) {
    case '0':
      stop();
      break;
    case '1':
      startRecord(IMMEDIATE);
      break;
    case '2':
      startRecord(GATED);
      break;
    case '3':
      setRate(sParams[1].toFloat());
      startPlay(0);
      break;
    case 's':
      efs->recToFile(this, sParams[1].toInt());
      break;
    case 'l':
      if (efs->recFromFile(this, sParams[1].toInt(), false)) com->msgWarning(String("Rload") + nome);
      else com->msgWarning(String("Rerr"));
      break;
    case 'i':
      if (efs->recFromFile(this, sParams[1].toInt(), true)) com->msgWarning(String("RSave") + nome);
      else com->msgWarning(String("Rerr"));
      break;
    case 'n':
      setNome(string.substring(2, 32));

      break;
    default:
      com->msgError(String("Unknown command: E R ") + s[0]);
  }
}
