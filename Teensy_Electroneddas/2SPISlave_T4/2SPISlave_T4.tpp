/*MIT License

Copyright (c) 2021 Antonio Brewer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/*Adapted for alternative pins of Teensy 4.1 by Franciscu "Brabudu" Capuzzi */

#define SLAVE_CR spiAddr[4]
#define SLAVE_FCR spiAddr[22]
#define SLAVE_IER spiAddr[6]
#define SLAVE_CFGR0 spiAddr[8]
#define SLAVE_CFGR1 spiAddr[9]
#define SLAVE_TDR spiAddr[25]
#define SLAVE_RDR spiAddr[29]
#define SLAVE_SR spiAddr[5]
#define SLAVE_TCR_REFRESH spiAddr[24] = (0UL << 27) | LPSPI_TCR_FRAMESZ(bits - 1) | 0xc0000000
#define SLAVE_TCR spiAddr[24]
#define SLAVE_PORT_ADDR volatile uint32_t *spiAddr = &(*(volatile uint32_t*)(0x40394000 + (0x4000 * _portnum)))
#define SLAVE_PINS_ADDR volatile uint32_t *spiAddr = &(*(volatile uint32_t*)(0x401F84EC + (_portnum * 0x10)))

 
void static lpspi4_slave_isr() {
  _LPSPI4->SLAVE_ISR();
}


SPISlave_T4_FUNC SPISlave_T4_OPT::SPISlave_T4() {
  if ( port == &SPI1 ) {
    _LPSPI4 = this;
	
	// 0=LPSPI1 1=LPSPI2 2=LPSPI3 3=LPSPI4
    _portnum = 2;
	
	//(pag 1079)
	// 
    CCM_CCGR1 |= (3UL << (_portnum*2));
	
	
    nvic_irq = 32 + _portnum;
    _VectorsRam[16 + nvic_irq] = lpspi4_slave_isr;

    // Alternate pins not broken out on Teensy 4.0/4.1 for LPSPI4 
    SLAVE_PINS_ADDR;
	
	/*
	(pag 840)
	LPSP1
	PCS0_SELECT_INPUT 
	0 GPIO_SD_B0_01_ALT4 — Selecting Pad: GPIO_SD_B0_01 for Mode: ALT4
 	1 GPIO_EMC_30_ALT3 — Selecting Pad: GPIO_EMC_30 for Mode: ALT3
	SCK_SELECT_INPUT
	0 GPIO_EMC_27_ALT3 — Selecting Pad: GPIO_EMC_27 for Mode: ALT3
	1 GPIO_SD_B0_00_ALT4 — Selecting Pad: GPIO_SD_B0_00 for Mode: ALT4
	...
	
	LPSP3
	PCS0_SELECT_INPUT
	0 GPIO_AD_B0_03_ALT7 — Selecting Pad: GPIO_AD_B0_03 for Mode: ALT7
	1 GPIO_AD_B1_12_ALT2 — Selecting Pad: GPIO_AD_B1_12 for Mode: ALT2
	
	SCK_SELECT_INPUT
	0 GPIO_AD_B0_00_ALT7 — Selecting Pad: GPIO_AD_B0_00 for Mode: ALT7
	1 GPIO_AD_B1_15_ALT2 — Selecting Pad: GPIO_AD_B1_15 for Mode: ALT2
	
	SDI_SELECT_INPUT
	0 GPIO_AD_B0_02_ALT7 — Selecting Pad: GPIO_AD_B0_02 for Mode: ALT7
	1 GPIO_AD_B1_13_ALT2 — Selecting Pad: GPIO_AD_B1_13 for Mode: ALT2
	
	SDO_SELECT_INPUT
	0 GPIO_AD_B0_01_ALT7 — Selecting Pad: GPIO_AD_B0_01 for Mode: ALT7
	1 GPIO_AD_B1_14_ALT2 — Selecting Pad: GPIO_AD_B1_14 for Mode: ALT2
*/
    spiAddr[0] = 0; /* PCS0_SELECT_INPUT */
    spiAddr[1] = 1; /* SCK_SELECT_INPUT */
    spiAddr[2] = 0; /* SDI_SELECT_INPUT */
    spiAddr[3] = 1; /* SDO_SELECT_INPUT */
		
	IOMUXC_SW_MUX_CTL_PAD_GPIO_AD_B0_03 = 0x7; /*ALT2 LPSPI3 PCS0 (CS1) 0 */
	IOMUXC_SW_MUX_CTL_PAD_GPIO_AD_B1_15 = 0x2; /*ALT7 LPSPI3 SCK (CLK1) 27 */
    IOMUXC_SW_MUX_CTL_PAD_GPIO_AD_B0_02 = 0x7; /*ALT2 LPSPI3 SDI (MISO1) 1 */
    IOMUXC_SW_MUX_CTL_PAD_GPIO_AD_B1_14 = 0x2; /*ALT7 LPSPI3 SDO (MOSI1) 26 */
    
	
  } 
}

SPISlave_T4_FUNC bool SPISlave_T4_OPT::active() {
  SLAVE_PORT_ADDR;
  return ( !(SLAVE_SR & (1UL << 9)) ) ? 1 : 0;
}


SPISlave_T4_FUNC bool SPISlave_T4_OPT::available() {
  SLAVE_PORT_ADDR;
  return ( (SLAVE_SR & (1UL << 8)) ) ? 1 : 0;
}


SPISlave_T4_FUNC void SPISlave_T4_OPT::pushr(uint32_t data) {
  SLAVE_PORT_ADDR;
  SLAVE_TDR = data;
}


SPISlave_T4_FUNC uint32_t SPISlave_T4_OPT::popr() {
  SLAVE_PORT_ADDR;
  uint32_t data = SLAVE_RDR;
  SLAVE_SR = (1UL << 8); /* Clear WCF */
  return data;
}

SPISlave_T4_FUNC void SPISlave_T4_OPT::SLAVE_ISR() {
	
	SLAVE_PORT_ADDR;
	
  if ( _spihandler ) {
    _spihandler();
    SLAVE_SR = 0x3F00;
    asm volatile ("dsb");
    return;
  }

 while ( !(SLAVE_SR & (1UL << 9)) ) { /* FCF: Frame Complete Flag, set when PCS deasserts */
    if ( SLAVE_SR & (1UL << 11) ) { /* transmit error, clear flag, check cabling */
      SLAVE_SR = (1UL << 11);
      transmit_errors++;
    }
    if ( (SLAVE_SR & (1UL << 8)) ) { /* WCF set */
      uint32_t val = SLAVE_RDR;
      Serial.print(val); Serial.print(" ");
      SLAVE_TDR = val;
      SLAVE_SR = (1UL << 8); /* Clear WCF */
    }
  }
  Serial.println();
  SLAVE_SR = 0x3F00; /* Clear remaining flags on exit */
  asm volatile ("dsb");
}


SPISlave_T4_FUNC void SPISlave_T4_OPT::begin() {
  /*
  SLAVE_PORT_ADDR;
  SLAVE_CR = LPSPI_CR_RST; /* Reset Module (pag 2871) */
  //SLAVE_CR = 0; /* Disable Module */
  //SLAVE_FCR = 0;    //x10001; /* 1x watermark for RX and TX */
  //SLAVE_IER = 0x03 | 0x0300; /* Interrupt enable bits */
  //SLAVE_CFGR0 = 0;
  //SLAVE_CFGR1 = 4; //auto pcs
  //SLAVE_CR |= LPSPI_CR_MEN;// | LPSPI_CR_DBGEN; /* Enable Module, Debug Mode */
  //SLAVE_SR = 0x3F00; /* Clear status register */
  //SLAVE_TCR_REFRESH;
  //SLAVE_TCR= 0xc0000007; //mode 3
  //SLAVE_TDR = 0x0; /* dummy data, must populate initial TX slot */
  //NVIC_ENABLE_IRQ(nvic_irq);
  //NVIC_SET_PRIORITY(nvic_irq, 1);
 SLAVE_PORT_ADDR;
  SLAVE_CR = LPSPI_CR_RST; /* Reset Module */
  SLAVE_CR = 0; /* Disable Module */
  SLAVE_FCR = 0;//x10001; /* 1x watermark for RX and TX */
  SLAVE_IER = 0x1; /* RX Interrupt */
  SLAVE_CFGR0 = 0;
  SLAVE_CFGR1 = 0;
  
  SLAVE_CR |= LPSPI_CR_MEN; // | LPSPI_CR_DBGEN; /* Enable Module, Debug Mode */
  SLAVE_SR = 0x3F00; /* Clear status register */
  SLAVE_TCR_REFRESH;
  //SLAVE_CR = 0; /* Disable Module */
  //SLAVE_CR |= LPSPI_CR_MEN;
  
  SLAVE_TDR = 0x0; /* dummy data, must populate initial TX slot */
  NVIC_ENABLE_IRQ(nvic_irq);
  NVIC_SET_PRIORITY(nvic_irq, 1);
	
}
