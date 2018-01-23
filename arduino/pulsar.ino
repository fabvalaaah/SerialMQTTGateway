#include <LiquidCrystal.h>

LiquidCrystal lcd(15, 0, 14, 4, 5, 6, 7); // LCD screen init

#define POT_PIN 3 // P1 (JMP7 --> B) on my board
#define POT_DELTA 701.0 // Potentiometer interval (here 0 to 700)
#define DELAY 500 // Delay between 2 measures (here 120 bpm)

unsigned long delta=0;

void setup() {
  Serial.begin(9600); // Preparing serial communication
  lcd.begin(16, 2); // Preparing the embedded LCD screen
}

void clearRow(int rowNumber) {
  if (rowNumber<0 || rowNumber>1)  {
    return;
  }

  lcd.setCursor(0, rowNumber);
  lcd.print("                 ");
}

void printRow(int rowNumber, char* string) {
  if (rowNumber<0 || rowNumber>1 || !string)  {
    return;
  }

  clearRow(rowNumber);
  lcd.setCursor(0, rowNumber);
  lcd.print(string);
}

void loop() {
  if (!delta || millis()-delta>DELAY) {
    int entier=analogRead(POT_PIN)*POT_DELTA/1024.0;
    char chaine[16];
    memset(chaine, 16, 1);
    sprintf(chaine, "%d", entier);
    printRow(0,chaine);
    Serial.println(entier); // Serial print
    delta=millis();
  }
}
