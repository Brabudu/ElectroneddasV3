#pragma once

#include <stdint.h>
#include "SdFat.h"

struct record {
  public:
    uint8_t nota;
    uint8_t reserved;
    unsigned long time;
};

#define IDLE 0
#define READY 1
#define RECORD 2
#define PLAY 3

#define NOMODE 0
#define IMMEDIATE 1
#define GATED 2

#define MAXSIZE 15000

class Recorder {
  private:
    
    record registrazione[MAXSIZE];
    char nome[32];
    uint8_t cuntz;

    uint8_t stato;
    uint16_t sample;
    uint16_t size;
    long startTime;
    float rate;

  public:
    Recorder();
    bool isIdle();
    bool isRecording();
    bool isPlaying();
    bool isReady();
    void parse(String s);

    void setNome(String nome);
    char* getNome();

    

    void poke(bool stato);

    void startRecord(uint8_t mode);
    void startPlay(uint8_t mode);
    void stop();

    void serialize(Stream* s);
    void deserialize(Stream* s, bool info);

    uint8_t getSample();
    bool putSample(uint8_t);

    void setRate(float rate);
};