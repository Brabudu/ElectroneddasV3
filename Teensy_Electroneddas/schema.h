#pragma once
#include <Audio.h>
#include "synth_waveform2.h"

#ifndef cannas_h
  #include "Cannas.h"
#endif



// GUItool: begin automatically generated code

AudioSynthWaveform2       tSynth;
AudioSynthWaveform2       msSynth;
AudioSynthWaveform2       mdSynth;

AudioFilterBiquad        bqTumbuStat1;   //xy=608.11669921875,385.1166687011719
AudioFilterBiquad        bqMancdStat;    //xy=608.11669921875,482.1166687011719
AudioFilterBiquad        bqMancdDinCrais;     //xy=610.1166076660156,444.1166687011719
AudioFilterBiquad        bqMancsDinCrais;     //xy=611.1166381835938,235.11666870117188

AudioFilterBiquad        bqMancdDinFin;     
AudioFilterBiquad        bqMancsDinFin;     

AudioFilterBiquad        bqMancsStat;    //xy=611.11669921875,280.1166687011719
AudioFilterBiquad        bqTumbuStat2;   //xy=612.11669921875,345.1166687011719
AudioMixer4              msMixer;        //xy=801.1165771484375,273.1166687011719
AudioMixer4              mdMixer;        //xy=801.1165771484375,469.1166687011719
AudioMixer4              tMixer;         //xy=805.1165771484375,370.1166687011719
AudioEffectFreeverb      freeverbR;      //xy=995.1168212890625,502.1166687011719
AudioMixer4              rMixer;         //xy=996.1166687011719,431.1166687011719
AudioEffectFreeverb      freeverbL;      //xy=998.1168212890625,358.1166687011719
AudioMixer4              lMixer;         //xy=999.1166687011719,294.1166687011719
AudioFilterBiquad        bqOutL;         //xy=1162.1165771484375,328.1166687011719
AudioAmplifier           ampR;           //xy=1164.11669921875,459.1166687011719
AudioAmplifier           ampL;           //xy=1166.11669921875,267.1166687011719
AudioFilterBiquad        bqOutR;         //xy=1166.1165771484375,397.1166687011719
AudioOutputUSB           usb1;           //xy=1334.11669921875,357.1166687011719
AudioAnalyzePeak         peak;           //xy=1372.11669921875,268.1166687011719
AudioOutputPT8211        pt8211_1;       //xy=1481.1165771484375,353.1166687011719

AudioConnection          md1_0(mdSynth, bqMancdStat);
AudioConnection          md2_0(mdSynth, bqMancdDinCrais);
AudioConnection          md3_0(bqMancdDinCrais, bqMancdDinFin);

AudioConnection          md1_1(bqMancdStat, 0, mdMixer, BQ1);
AudioConnection          md2_1(bqMancdDinFin, 0, mdMixer, BQ0);
//AudioConnection          md3_1(ldMancd, 0, mdMixer, 2);

AudioConnection          md1_2(mdMixer, 0, lMixer, MD);
AudioConnection          md2_2(mdMixer, 0, rMixer, MD);

AudioConnection          ms1_0(msSynth, bqMancsStat);
AudioConnection          ms2_0(msSynth, bqMancsDinCrais);
AudioConnection          ms3_0(bqMancsDinCrais, bqMancsDinFin);

AudioConnection          ms1_1(bqMancsStat, 0, msMixer, BQ1);
AudioConnection          ms2_1(bqMancsDinFin, 0, msMixer, BQ0);
AudioConnection          ms1_2(msMixer, 0, lMixer, MS);
AudioConnection          ms2_2(msMixer, 0, rMixer, MS);

AudioConnection          t1_0(tSynth, bqTumbuStat1);
AudioConnection          t2_0(tSynth, bqTumbuStat2);
AudioConnection          t1_1(bqTumbuStat1, 0, tMixer, BQ1);
AudioConnection          t2_1(bqTumbuStat2, 0, tMixer, BQ0);
AudioConnection          t1_2(tMixer, 0, lMixer, T1);
AudioConnection          t2_2(tMixer, 0, rMixer, T1);

AudioConnection          outr_1(rMixer, 0, bqOutR, 0);
AudioConnection          outr_2(bqOutR, 0, ampR, 0);
AudioConnection          outr_4(ampR, 0, usb1, 1);   //
AudioConnection          outr_4b(ampR, 0, pt8211_1, 1);   //

AudioConnection          outriv_1(rMixer, 0, freeverbR, 0);
AudioConnection          outriv_2(freeverbR, 0, rMixer,3);

AudioConnection          outriv_3(lMixer, 0, freeverbL, 0);
AudioConnection          outriv_4(freeverbL, 0, lMixer,3);

AudioConnection          outl_1(lMixer, 0, bqOutL, 0);
AudioConnection          outl_2(bqOutL, 0, ampL, 0);
AudioConnection          outl_4(ampL, 0, usb1, 0);   //
AudioConnection          outl_4b(ampL, 0, pt8211_1, 0);   //

AudioConnection          outl_5(ampL, peak);
