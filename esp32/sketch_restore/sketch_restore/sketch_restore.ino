#include <Preferences.h>

Preferences preferences;

void setup() {
  Serial.begin(115200);
  preferences.begin("lamps", false);
  Serial.println("Apagando variaveis armazenadas em Flash...");
  
  Serial.print("Token: ");
  preferences.putString("token", "");
  Serial.println("OK");

  Serial.print("SSID: ");
  preferences.putString("ssid", "");
  Serial.println("OK");

  Serial.print("Password: ");
  preferences.putString("password", "");
  Serial.println("OK");

  Serial.println("Processo realizado com sucesso.");
  
}

void loop() {}
