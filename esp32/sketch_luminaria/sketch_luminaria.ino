#define TOUCH_SENSOR_PIN 35
#define IR_PIN 19

#include <BluetoothSerial.h>
#include <IRremote.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <Preferences.h>

char* cores[] = {"0xF720DF", "0xF710EF", "0xF710EF", "0xF708F7", "0xF708F7", "0xF7906F", "0xF7B04F", "0xF78877", "0xF7609F", "0xF750AF", "0xF7708F", "0xF748B7"};

const char* WHITE = "0xF7E01F";
const char* ON = "0xF7C03F";
const char* OFF = "0xF740BF";
const char* FLASH = "0xF7D02F";
const char* STROBE = "0xF7F00F";
const char* FADE = "0xF7C837";
const char* SMOOTH = "0xF7E817";

String ssid;
String password;

const char* server = "192.168.0.21";
const char* code = "2770265";

String token;

int relation = 0;
int corAtual = 0;
int incoming;
String message = "";

boolean deviceConfigurated = false;
boolean lampTurnedOn = true;

char relationArray[12];

int actionChangeColor = -1;

StaticJsonDocument<200> doc;
DeserializationError jsonError;

WiFiClient espClient;
PubSubClient client(espClient);
HTTPClient http;
IRsend irsend(IR_PIN);
BluetoothSerial ESP_BT;
Preferences preferences;

void setup() {
  Serial.begin(115200);
  pinMode(TOUCH_SENSOR_PIN, INPUT);
  pinMode(IR_PIN, OUTPUT);
  preferences.begin("lamps", false);
  //preferences.putString("token", "0");

  checkConfiguration();

  if(deviceConfigurated) {
    conectarWifi();
    client.setServer(server, 1883);
    client.setCallback(callback);
  }
  
  ESP_BT.begin("ESP32_Lamp_" + String(code));
}

void loop() {
  while(!deviceConfigurated) {
    if(ESP_BT.available()) {
      char incomingChar = ESP_BT.read();
      message += incomingChar;
      checkReceivedMessage();
    }
  }

  if(!deviceConfigurated) {
     conectarWifi();
     client.setServer(server, 1883);
     client.setCallback(callback);
     deviceConfigurated = true;
  } 
  
  if (relation == 0) {
    buscaRelacao();
  }  

  if (!client.connected() && relation != 0) {
    reconnectMQTT();
  }  

  if (digitalRead(TOUCH_SENSOR_PIN) == HIGH) {
    enviaCor();
  }

  if(ESP_BT.available()) {
      char incomingChar = ESP_BT.read();
      message += incomingChar;
      checkReceivedMessage();
      checkControlPanelAction();
  }

  client.loop();
}

void checkControlPanelAction() {
  Serial.println(message);
  if(message.indexOf("ONOFF") != -1) {
    if(lampTurnedOn) {
      for (int i = 0; i < 3; i++) {
        irsend.sendNEC(hstol(OFF), 32);
        delay(100);
      }
    } else {
      for (int i = 0; i < 3; i++) {
        irsend.sendNEC(hstol(ON), 32);
        delay(100);
      }
    }
    
    lampTurnedOn = !lampTurnedOn;
    message = "";  
  }

  if(message.indexOf("CHANGECOLOR") != -1) {
    actionChangeColor++;
    if(actionChangeColor >= 3) {
      actionChangeColor = 0;  
    }
    
    switch(actionChangeColor) {
      case 0:
        for (int i = 0; i < 3; i++) {
          irsend.sendNEC(hstol(WHITE), 32);
          delay(100);
        }
        break;
       case 1:
        for (int i = 0; i < 3; i++) {
          irsend.sendNEC(hstol(FADE), 32);
          delay(100);
        }
        break;
       case 2:
        generateRandomColor();
        for (int i = 0; i < 3; i++) {
          irsend.sendNEC(hstol(cores[corAtual]), 32);
          delay(100);
        }
        break;  
    };

    message = ""; 
  }
}

void checkConfiguration() {
  token = preferences.getString("token", "0");
  delay(500);
  ssid = preferences.getString("ssid", "0");
  delay(500);
  password = preferences.getString("password", "0");
  delay(500);

  Serial.println(token);
  Serial.println(ssid);
  Serial.println(password);
  
  if(token != "0" && ssid != "0" && password != "0") {
    deviceConfigurated = true;
    Serial.println("Variaveis de conexao configuradas");
  }  
}

void checkReceivedMessage() {
  int checkToken = message.indexOf("/token");
  int checkSsid = message.indexOf("/ssid");
  int checkPassword = message.indexOf("/password");
  
  if(checkToken != -1) {
    token = message.substring(0, checkToken);
    Serial.println(token);
    message = "";
    preferences.putString("token", token);
  }

  if(checkSsid != -1) {
    ssid = message.substring(0, checkSsid);
    Serial.println(ssid);
    message = "";
    preferences.putString("ssid", ssid);
  }

  if(checkPassword != -1) {
    password = message.substring(0, checkPassword);
    Serial.println(password);
    message = "";
    deviceConfigurated = true;
    preferences.putString("password", password);
  }      
}



void enviaCor() {
  generateRandomColor();

  client.publish(relationArray, cores[corAtual]);
  delay(1000);
}

int generateRandomColor() {
  int next = corAtual;
  while (corAtual == next) {
    next = random(0, 11);
  }

  corAtual = next;
}

void buscaRelacao() {
  String url = "http://" + String(server) + ":3000/relations";

  http.begin(url);
  http.addHeader("Authorization", "Bearer " + token);

  int httpCode = http.GET();
  String payload = http.getString();
  deserializeJson(doc, payload);
  if(httpCode == 200) {
    Serial.println("Relaçao configurada.");
    relation = doc["id"];
  }
  http.end();
  
}

void conectarWifi() {
  WiFi.begin(ssid.c_str(), password.c_str());

  Serial.print("Conectando ao WiFi.");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("Conectado ao WiFi.");
}


void procurarRedesWifi() {
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();

  int n = WiFi.scanNetworks();

  if (n == 0) {
    Serial.println("Nenhum WiFi encontrado");
  } else {
    Serial.print(n);
    Serial.println(" redes WiFi encontradas");
    for (int i = 0; i < n; ++i) {
      Serial.print(i + 1);
      Serial.print(": ");
      Serial.print(WiFi.SSID(i));
      Serial.print(" (");
      Serial.print(WiFi.RSSI(i));
      Serial.print(") ");
      Serial.print(" [");
      Serial.print(WiFi.channel(i));
      Serial.print("] ");
      delay(10);
    }
  }
}

void callback(char* topic, byte* message, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;

  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();

  int identifier = hstol(messageTemp);
  Serial.println(identifier);

  for (int i = 0; i < 3; i++) {
    irsend.sendNEC(identifier, 32);
    delay(100);
  }
}

long hstol(String recv) {
  return strtol(recv.c_str(), NULL, 16);
}

void reconnectMQTT() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect(code)) {
      Serial.println("connected");
      String relationCode = String("relation/") + String(relation);
      relationCode.toCharArray(relationArray, 12);
      client.subscribe(relationArray);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");

      delay(5000);
    }
  }
}
