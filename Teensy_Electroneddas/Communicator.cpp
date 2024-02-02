#include "Communicator.h"


Communicator::Communicator(Stream* stream) {
  this->stream=stream;
  enabled=true;
}

void Communicator::setWarningEnabled(bool enabled) {
  this->enabled=enabled;
}

void Communicator::msgInfo(String msg){
  stream->println(msg);
  };
void Communicator::msgError(String msg){
  stream->print(CERROR);
  stream->println(msg);
  };
void Communicator::msgOk(String msg){
  stream->print(COK);
  stream->println(msg);
  };
void Communicator::msgWarning(String msg, bool hiPriority){
  if (enabled) {
    stream->print(CWARNING);
    stream->println(msg);
    if (hiPriority) stream->flush();
  }
  };
