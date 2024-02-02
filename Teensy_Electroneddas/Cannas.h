//31/12/2021 Diagramma delle classi e nuove strutture con json incorporato
#pragma once

#include <Audio.h>
#include <ArduinoJson.h>
#include "IntervalTimerEx.h"
#include "synth_waveform2.h"

#define cannas_h

#define VERSION 1
#define SUBVERSION 0

#define MS 0
#define MD 1
#define T1 2
#define REVERB 3

#define BQ0 0
#define BQ1 1

#define MAX_PUNTU 20
#define MAX_CUNTZ 16
#define MAX_MOD 7

class Biquad
{
  public:
    Biquad();

    void deserialize(JsonObject jo);
    void serialize(JsonObject jo);
    void parse(String string);

    void setFreq(uint16_t freq);
    uint16_t getFreq();

    void setQ(float q);
    float getQ();

    void setType(char type);
    char getType();

    void setBQ(AudioFilterBiquad* bq, uint8_t stage);
    AudioFilterBiquad* GetBQ();

    void sync();
    void sync(uint16_t freq);


  private:
    uint16_t freq;
    float q;
    char type;
    AudioFilterBiquad* bq;
    uint8_t bqStage;
    const double bqAllPass[5] = {1, 0, 0, 0, 0};
};

class Crai {
  public:
    Crai();

    void deserialize(JsonObject jo);
    void serialize(JsonObject jo);
    void parse(String string);

    void setVol(float vol);
    float getVol();

    void setVolA(float volA);
    float getVolA();

    void setFini(float fini);
    float getFini();

    void setDuty(float duty);
    float getDuty();

    void setPuntu(uint8_t puntu);
    uint8_t getPuntu();
    Biquad* getBQ();

  private:
    float vol;
    float volA;
    float fini;
    float duty;
    uint8_t puntu;
  protected:
    Biquad bq;

};

class Canna {
  public:
    Canna(uint8_t ncrais);

    void deserialize(JsonObject jo);
    void serialize(JsonObject jo);
    void parse(String string);

    void setVolArm(float volArm);
    float getVolArm();

    void setStrb(uint8_t strb);
    uint8_t getStrb();

    void setSonu(uint8_t sonu);
    uint8_t getSonu();

    void setPort(uint8_t port);
    uint8_t getPort();

    void setBaseFreq(float freq);

    void setMIDI(uint8_t channel, uint8_t velocity, uint8_t transposition, uint8_t mode);

    void update(float modulation,float modvol, float modf, float modfilt);

    void sync();

    void playCrai(uint8_t crai);
    void playCrai(uint8_t crai, bool oberta);
    
    uint8_t getCraiAct();

    void setCrais(byte* crais);
    
    void setBiquads(AudioFilterBiquad* bqStat, AudioFilterBiquad* bqDin);

    Biquad* getBiquad();

    void setSynth(AudioSynthWaveform2* s);
    void setMixer(AudioMixer4* mixer);


  protected:
    float volArm;
    uint8_t strb;
    uint8_t sonu;
    uint8_t port=0;
    int timbru;
    float obFactDuty=.8;
    float obFactVol=1.1;

    Biquad  bqStat;
    Biquad  bqDinF;
    Crai* crais;
   
    float baseFreq;
    AudioSynthWaveform2* synth;
    AudioMixer4* mixer;

  private:

  //MIDI
   uint8_t channel=0;
   uint8_t velocity=127;
   uint8_t transposition=24;
   uint8_t mode=0;
   
    uint8_t ncrais;

    uint8_t port_count=0;
    
    volatile uint8_t craiAct;

    volatile float dutyDest;
    volatile float dutyAct = .1;
    volatile float dutyF;

    volatile float freqDest;
    volatile float freqAct = 220;
    volatile float freqF;

    volatile float ffreqDest;
    volatile float ffreqAct = 1000;
    volatile float ffreqF;

    volatile float volDest;
    volatile float volAct = 1;
    volatile float volF;
};

class Cuntzertu {
  private:
    
  public:
    Cuntzertu();

    
    void deserialize(Stream* s);
    void deserialize(const char* s);

    void deserializeFunction(const char* s,int num);
    
    void serialize(Stream* s);
    void parse(String string);

    void writeJSONPrefs(Stream* s);
    void readJSONPrefs(Stream* s);

    void setPreferredCuntz(uint8_t num);
    uint8_t getPreferredCuntz();
    
    void setNome(String nome);
    char* getNome();

    void setDescr(String descr);
    char* getDescr();

    void setVol(float vol);
    void setVolBilT(float volT, float bilT);
    void setVolBilMs(float volMs, float bilMs);
    void setVolBilMd(float volMd, float bilMd);

    float getVol();
    float getVolMs();
    float getVolMd();
    float getVolT();

    float getBilT();
    float getBilMs();
    float getBilMd();

    void setFini(float fini);

    float getFini();

    void setPuntu(uint8_t puntu);
    void setCuntz(uint8_t cuntz);
    void setModal(uint8_t mod);

    uint8_t getPuntu();
    uint8_t getCuntz();
    uint8_t getModal();

    uint8_t getFilterMode();
    void setFilterMode(uint8_t mode);

    void setVerb(float vol, float damp, float room);
    float getVerbVol();
    float getVerbDamp();
    float getVerbRoom();

    uint8_t getSulSens();  
    uint8_t getSulZero();
    uint8_t getSulLimin();
    
    uint8_t getSulProgZ();
    uint8_t getSulProgS();

    void setSulSens(uint8_t sens);
    void setSulZero(uint8_t zero);
    void setSulLimin(uint8_t limin);
    
    void setSulProgZ(uint8_t num);
    void setSulProgS(uint8_t num);

    void setAcordadura(uint8_t num);
    ///////////

    void setBiquads(AudioFilterBiquad* left, AudioFilterBiquad* right);
    void setOutAmps(AudioAmplifier* lOut, AudioAmplifier* rOut);
    void setReverbs(AudioEffectFreeverb* lRev, AudioEffectFreeverb* rRev);
    
    void setCannaMixers(AudioMixer4* lMix, AudioMixer4* rMix);
    void beginTimer(IntervalTimerEx* timer);

    void setGateMode(uint8_t mode);
    uint8_t getGateMode();

    void timerRoutine();
    void sync();

    void syncBT0(Stream*);
    void syncBT1(Stream*);
    void syncBT2(Stream*);
    
    void setSul(float sul);
    void mute();

    static float getBaseFreq();
    static float calcFrequenza(uint8_t nota);
   
  private:
    void syncFreq();
    void send_f(Stream* s, float arg);
    void deserialize(JsonObject jo);
    void calcCrais(uint8_t cuntz, uint8_t modal); 
    byte diatToCroma(byte crai, uint8_t modal);
    
   
  public:
    Canna tumbu=Canna(1);
    Canna mancs=Canna(5);
    Canna mancd=Canna(5);

   

  private:
    char nome[32];
    char descr[64];
    float vol;
    uint8_t cuntz;
    uint8_t mod;
    uint8_t num_acordadura = 0;
    uint8_t puntu;
    float fini;

    float volT;
    float bilT;
    const uint8_t tumbuMixChan = 2;

    float volMs;
    float bilMs;
    const uint8_t mancsMixChan = 0;

    float volMd;
    float bilMd;
    const uint8_t mancdMixChan = 1;

    Biquad lBq;
    Biquad rBq;

    float revVol=0;
    float revDamp=0.5;
    float revRoom=0.5;
    
    uint8_t filterMode=0;

    uint8_t vers=0;

    uint8_t preferredCuntz=0;

    static float freq;

    static float acordadura[];
   
    IntervalTimer* timer;

    AudioAmplifier* lOut;
    AudioAmplifier* rOut;

    AudioMixer4* lMix;
    AudioMixer4* rMix;

    AudioEffectFreeverb* lRev;
    AudioEffectFreeverb* rRev;

    //
    
    //
    float avgRnd = 0;
    uint8_t tmrCount=0;

    ///////
    volatile float sulidu=0;
    volatile bool suling=false;
    volatile bool oldsuling=false;
    
    float avgSulidu=0;
    
    float ssens=30;
    uint8_t slim=35;
    uint8_t szero=50;

    uint8_t sulProgZ=0;
    uint8_t sulProgS=0;

    
    float sulf=0;
    float sulff=1;
    float sulv=1;
       
    float sef=0.7;
    float sev=0.5;
    float seff=0.5;
    
    float sff=1;
    float sfv=1;
    float sfff=1;

    float funtzioni[3][100];
   
    bool  sulFuntz=false;
     
    uint8_t gate=100;
    uint8_t gateMode=0;

    const String filtrus[3]={"0 0 N","3900 1 L","6000 10 h"};

};

/////
