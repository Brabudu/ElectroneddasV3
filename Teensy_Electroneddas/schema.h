#pragma once
#include <Audio.h>
#include "synth_waveform2.h"

#ifndef cannas_h
  #include "Cannas.h"
#endif

// GUItool: begin automatically generated code

AudioAnalyzePeak         peak;

AudioSynthWaveform2       tSynth;
AudioSynthWaveform2       msSynth;
AudioSynthWaveform2       mdSynth;

AudioFilterBiquad        bqTumbuStat1;
AudioFilterBiquad        bqTumbuStat2;

AudioFilterBiquad        bqMancsStat;
AudioFilterBiquad        bqMancsDin;

AudioFilterBiquad        bqMancdStat;
AudioFilterBiquad        bqMancdDin;
//AudioFilterLadder        ldMancd; 

AudioMixer4              tMixer;
AudioMixer4              msMixer;
AudioMixer4              mdMixer;

AudioAmplifier           ampL;
AudioAmplifier           ampR;

AudioFilterBiquad        bqOutR;
AudioFilterBiquad        bqOutL;

AudioMixer4              rMixer;         //xy=775.2000122070312,306.20001220703125
AudioMixer4              lMixer;         //xy=778.2000122070312,169.1999969482422

AudioOutputUSB           usb1;           //xy=944.2000122070312,328.20001220703125    //
AudioOutputPT8211        pt8211_1; 

AudioEffectFreeverb      freeverbR;      //xy=1819.1167602539062,472.1166687011719
AudioEffectFreeverb      freeverbL;      //xy=1820.1167602539062,419.1166687011719

AudioConnection          md1_0(mdSynth, bqMancdStat);
AudioConnection          md2_0(mdSynth, bqMancdDin);
//AudioConnection          md3_0(mdSynth, ldMancd);

AudioConnection          md1_1(bqMancdStat, 0, mdMixer, BQ1);
AudioConnection          md2_1(bqMancdDin, 0, mdMixer, BQ0);
//AudioConnection          md3_1(ldMancd, 0, mdMixer, 2);

AudioConnection          md1_2(mdMixer, 0, lMixer, MD);
AudioConnection          md2_2(mdMixer, 0, rMixer, MD);

AudioConnection          ms1_0(msSynth, bqMancsStat);
AudioConnection          ms2_0(msSynth, bqMancsDin);
AudioConnection          ms1_1(bqMancsStat, 0, msMixer, BQ1);
AudioConnection          ms2_1(bqMancsDin, 0, msMixer, BQ0);
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
