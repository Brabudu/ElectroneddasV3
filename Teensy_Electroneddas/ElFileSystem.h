#pragma once

#include "SdFat.h"
#include "Cannas.h"

#define SD_CONFIG SdioConfig(FIFO_SDIO)

class ElFileSystem {
  private:
    SdFs sd;
    bool sd_ok=false;
  public:
    ElFileSystem();
    
    bool isMounted();
    
    bool execute(int num);
    void deleteFile(String file);
    void torra(int num);

    void listaFiles(bool readable);
    bool newFile(String file);
    bool copy(int numS, int numD);


    void cuntzToFile(Cuntzertu* c, int num);
    bool cuntzFromFileJson(Cuntzertu* c, int num,bool sync);
    bool cuntzFromFileFunction(Cuntzertu* c, int num,bool sync);
    
    bool cuntzFromFile(Cuntzertu* c, String file,bool sync,char preamble);

    void savePrefs(Cuntzertu* c);
    bool readPrefs(Cuntzertu* c);
};
    
