#include "Display.h"

#include "bitmap.h"
#include "ElFileSystem.h"

const uint8_t menu[5][10][5] PROGMEM = {
  { // page 0
    { 0, 0, 127, 11, 1 },
    { 6, 22, 69, 41, 1 },
    { 78, 18, 101, 48, 1 },
    { 8, 50, 36, 60, 1 },
    { 38, 50, 60, 60, 1 },
    { 61, 50, 82, 60, 1 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 } },
  { // page 1
    { 35, 9, 58, 40, 1 },
    { 59, 9, 80, 40, 1 },
    { 83, 9, 104, 40, 1 },
    { 35, 42, 58, 63, 1 },
    { 59, 42, 80, 63, 1 },
    { 83, 42, 104, 63, 1 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 } },
  { // page 2
    { 5, 4, 26, 35, 1 },
    { 35, 4, 58, 35, 1 },
    { 59, 4, 80, 35, 1 },
    { 83, 4, 104, 35, 1 },
    { 4, 49, 19, 63, 1 },
    { 22, 49, 41, 63, 1 },
    { 46, 49, 65, 63, 1 },
    { 68, 49, 87, 63, 1 },
    { 90, 49, 109, 63, 1 },
    { 0, 0, 0, 0, 0 } },
  { // page 3
    { 0, 0, 101, 13, 1 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 } },
  { // page 4
    { 0, 0, 101, 13, 1 },
    { 0, 15, 101, 28, 1 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 },
    { 0, 0, 0, 0, 0 } }

};


#define POS_MENU 10
#define PAGES 3
#define MAX_CUNTZ_NUM 200


#define BASE 0
#define ENTERED 1
#define SELECTED 2

extern ElFileSystem* efs;
extern Display* d;
extern Cuntzertu* c;

extern uint8_t cuntz_num;

extern void resetController();
extern float getCharge();

extern String command;

extern uint8_t last_error_mem;

//Encoder
long enc_pos;
long enc_old_pos;

int enc_count;
int enc_puls_count = 0;

Encoder myEnc(PIN_ENC1, PIN_ENC2);
IntervalTimer encoderTimer;

Display::Display() {
  //Init encoder

  pinMode(PIN_ENC1, INPUT_PULLUP);
  pinMode(PIN_ENC2, INPUT_PULLUP);

  enc_pos = 0;
  enc_old_pos = 0;
  enc_count = 0;

  encoderTimer.begin(pollEncoder, 10000);
  encoderTimer.priority(255);

  oled.init();
  oled.clear();
}

void Display::showLogo(String vers) {
  oled.home();
  oled.print(" :: ELECTRONEDDAS ::");

  oled.setCursor(110, 7);
  oled.print(vers);

  oled.drawBitmap(40, 12, el_bitmap_48x48, 48, 48, BITMAP_NORMAL, BUF_ADD);

  oled.update();
}
void Display::move(int inc) {
  cursMenu(pos, page, MENU_UNDRAW, MENU_UNSELECTED);
  do {
    pos += inc;
    if (pos >= POS_MENU) pos = 0;
    if (pos < 0) pos = POS_MENU - 1;

  } while (((pos < POS_MENU) && (menu[page][pos][4] == 0)) || (pos == POS_MENU));

  cursMenu(pos, page, MENU_DRAW, MENU_UNSELECTED);
  oled.update();
}
void Display::right() {
  go(true, -1, false);
}
void Display::left() {
  go(true, 1, false);
}

bool Display::go(bool ok, int dir, bool longPress) {
  if (isBlanked()) {
    oled.clear();
    drawMenu();
    blanked = false;
    pos = 0;
    level = 0;
    if (dir == 0) displayPage();
  }

  switch (level) {
    case BASE:
      //Click ok
      if (ok && (dir == 0)) {
        if (longPress) {  //Service page
          page = 4;
          oled.clear();
          displayPage();
        }
        level = ENTERED;
        cursMenu(pos, page, MENU_DRAW, MENU_UNSELECTED);
        oled.update();
      }
      //Click canc
      if (!ok) {
        initMenu();
        return false;
      }
      if (dir != 0) {
        page += dir;
        if (page < 0) page = PAGES;
        page %= PAGES;

        oled.rect(0, 0, 112, 63, OLED_CLEAR);
        displayPage();
        pos = 0;

        oled.update();
        return true;
      }
      break;
    case ENTERED:
      //Click canc
      if (!ok) {
        level = BASE;
        cursMenu(pos, page, MENU_UNDRAW, MENU_UNSELECTED);
        oled.update();
      }
      //Click ok
      if (ok && (dir == 0)) {
        if (pos >= POS_MENU) {
          page = pos - POS_MENU;

          cursMenu(pos, page, MENU_DRAW, MENU_SELECTED);
          oled.rect(0, 0, 112, 63, OLED_CLEAR);
          displayPage();
          pos = 0;

          cursMenu(pos, page, MENU_DRAW, MENU_UNSELECTED);
          oled.update();
          return true;
        } else {

          if ((page == 2) && (pos == 8)) {  //Conferma

            if (longPress) {
              efs->savePrefs(c);
              cursMenu(pos, page, MENU_DRAW, MENU_SELECTED);
              oled.update();
            }

          } else if ((page == 0) && (pos == 5)) {  //Conferma

            if (longPress) {
              efs->cuntzToFile(c, cuntz_num);
              cursMenu(pos, page, MENU_DRAW, MENU_SELECTED);
              oled.update();
            }

          } else {
            level = SELECTED;
            cursMenu(pos, page, MENU_DRAW, MENU_SELECTED);
            oled.update();
          }
        }
      }
      //Rotary
      if (dir != 0) {
        move(dir);
      }

      break;
    case SELECTED:
      //click
      if ((dir == 0)) {
        level = ENTERED;
        cursMenu(pos, page, MENU_UNDRAW, MENU_UNSELECTED);
        cursMenu(pos, page, MENU_DRAW, MENU_UNSELECTED);
        oled.update();
        return true;
      } else {

        cursMenu(pos, page, MENU_DRAW, MENU_SELECTED);
        switch (page) {
          case 0:
            {
              switch (pos) {
                case 0:  //nome
                  {
                    if (!efs->isMounted()) break;
                    do {
                      cuntz_num = (cuntz_num + dir + MAX_CUNTZ_NUM) % MAX_CUNTZ_NUM;
                    } while (!efs->cuntzFromFileJson(c, cuntz_num, true));
                    c->setPreferredCuntz(cuntz_num);
                    displayPage();
                    cursMenu(0, 0, MENU_DRAW, MENU_SELECTED);
                  }
                  break;
                case 1:  //crai
                  c->setPuntu(c->getPuntu() + dir);
                  displayPage();

                  break;
                case 2:  //fini
                  c->setFini(c->getFini() + 0.005 * dir);
                  displayPage();

                  break;
                case 3:  //cuntzertu
                  c->setCuntz(c->getCuntz() + dir);
                  displayPage();

                  break;
                case 4:  //cuntzertu
                  c->setModal(c->getModal() + dir);
                  displayPage();

                  break;
              }

              oled.update();
            }
            break;
          case 1:
            {
              switch (pos) {

                case 0:  //vol t
                  c->setVolBilT(c->getVolT() + 0.1 * dir, c->getBilT());
                  break;
                case 1:  //volMs
                  c->setVolBilMs(c->getVolMs() + 0.1 * dir, c->getBilMs());
                  break;
                case 2:  //volMd
                  c->setVolBilMd(c->getVolMd() + 0.1 * dir, c->getBilMd());
                  break;
                case 3:  //bil t
                  c->setVolBilT(c->getVolT(), c->getBilT() + 0.1 * dir);
                  break;
                case 4:  //volMs
                  c->setVolBilMs(c->getVolMs(), c->getBilMs() + 0.1 * dir);
                  break;
                case 5:  //volMd
                  c->setVolBilMd(c->getVolMd(), c->getBilMd() + 0.1 * dir);
                  break;
              }
              displayPage();
              oled.update();
            }
            break;
          case 2:
            {
              switch (pos) {
                case 0:  //vol
                  c->setVol(c->getVol() + 0.1 * dir);
                  break;
                case 1:  //liv
                  c->setVerb(c->getVerbVol() + 0.05 * dir, c->getVerbDamp(), c->getVerbRoom());
                  break;
                case 2:  //damp
                  c->setVerb(c->getVerbVol(), c->getVerbDamp() + 0.05 * dir, c->getVerbRoom());
                  break;
                case 3:  //room
                  c->setVerb(c->getVerbVol(), c->getVerbDamp(), c->getVerbRoom() + 0.05 * dir);
                  break;
                case 4:  //filtru
                  c->setFilterMode((byte)(c->getFilterMode() + dir) % 3);
                  displayPage();
                  break;
                case 5:  //gate
                  c->setGateMode((byte)(c->getGateMode() + dir) % GATEMODEMAX);
                  displayPage();
                  break;
                case 6:  //zero
                  c->setSulProgZ((byte)(c->getSulProgZ() + dir) % 6);
                  c->setSulProgS(c->getSulProgS());
                  displayPage();
                  break;
                case 7:  //sensibilidadi
                  c->setSulProgS((byte)(c->getSulProgS() + dir) % 3);
                  displayPage();
                  break;
                case 8:

                  break;
              }
              displayPage();
              oled.update();
            }
            break;
          case 4:
            {
              switch (pos) {
                case 0:  //calib
                  oled.setCursorXY(2, 2);
                  oled.print("Calib error: ");
                  oled.print(last_error_mem);
                  break;
                case 1:  //test

                  command = "E N";

                  break;
              }
              displayPage();
              oled.update();
            }
        }
      }
  }
  return true;
}
void Display::initMenu() {
  page = 0;
  pos = 0;
  oled.clear();
  drawMenu();
  cursMenu(0, 0, MENU_UNDRAW, MENU_SELECTED);
  displayPage();

  oled.update();
}

void Display::drawMenu() {
  //oled.fastLineV(112, 14, 63);
  oled.setCursorXY(122, 14);
  oled.print("1");
  oled.setCursorXY(122, 28);
  oled.print("2");
  oled.setCursorXY(122, 42);
  oled.print("3");
  oled.setCursorXY(122, 56);
  oled.print("4");
}

void Display::testMenu(uint8_t p) {
  //oled.clear();
  for (int i = 0; i < 7; i++) {

    oled.rect(menu[p][i][0], menu[p][i][1], menu[p][i][2], menu[p][i][3], OLED_STROKE);
  }
}

void Display::cursMenu(uint8_t pos, uint8_t p, bool on, bool in) {

  if (on) {
    if (in) oled.rect(menu[p][pos][0], menu[p][pos][1], menu[p][pos][2], menu[p][pos][3], OLED_STROKE);
    else {
      oled.fastLineH(menu[p][pos][3], menu[p][pos][0], menu[p][pos][2]);
      oled.fastLineH(menu[p][pos][1], menu[p][pos][0], menu[p][pos][2]);
    }
  } else clearRect(menu[p][pos][0], menu[p][pos][1], menu[p][pos][2], menu[p][pos][3]);
}
void Display::displayPage() {
  //cli();
  //oled.clear();
  switch (page) {
    case 0:
      {

        oled.rect(0, 0, 127, 9, OLED_CLEAR);

        //oled.rect(0,12,112,63,OLED_CLEAR);

        oled.setCursorXY(2, 2);
        oled.textMode(BUF_ADD);
        oled.print(cuntz_num);
        oled.setCursorXY(20, 2);
        oled.print(c->getNome());

        oled.textMode(BUF_REPLACE);

        oled.setCursorXY(10, 52);
        oled.print(cuntzertus[c->getCuntz()]);


        oled.setCursorXY(40, 52);
        oled.print(modal[c->getModal()]);


        oled.setCursorXY(8, 24);
        oled.setScale(2);
        oled.invertText(true);
        oled.print(notazE[(c->getPuntu() + 12) % 12]);
        oled.print((int)(c->getPuntu() + 12) / 12);
        oled.invertText(false);
        oled.setScale(1);

        drawKnob(90, 38, 94, 106, c->getFini() * 100, "fin", false);
        oled.setCursorXY(64, 52);
        oled.print("Mem");
        drawBattery(94, 54);
      }
      break;
    case 1:
      {
        //drawKnob(16,30,0,20,c->getVol()*10,"vol",false);
        oled.setCursorXY(10, 50);
        oled.print("Bal");
        oled.setCursorXY(10, 32);
        oled.print("Vol");

        drawKnob(46, 30, 0, 20, c->getVolT() * 10, "tum", false);
        drawKnob(70, 30, 0, 20, c->getVolMs() * 10, "msa", false);
        drawKnob(94, 30, 0, 20, c->getVolMd() * 10, "mda", false);
        drawKnob(46, 54, -10, 10, c->getBilT() * 10, "", false);
        drawKnob(70, 54, -10, 10, c->getBilMs() * 10, "", false);
        drawKnob(94, 54, -10, 10, c->getBilMd() * 10, "", false);
      }
      break;
    case 2:
      {
        drawKnob(16, 25, 0, 20, c->getVol() * 10, "vol", false);
        drawKnob(46, 25, 0, 3, c->getVerbVol() * 10, "Liv", false);
        drawKnob(70, 25, 0, 10, c->getVerbDamp() * 10, "Amm", false);
        drawKnob(94, 25, 0, 5, c->getVerbRoom() * 10, "Apo", false);

        switch (c->getFilterMode()) {
          case 0:
            oled.drawBitmap(8, 54, allpass_8x4, 8, 4, BITMAP_NORMAL, BUF_REPLACE);
            break;
          case 1:
            oled.drawBitmap(8, 54, lopass_8x4, 8, 4, BITMAP_NORMAL, BUF_REPLACE);
            break;
          case 2:
            oled.drawBitmap(8, 54, hipass_8x4, 8, 4, BITMAP_NORMAL, BUF_REPLACE);
            break;
          default:
            oled.drawBitmap(8, 54, sd_bitmap_16x11, 8, 4, BITMAP_NORMAL, BUF_REPLACE);
        }

        oled.roundRect(6, 51, 17, 61, OLED_STROKE);
        oled.textMode(BUF_REPLACE);

        oled.setCursorXY(26, 54);
        oled.print(gate[c->getGateMode()]);
        oled.roundRect(24, 51, 39, 61, OLED_STROKE);

        oled.setCursorXY(50, 54);
        oled.print(sulP[c->getSulProgZ()]);
        oled.roundRect(48, 51, 63, 61, OLED_STROKE);

        oled.setCursorXY(72, 54);
        oled.print(sulS[c->getSulProgS()]);
        oled.roundRect(70, 51, 85, 61, OLED_STROKE);

        oled.setCursorXY(92, 54);
        oled.print("Mem");
      }
      break;
    case 4:
      {
        oled.setCursorXY(2, 2);
        oled.print("Calib error: ");
        oled.print(last_error_mem);
        oled.setCursorXY(2, 15);
        oled.print("Test sonu ");
      }
      break;
  }

  oled.rect(114, 12, 121, 63, OLED_CLEAR);
  oled.setCursorXY(114, 14 + page * 14);
  oled.print(">");


  //sei();
}

void Display::blankPage() {
  //oled.rect(0,0,127,9,OLED_CLEAR);
  oled.clear();
  oled.setCursorXY(2, 2);
  oled.textMode(BUF_ADD);
  oled.print(cuntz_num);
  oled.setCursorXY(20, 2);
  oled.print(c->getNome());
  oled.update();
  blanked = true;
}

void Display::drawKnob(uint8_t x, uint8_t y, float minv, float maxv, float value, String label, bool v) {
  oled.rect(x - 6, y - 6, x + 6, y + 6, OLED_CLEAR);
  float pos = (value - minv) / (maxv - minv);
  //315 gradi di escursione
  float alfa = 1.9625 + 5.5 * pos;
  oled.circle(x, y, 6, OLED_STROKE);
  oled.line(x, y, x + 6 * cos(alfa), y + 6 * sin(alfa));
  if (v) {
    oled.setCursorXY(x - 4, y + 8);
    oled.print((int)value);
  }
  oled.setCursorXY(x - 8, y - 16);
  oled.textMode(BUF_ADD);
  oled.print(label);
  oled.textMode(BUF_REPLACE);
}
void Display::drawBattery(uint8_t x, uint8_t y) {
  oled.rect(x, y, x + 13, y + 6, OLED_STROKE);
  oled.rect(x + 2, y + 2, x + toBar(getCharge()), y + 4, OLED_FILL);
}
void Display::refreshBattery() {
  if (page == 0) drawBattery(94, 54);
}
int Display::toBar(float v) {
  if (v > 3.9) return 9;
  if (v > 3.8) return 8;
  if (v > 3.7) return 7;
  if (v < 3.5) return 0;
  if (v < 3.6) return 1;
  return 2 + int((v - 3.6) * 50);
}


void Display::test() {
  oled.rect(0, 0, 127, 63, OLED_STROKE);
}
void Display::clearRect(int x0, int y0, int x1, int y1) {
  oled.fastLineH(y0, x0 + 1, x1 - 1, 0);
  oled.fastLineH(y1, x0 + 1, x1 - 1, 0);
  oled.fastLineV(x0, y0, y1, 0);
  oled.fastLineV(x1, y0, y1, 0);
}
void Display::update() {
  oled.update();
}

void Display::setEnabled(bool enabled) {
  if (enabled) {
    initMenu();
    this->enabled = true;
  } else {
    this->enabled = false;
    oled.clear();
    page = 0;
    displayPage();
    update();
  }
}
bool Display::isEnabled() {
  return enabled;
}
bool Display::isBlanked() {
  return blanked;
}

void Display::pollEncoder() {

  if (!d->isEnabled()) return;

  enc_pos = myEnc.read();

  if (digitalRead(PIN_PULS_OK) == false) {
    enc_puls_count = 0;
    while ((digitalRead(PIN_PULS_OK) == false) && (enc_puls_count < 100)) {
      enc_puls_count++;
      delay(10);
    }

    d->go(true, 0, (enc_puls_count == 100));
    delay(100);
    enc_count = 0;
  }
  if (digitalRead(PIN_PULS_CANC) == false) {
    if (!d->go(false, 0, false)) resetController();
    while (digitalRead(PIN_PULS_CANC) == false)
      ;
    delay(100);
    enc_count = 0;
  }
  if (enc_pos - enc_old_pos > 3) {
    d->left();
    enc_old_pos = enc_pos;
    enc_count = 0;
  }
  if (enc_pos - enc_old_pos < -3) {
    d->right();
    enc_old_pos = enc_pos;
    enc_count = 0;
  }

  if ((enc_count > 2000) && (!d->isBlanked())) {
    //d->go(false,0,false);
    d->blankPage();
    enc_count = 0;
  }
  enc_count++;
}
