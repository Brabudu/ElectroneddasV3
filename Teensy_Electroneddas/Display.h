#pragma once

#include <GyverOLED.h>
#include "Cannas.h"
#include <Encoder.h>


#define MENU_SELECTED true
#define MENU_UNSELECTED false

#define MENU_DRAW true
#define MENU_UNDRAW false

#define PIN_ENC1 31
#define PIN_ENC2 32

#define PIN_PULS_OK 33
#define PIN_PULS_CANC 30

class Display {
  private:
    GyverOLED<SSH1106_128x64> oled;
    
    
    
    int pos=0;    
    uint8_t page=0;

    int sens=0; //TEMP!

    uint8_t filt_num=0;

    int level=0;

    bool enabled=true;
    bool blanked=false;
    
    
    const String notazE[12]  = {"DO  ","DO# ","RE  ","RE# ","MI  ","FA  ","FA# ","SOL ","SOL#","LA  ","SIb ","SI  "};
    const String cuntzertus[16]  = {"Fior","P.O.","Med ","MedP","Fiu ","Spin","SpiP","MedF","Samp", "Mori","P&M ","Fdda","    ","    ","    ","Pers"};
    const String modal[7] ={" I ","II ","III","IV "," V ","VI ","VII"};
   // const String filtrus[3] ={"Pranu ","Cufias","Presen"};
    const String gate[5]={"No","Cr","Su","AS","CS"};
    const String sulP[6]={"P0","P1","P2","P3","P4","P5"};
    const String sulS[3]={"SA","SM","SB"};

    void static pollEncoder();
    int toBar(float volt);
    
  public:
	  Display();
    void left();
    void right();
    bool go(bool ok,int dir,bool longPress);

      
    void move(int inc);
    
    void setEnabled(bool enabled);
    bool isEnabled();
    bool isBlanked();
    
    void showLogo(String v);
    void drawMenu();
    void posMenu(uint8_t pos, bool on, bool selected);
    void testMenu(uint8_t page);
    void cursMenu(uint8_t pos, uint8_t page, bool on, bool in);
    void drawKnob(uint8_t x, uint8_t y, float minv, float maxv, float value,String label,bool v);
    void drawBattery(uint8_t x, uint8_t y);
    void displayPage();
    void initMenu();
    void update();
    void test();
    void cmdMode(bool mode);
    void clearRect(int x0, int y0, int x1, int y1);
    void refreshBattery();

    void blankPage();
};
