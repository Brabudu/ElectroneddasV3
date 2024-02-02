#include "ElFileSystem.h"
#include "Communicator.h"

extern Communicator* com;

extern void parse(String);

ElFileSystem::ElFileSystem() {
  if (!sd.begin(SD_CONFIG)) sd_ok=false;
  else sd_ok=true;
}

bool ElFileSystem::isMounted() {
  return sd_ok;
}
bool ElFileSystem::execute(int num) {
  if (!isMounted()) return false;
  
  String file=String(num)+".cmd";
  char filename[12];
  
  file.toCharArray(filename,12);

  FsFile myFile = sd.open(filename, FILE_READ);
  if (!myFile) {
    com->msgError("SD ex Error");
    return false;
  }
  myFile.seek(0);
 
  char riga[20];

  while (myFile.available()) {
    int i=0;
    while(myFile.available()&&(riga[i]=myFile.read())!='\n'){
      i++;
    }  
    if (i>2) parse(riga); 
  }
    com->msgOk("exec ok "+file);
    myFile.close();
    return true;
}


void ElFileSystem::listaFiles(bool readable) {
  if (!isMounted()) return;
  
  FsFile root=sd.open("/");

  com->setWarningEnabled(false);
  
  FsFile entry;
  char filename[12];
  String out="%";
  do {
    entry = root.openNextFile();
    if (!entry) break;
    if (!entry.isDirectory()) {
      entry.getName(filename,12);
     String n=String(filename);
     // if (n.substring(n.length()-4)==".JSO") {
        if (readable) {
          com->msgInfo(n);
        } else {
          out=out+"\""+n+"\",";
        }
      //}
    }
  } while (entry);
  out+="%";
  com->setWarningEnabled(true);
  
  if (!readable) com->msgWarning(out,true);
  
  
}
void ElFileSystem::deleteFile(String file) {
  if (!isMounted()) return;
  sd.remove(file);
  com->msgOk("ok del");
  }
  
bool ElFileSystem::copy(int numS, int numD) {
  if (!isMounted()) return false;
  if (sd.exists((String(numS)+".JSO"))) {
    FsFile myFileS = sd.open((String(numS)+".JSO"), FILE_READ);
    FsFile myFileD = sd.open((String(numD)+".JSO"), O_WRONLY | O_CREAT | O_TRUNC);
    if (!myFileS) {
      com->msgError("SD copy Error");
      return false;
    }
    size_t n;  
    uint8_t buf[64];
    while ((n = myFileS.read(buf, sizeof(buf))) > 0) {
      myFileD.write(buf, n);
    }
    myFileS.close();
    myFileD.close();
    com->msgOk("ok copy");
    return true;
  } else {
    com->msgError("No file");
    return false;
  }
  
}

void ElFileSystem::torra(int num) {
  if (!isMounted()) return;
  
  if (sd.exists((String(num)+".old"))) {
    sd.rename((String(num)+".old"),(String(num)+".JSO"));
    com->msgOk("ok reset");
  } else {
    com->msgError("No file");
  }
}

bool ElFileSystem::newFile(String file) { 
  if (!isMounted()) return false; 
  //cli();
  char filename[12];
  
  file.toCharArray(filename,12);
  
  FsFile myFile = sd.open(filename, O_WRONLY | O_CREAT | O_TRUNC);
  if (!myFile) {
    com->msgError("SD new Error");
    return false;
  }
  myFile.seek(0);
  bool stop=false;
  
  do 
  {
    if (Serial.available()>0) {
      char byteBuffer = Serial.read();
      if (byteBuffer=='@') {
        stop=true;
      } else {
        myFile.write(byteBuffer);
      }
    }
    //delay(100);
  } while (!stop);
  
  myFile.close();
  com->msgOk("ok "+file);
  while (Serial.available()>0) {
    Serial.read();
  }
 return true;
}

void ElFileSystem::cuntzToFile(Cuntzertu* c, int num) {
  if (!isMounted()) {  
    com->msgError("No SD");
    return;
  }
  
  cli();
  String file=String(num)+".JSO";
  char filename[12];
  
  file.toCharArray(filename,12);
  if (sd.exists(filename)) {
    sd.rename(filename,(String(num)+".old"));
  }
  FsFile myFile = sd.open(filename, O_WRONLY | O_CREAT | O_TRUNC);
  if (!myFile) {
    com->msgError("SD cw Error");
    return;
  }
  myFile.seek(0);
  
  c->serialize(&myFile);
  myFile.truncate();
  myFile.close();
  com->msgOk("ok "+file);
  sei();
  
}

bool ElFileSystem::cuntzFromFileJson(Cuntzertu* c, int num, bool load) {
  //cli();
  String file=String(num)+".JSO";
  return cuntzFromFile(c, file,load,0);
}
bool ElFileSystem::cuntzFromFileFunction(Cuntzertu* c, int num, bool load) {
  //cli();
  String file=String(num)+".FUN";
  return cuntzFromFile(c, file,load,'F');
}

bool ElFileSystem::cuntzFromFile(Cuntzertu* c, String file, bool load, char preamble) {
   if (!isMounted()) {  
    com->msgError("No SD");
    return false;
  }
  char filename[12];
  
  file.toCharArray(filename,12);
  
  FsFile myFile = sd.open(filename, FILE_READ);
  if (!myFile) {
    com->msgError("SD cr Error");
    return false;
  }
  
  myFile.seek(0);
  if (load) {
    c->mute();
    c->deserialize(&myFile);   
    c->sync();
    com->msgWarning("*"+file,true);
  }
  else {  //TODO
      com->setWarningEnabled(false);
      Serial.print('?');
      if (preamble!=0) Serial.print(preamble);
      while (myFile.available()) { //execute while file is available
        char letter = myFile.read(); //read next character from file
        Serial.print(letter); //display character
      }
      Serial.println();
      com->setWarningEnabled(true);
  }
  
  myFile.close();
  
  return true;
  //sei();
  
}

void ElFileSystem::savePrefs(Cuntzertu* c) {
  if (!isMounted()) {  
    com->msgError("No SD");
    return;
  }
  
  cli();
  String file="prefs.el";
  char filename[10];
  
  file.toCharArray(filename,10);
  
  FsFile myFile = sd.open(filename, O_WRONLY | O_CREAT | O_TRUNC);
  if (!myFile) {
    com->msgError("SD pw Err");
    return;
  }
  myFile.seek(0);
  
  c->writeJSONPrefs(&myFile);
  myFile.truncate();
  myFile.close();
  com->msgOk("ok "+file);
  sei();
}

bool ElFileSystem::readPrefs(Cuntzertu* c) {
   if (!isMounted()) {  
    com->msgError("No SD");
    return false;
  }
  String file="prefs.el";
  char filename[10];
  file.toCharArray(filename,10);
  
  FsFile myFile = sd.open(filename, FILE_READ);
  if (!myFile) {
    com->msgError("SD pr Err");
    return false;
  }
  myFile.seek(0);
  c->readJSONPrefs(&myFile);   
  
  myFile.close();
  
  return true;
  //sei();
  
}
