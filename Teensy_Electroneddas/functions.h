#ifndef functions_h

#define functions_h

/// J. Piquard ///
inline int StringSplit(String sInput, char cDelim, String sParams[], int iMaxParams)
{
    int iParamCount = 0;
    int iPosDelim, iPosStart = 0;
    sInput.trim();
    
    do {
        // Searching the delimiter using indexOf()
        iPosDelim = sInput.indexOf(cDelim,iPosStart);
       
        if (iPosDelim > (iPosStart)) {
            // Adding a new parameter using substring() 
            sParams[iParamCount] = sInput.substring(iPosStart,iPosDelim);
          
            iParamCount++;
            // Checking the number of parameters
            if (iParamCount >= iMaxParams) {           
                return (iParamCount);
            }
            iPosStart = iPosDelim + 1;
        }
    } while (iPosDelim >= 0);
    if (iParamCount < iMaxParams) {
        // Adding the last parameter as the end of the line
        sParams[iParamCount] = sInput.substring(iPosStart,sInput.length());
        
        iParamCount++;
    }

    return (iParamCount);
}

inline  void StringSplitFirst(String *sInput, char cDelim, String *first)
{
  
    
    byte iPosDelim=0;
  
    iPosDelim = sInput->indexOf(cDelim,0);
   
    if ((iPosDelim > 0)&&(iPosDelim!=255)) {
        *first = sInput->substring(0,iPosDelim);
        *sInput= sInput->substring(iPosDelim+1,sInput->length());
    }  else {
      *first=*sInput;
    }
}

inline  void led(float t, byte v) {
  for (int i = 0; i < v; i++) {
    digitalWrite(LED_BUILTIN, HIGH);
    delay (t * 1000);
    digitalWrite(LED_BUILTIN, LOW);
    delay (t * 1000);
  }
}
#endif
