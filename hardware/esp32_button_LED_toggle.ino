// constants won't change
#include <WiFi.h>
#include <FirebaseESP32.h>
  
#define FIREBASE_HOST "https://ezypark-49e23-default-rtdb.asia-southeast1.firebasedatabase.app/"
#define FIREBASE_AUTH "fUBL641MzuZrloGfWuQESgMctLKOUGefpK3XgZX8"
#define WIFI_SSID "SINGTEL-B990 (2.4GHz)"
#define WIFI_PASSWORD "zamohmoahi"

//Define FirebaseESP32 data object
FirebaseData firebaseData;
FirebaseJson lot1_json;
FirebaseJson lot2_json;
FirebaseJson lot3_json;
FirebaseJson lot4_json;
FirebaseJson lot5_json;
FirebaseJson waiting_cars_json;
FirebaseJson available_lots_json;
FirebaseJson total_avail_lots_json;

const int BUTTON_PIN_GANTRY = 21;
const int BUTTON_PIN_1 = 16; // ESP32 pin connected to button's pin
const int LED_PIN_1    = 18; // ESP32 pin connected to LED's pin
const int BUTTON_PIN_2 = 17;
const int LED_PIN_2    = 19;
const int BUTTON_PIN_3 = 14;
const int LED_PIN_3    = 13;
const int BUTTON_PIN_4 = 32;
const int LED_PIN_4    = 33;
const int BUTTON_PIN_5 = 25;
const int LED_PIN_5    = 26;

// variables will change:
int totalCarsInCarpark = 28;
int totalLots = 26;
int totalAvailableLots_B1=2;
int totalAvailableLots_B2=3;
int totalAvailableLots;
int carsWaiting;
int lastButtonGantryState;   
int currentButtonGantryState;
int led1State=HIGH;     // the current state of LED
int lastButton1State;    // the previous state of button
int currentButton1State; // the current state of button
int led2State=HIGH;     
int lastButton2State;   
int currentButton2State; 
int led3State=HIGH;     
int lastButton3State;   
int currentButton3State; 
int led4State=HIGH;     
int lastButton4State;   
int currentButton4State; 
int led5State=HIGH;     
int lastButton5State;   
int currentButton5State; 


void setup() {
  Serial.begin(115200);                // initialize serial
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);  // connect to wifi
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
 
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  Serial.println("------------------------------------");
  Serial.println("Connected...");
  
  pinMode(BUTTON_PIN_GANTRY, INPUT_PULLUP);
  pinMode(BUTTON_PIN_1, INPUT_PULLUP); // set ESP32 pin to input pull-up mode
  pinMode(LED_PIN_1, OUTPUT);          // set ESP32 pin to output mode
  pinMode(BUTTON_PIN_2, INPUT_PULLUP);
  pinMode(LED_PIN_2, OUTPUT);
  pinMode(BUTTON_PIN_3, INPUT_PULLUP);
  pinMode(LED_PIN_3, OUTPUT);
  pinMode(BUTTON_PIN_4, INPUT_PULLUP);
  pinMode(LED_PIN_4, OUTPUT);
  pinMode(BUTTON_PIN_5, INPUT_PULLUP);
  pinMode(LED_PIN_5, OUTPUT);

  currentButtonGantryState = digitalRead(BUTTON_PIN_GANTRY);
  currentButton1State = digitalRead(BUTTON_PIN_1);
  currentButton2State = digitalRead(BUTTON_PIN_2);
  currentButton3State = digitalRead(BUTTON_PIN_3);
  currentButton4State = digitalRead(BUTTON_PIN_4);
  currentButton5State = digitalRead(BUTTON_PIN_5);
}

void loop() {
//  Serial.print("Cars in carpark:");
//  Serial.println(totalCarsInCarpark);
  lastButtonGantryState    = currentButtonGantryState;
  currentButtonGantryState = digitalRead(BUTTON_PIN_GANTRY);
  lastButton1State    = currentButton1State;      // save the last state
  currentButton1State = digitalRead(BUTTON_PIN_1); // read new state
  lastButton2State    = currentButton2State;
  currentButton2State = digitalRead(BUTTON_PIN_2);
  lastButton3State    = currentButton3State;
  currentButton3State = digitalRead(BUTTON_PIN_3);
  lastButton4State    = currentButton4State;
  currentButton4State = digitalRead(BUTTON_PIN_4);
  lastButton5State    = currentButton5State;
  currentButton5State = digitalRead(BUTTON_PIN_5);

  if (carsWaiting<0) {
    carsWaiting=0;
  }
  
  if(lastButtonGantryState == LOW && currentButtonGantryState== HIGH) {
    Serial.println("Car has entered the carpark");

    // increase count
    totalCarsInCarpark += 1; 
    totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
    carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
    waiting_cars_json.set("/waiting_cars", carsWaiting);
    Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
    Serial.print("Cars waiting in carpark:");
    Serial.println(carsWaiting);
//    delay(500);
  }

  if(lastButton1State == LOW && currentButton1State == HIGH) {

    // toggle state of LED
    led1State = !led1State;
   

    // control LED arccoding to the toggled state
    digitalWrite(LED_PIN_1, led1State); 
    if(led1State == HIGH) {
      Serial.println("Car exited Lot 1");
      totalAvailableLots_B1 +=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      
      lot1_json.set("/lot1", true);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",lot1_json);

      available_lots_json.set("/available_lots", totalAvailableLots_B1);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      delay(500);
      totalCarsInCarpark -= 1;
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      Serial.println("Car exited the carpark");
    }
    else {
      Serial.println("Car entered Lot 1");
      totalAvailableLots_B1 -=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      lot1_json.set("/lot1", false);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",lot1_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B1);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      
    }
    Serial.print("Available Lots:");
    Serial.println(totalAvailableLots);
//    delay(500);
  }

  if(lastButton2State == LOW && currentButton2State == HIGH) {

    // toggle state of LED
    led2State = !led2State;
   

    // control LED arccoding to the toggled state
    digitalWrite(LED_PIN_2, led2State); 
    if(led2State == HIGH) {
      Serial.println("Car exited Lot 2");
      totalAvailableLots_B1 +=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      
      lot2_json.set("/lot2", true);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",lot2_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B1);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      delay(500);
      totalCarsInCarpark -= 1;
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      Serial.println("Car exited the carpark");
    }
    else {
      Serial.println("Car entered Lot 2");
      totalAvailableLots_B1 -=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      lot2_json.set("/lot2", false);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",lot2_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B1);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B1",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      
    }
    Serial.print("Available Lots:");
    Serial.println(totalAvailableLots);
  }

  if(lastButton3State == LOW && currentButton3State == HIGH) {

    // toggle state of LED
    led3State = !led3State;
   

    // control LED arccoding to the toggled state
    digitalWrite(LED_PIN_3, led3State); 
    if(led3State == HIGH) {
      Serial.println("Car exited Lot 3");
      totalAvailableLots_B2 +=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      
      lot3_json.set("/lot3", true);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",lot3_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B2);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      delay(500);
      totalCarsInCarpark -= 1;
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      Serial.println("Car exited the carpark");
    }
    else {
      Serial.println("Car entered Lot 3");
      totalAvailableLots_B2 -=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      lot3_json.set("/lot3", false);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",lot3_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B2);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      
    }
    Serial.print("Available Lots:");
    Serial.println(totalAvailableLots);
//    delay(500);
  }

  if(lastButton4State == LOW && currentButton4State == HIGH) {

    // toggle state of LED
    led4State = !led4State;
   

    // control LED arccoding to the toggled state
    digitalWrite(LED_PIN_4, led4State); 
    if(led4State == HIGH) {
      Serial.println("Car exited Lot 4");
      totalAvailableLots_B2 +=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      
      lot4_json.set("/lot4", true);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",lot4_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B2);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      delay(500);
      totalCarsInCarpark -= 1;
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      Serial.println("Car exited the carpark");
    }
    else {
      Serial.println("Car entered Lot 4");
      totalAvailableLots_B2 -=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      lot4_json.set("/lot4", false);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",lot4_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B2);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      
    }
    Serial.print("Available Lots:");
    Serial.println(totalAvailableLots);
//    delay(500);
  }

  if(lastButton5State == LOW && currentButton5State == HIGH) {

    // toggle state of LED
    led5State = !led5State;
   

    // control LED arccoding to the toggled state
    digitalWrite(LED_PIN_5, led5State); 
    if(led5State == HIGH) {
      Serial.println("Car exited Lot 5");
      totalAvailableLots_B2 +=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      
      lot5_json.set("/lot5", true);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",lot5_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B2);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      delay(500);
      totalCarsInCarpark -= 1;
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      Serial.println("Car exited the carpark");
    }
    else {
      Serial.println("Car entered Lot 5");
      totalAvailableLots_B2 -=1;
      totalAvailableLots = totalAvailableLots_B1 + totalAvailableLots_B2;
      carsWaiting = totalCarsInCarpark - totalLots + totalAvailableLots;
      lot5_json.set("/lot5", false);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",lot5_json);
      
      available_lots_json.set("/available_lots", totalAvailableLots_B2);
      Firebase.updateNode(firebaseData,"/carparks/Century Square/B2",available_lots_json);
      
      total_avail_lots_json.set("/total_avail_lots", totalAvailableLots);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",total_avail_lots_json);
      
      waiting_cars_json.set("/waiting_cars", carsWaiting);
      Firebase.updateNode(firebaseData,"/carparks/Century Square",waiting_cars_json);
      
    }
    Serial.print("Available Lots:");
    Serial.println(totalAvailableLots);
//    delay(500);
  }

  
//  if(lastButton2State == LOW && currentButton2State == HIGH) {
//
//    // toggle state of LED
//    led2State = !led2State;
//   
//
//    // control LED arccoding to the toggled state
//    digitalWrite(LED_PIN_2, led2State); 
//    if(led2State == HIGH) {
//      Serial.println("Car exited Lot 2");
//      totalOccupiedLots -=1;
//      delay(1000);
//      totalCarsInCarpark -= 1;
//      Serial.println("Car exited the carpark");
//    }
//    else {
//      Serial.println("Car entered Lot 2");
//      totalOccupiedLots +=1;
//    }
//    Serial.print("Occupied Lots:");
////  if(lastButton1State == LOW && currentButton1State == HIGH) {
////    totalOccupiedLots -=1;
////  }
//    Serial.println(totalOccupiedLots);
////    delay(500);
//  }
//
//  if(lastButton3State == LOW && currentButton3State == HIGH) {
//
//    // toggle state of LED
//    led3State = !led3State;
//   
//
//    // control LED arccoding to the toggled state
//    digitalWrite(LED_PIN_3, led3State); 
//    if(led3State == HIGH) {
//      Serial.println("Car exited Lot 3");
//      totalOccupiedLots -=1;
//      delay(1000);
//      totalCarsInCarpark -= 1;
//      Serial.println("Car exited the carpark");
//    }
//    else {
//      Serial.println("Car entered Lot 3");
//      totalOccupiedLots +=1;
//    }
//    Serial.print("Occupied Lots:");
//    Serial.println(totalOccupiedLots);
//  }
//
//  if(lastButton4State == LOW && currentButton4State == HIGH) {
//
//    // toggle state of LED
//    led4State = !led4State;
//   
//
//    // control LED arccoding to the toggled state
//    digitalWrite(LED_PIN_4, led4State); 
//    if(led4State == HIGH) {
//      Serial.println("Car exited Lot 4");
//      totalOccupiedLots -=1;
//      delay(1000);
//      totalCarsInCarpark -= 1;
//      Serial.println("Car exited the carpark");
//    }
//    else {
//      Serial.println("Car entered Lot 4");
//      totalOccupiedLots +=1;
//    }
//    Serial.print("Occupied Lots:");
//    Serial.println(totalOccupiedLots);
//  }
//
//  if(lastButton5State == LOW && currentButton5State == HIGH) {
//
//    // toggle state of LED
//    led5State = !led5State;
//   
//
//    // control LED arccoding to the toggled state
//    digitalWrite(LED_PIN_5, led5State); 
//    if(led5State == HIGH) {
//      Serial.println("Car exited Lot 5");
//      totalOccupiedLots -=1;
//      delay(1000);
//      totalCarsInCarpark -= 1;
//      Serial.println("Car exited the carpark");
//    }
//    else {
//      Serial.println("Car entered Lot 5");
//      totalOccupiedLots +=1;
//    }
//    Serial.print("Occupied Lots:");
//    Serial.println(totalOccupiedLots);
//  }
  
}

//  if(lastButton2State == LOW && currentButton2State == HIGH) {
//    totalOccupiedLots -=1;
//  }
// }
