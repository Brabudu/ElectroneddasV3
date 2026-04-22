#pragma once
#include <Audio.h>

//#define SD_CONFIG SdioConfig(FIFO_SDIO)

#define CWARNING '?'
#define CERROR '!'
#define COK '#'

class Communicator {
private:
  Stream* stream;
  bool enabled;

public:
  Communicator(Stream* stream);

  void msgInfo(String msg);
  void msgError(String msg);
  void msgOk(String msg);
  void msgWarning(String msg, bool hiPriority);
  void msgWarning(String msg);
  void setWarningEnabled(bool);
};
