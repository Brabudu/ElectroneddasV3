#ifndef info_h

#define info_h

// E [ELECTRONEDDAS]
const String info_E =
  "i                          //Info\n"
  "g [mode] [param]           //Gate mode 0=Nudda 1=Crais 2=Sulidu \n"
  "m [mode]                   //Monitor mode (FLAG: CRAIS 1- ESA   2-SUL   4- BT    8 - CLIP  16) \n"
  "s [file num]               //Salva \n"
  "n [nome]                   //Crea file e salva da console fino all'inserimento di @\n"
  "l [file num]               //Carriga file de su cuntzertu\n"
  "r [file num]               //Manda file de su cuntzertu in sa console\n"
  "f [nome]                   //Manda file in sa console\n"
  "t [file num]               //Ripristina file\n"
  "d                          //Lista files (array)\n"
  "D                          //Lista files\n"
  "x [nome]                   //Cancella file\n"
  "e [file num]               // Esegue file di script\n"
  "h                          //Reset controller\n"
  "c                          //Reset cuntzertu\n"
  "u                          //Agiornamentu display\n"
  "w [mode]                   //Modalidadi PC (0=off 1=on) \n"
  "p                          //Sarva preferentzias\n"
  "z                          //Carriga preferentzias\n"
  "B                          //Manda a BT\n"
  "Y                          //Torrat a principiu su stracasciu 0\n"
  "N                          //Noda de test 0\n"
  "I                          //Set ID\n"
  "R [p] {n}                  //Record (p=1 start now -p=2 start -p3=play {rate} -p=0 stop -s,l=save/load {nfile})\n";

const String info_B = "n [nome]                     //nomini de su cuncertu (max 32)\n"
                      "d [descrizione]              //Descritzioni de su cuncertu (max 64)\n"
                      "p [nota]                     //Puntu (0:20)\n"
                      "f [acc. fine]                //Acordadura fini (0.96:1.06)\n"
                      "c [cuntz] [modal]            //Cuntzertu (0-15) e modali (0:6)\n"
                      "a [num]                      //Acordadura\n"
                      "vc [volume]                  //Volume cuntzertu(0:2)\n"
                      "vt [volume] [bilanciamento]  //Volume e bilan. tumbu(0:2) (-1:1)\n"
                      "vs [volume] [bilanciamento]  //Volume e bilan. mancosa(0:2) (-1:1)\n"
                      "vd [volume] [bilanciamento]  //Volume e bilan. mancosedda(0:2) (-1:1)\n"

                      "s [lim] [span] [zero] //Sulidu\n"
                      "z [progs] [progz] //Sulidu prog\n"
                      "Z [JSON]          //Sulidu funtz\n"
                      "r [vol] [damp] [room]       //Riverbero (0:0.3) (0:1) (0:0.5)\n"
                      "F [BIQUAD]        //Filtru de bessida \n"
                      "Fp [MODO]         //Filtru de bessida 0=pranu, 1=pbasciu 2=pres\n"
                      "T [CANNA]\n"
                      "S [CANNA]\n"
                      "D [CANNA]\n"
                      "J //Serialize cuntzertu USB\n"
                      "P //Serialize prefs USB\n"
                      "B //Serialize Bluetooth\n";

const String info_C =
  "v [volArm]   //Volume statico-dinamico (0:2) \n"
  "c [sonu]                   //Tipo campione (0:6)\n"
  "s [strobbu] [portamentu]   //Disturbo e portamento (0:5) (0:5)\n"
  "p [crai]                   //Nota (0:4) o (0) \n"
  "t [timbru]                 //Modifica timbro (-10:10)\n"
  "m [chan] [vel] [transp] [mode] //MIDI \n"
  "o [obfactor duty] [obfactor vol] //Variazione tasto aperto (0.5:1) (1:1.5)\n"
  "Cx [CRAI]\n"
  "F [BIQUAD stat] \n"
  "D [BIQUAD dinF] \n";
#endif
