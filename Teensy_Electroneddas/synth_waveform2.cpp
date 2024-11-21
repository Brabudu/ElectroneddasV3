/* Audio Library for Teensy 3.X
 * Copyright (c) 2018, Paul Stoffregen, paul@pjrc.com
 *
 * Development of this audio library was funded by PJRC.COM, LLC by sales of
 * Teensy and Audio Adaptor boards.  Please support PJRC's efforts to develop
 * open source software by purchasing Teensy or other PJRC products.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, development funding notice, and this permission
 * notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

 /*Modified by Franciscu Capuzzi */

#include <Arduino.h>
#include "synth_waveform2.h"
#include "arm_math.h"
#include "utility/dspinst.h"



#define BASE_AMPLITUDE 0x6000  // 0x7fff won't work due to Gibb's phenomenon, so use 3/4 of full range.



void AudioSynthWaveform2::update(void)
{
	audio_block_t *block;
	int16_t *bp, *end;
	int32_t val1, val2;
	int16_t magnitude15;
	uint32_t i, ph, index, index2, scale;
	const uint32_t inc = phase_increment;

	ph = phase_accumulator + phase_offset;
	if (magnitude == 0) {
		phase_accumulator += inc * AUDIO_BLOCK_SAMPLES;
		return;
	}
	block = allocate();
	if (!block) {
		phase_accumulator += inc * AUDIO_BLOCK_SAMPLES;
		return;
	}
	bp = block->data;

	switch(tone_type) {

	case WAVEFORM_ARBITRARY:
		if (!arbdata) {
			release(block);
			phase_accumulator += inc * AUDIO_BLOCK_SAMPLES;
			return;
		}
		// len = 256
		for (i=0; i < AUDIO_BLOCK_SAMPLES; i++) {
			index = ph >> 24;
			index2 = index + 1;
			if (index2 >= 256) index2 = 0;
			val1 = *(arbdata + index);
			val2 = *(arbdata + index2);
			scale = (ph >> 8) & 0xFFFF;
			val2 *= scale;
			val1 *= 0x10000 - scale;
			*bp++ = multiply_32x32_rshift32(val1 + val2, magnitude);
			ph += inc;
		}
		break;

case WAVEFORM_TEST:
		magnitude15 = signed_saturate_rshift(magnitude, 16, 1);
		for (i=0; i < AUDIO_BLOCK_SAMPLES; i++) {
			index = ph >> 24;
			index2 = index + 1;
			if (index2 >= 256) index2 = 0;
			
			if (index < (pulse_width>>24)) {
				val1=magnitude15;
			
			} else if (index<(pulse_width>>23)){
				val1=-(magnitude15>>1);
			} else {
				val1=0;
			}
			
			if ((index2) < (pulse_width>>24)) {
				val2=magnitude15;
				
			
			} else if ((index+1)<(pulse_width>>23)){
				val2=-(magnitude15>>1);
			} else {
				val2=0;
			}
			
			scale = (ph >> 8) & 0xFFFF;
			val2 *= scale;
			val1 *= 0x10000 - scale;
			*bp++ = multiply_32x32_rshift32(val1 + val2, magnitude);
			ph += inc;
		}
		break;
		
		case WAVEFORM_TEST2:
			for (i=0; i < AUDIO_BLOCK_SAMPLES; i++)
			{
			  int32_t new_ph = ph + inc ;
			  int32_t val = band_limit_waveform.generate_pulse (new_ph, pulse_width, i) ;
			  int32_t val2 = band_limit_waveform.generate_pulse (new_ph, pulse_width/2, i)>>pulse_fact;
			  
			*bp++ = (int16_t) (((val+val2) * magnitude) >> 17) ;
			 
			  ph = new_ph ;
			}
			break;
		
    case WAVEFORM_TEST3:
			for (i=0; i < AUDIO_BLOCK_SAMPLES; i++)
			{
			  int32_t new_ph = ph + inc ;
			  int32_t val = band_limit_waveform.generate_pulse (new_ph, pulse_width, i) ;
			  int32_t val2 = band_limit_waveform.generate_pulse (new_ph, .5, i)>>pulse_fact;
			  
			*bp++ = (int16_t) (((val+val2) * magnitude) >> 17) ;
			 
			  ph = new_ph ;
			}
			break;
	

	case WAVEFORM_BANDLIMIT_PULSE:
		for (i=0; i < AUDIO_BLOCK_SAMPLES; i++)
		{
		  int32_t new_ph = ph + inc ;
		  int32_t val = band_limit_waveform.generate_pulse (new_ph, pulse_width, i) ;
		  *bp++ = (int16_t) ((val * magnitude) >> 16) ;
		  ph = new_ph ;
		}
		break;

	}
	phase_accumulator = ph - phase_offset;

	if (tone_offset) {
		bp = block->data;
		end = bp + AUDIO_BLOCK_SAMPLES;
		do {
			val1 = *bp;
			*bp++ = signed_saturate_rshift(val1 + tone_offset, 16, 0);
		} while (bp < end);
	}
	transmit(block, 0);
	release(block);
}

//--------------------------------------------------------------------------------
