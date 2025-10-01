/* This is the code for the ESP32-C3 based card reader
 *
 * MCU Type         : ESP32-C3 (single core, RISC-V)
 * RFID reader      : MFRC-522 (SPI)
 * Ethernet module  : W5500 based (SPI)
 */

// ------------- Includes -------------
#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <ESPmDNS.h>


// ------------- Defines -------------
#define SERVER_NAME "dataserver.local"
#define SERVER_PORT 54321


// ------------ Globals -------------
byte mac[6]; // MAC address of W5500, walue from ESP's MAC
EthernetClient client;
 
void setup()
{
    Serial.begin(115200);

    uint64_t chipMac = ESP.getEfuseMac(); // ESP's factory MAC, 48 bits
    mac[0] = (chipMac >> 40) & 0xFF;
    mac[1] = (chipMac >> 32) & 0xFF;
    mac[2] = (chipMac >> 24) & 0xFF;
    mac[3] = (chipMac >> 16) & 0xFF;
    mac[4] = (chipMac >> 8 ) & 0xFF;
    mac[5] =  chipMac & 0xFF;

    // DEBUG {
    Serial.print("ESP mac: ");
    for (int i = 0; i < 6; i++)
    {
        Serial.print(mac[i], HEX);
    }
    Serial.println();
    // DEBUG }

    // Initialize W5500 via DHCP
    while (Ethernet.begin(mac) == 0)
    {
        Serial.println("DHCP failed"); // Error msg
        delay(1000); // Try again after 1 sec
    }
    Serial.println("DHCP configured");  // Debug msg

    // Network infromations for debug
    Serial.print("Assigned IP: "); Serial.println(Ethernet.localIP());    // IP address from DHCP
    Serial.print("Subnet mask: "); Serial.println(Ethernet.subnetMask()); // Netmask from DHCP
    Serial.print("Gateway IP:  "); Serial.println(Ethernet.gatewayIP());  // Gateway from DHCP
    
    // mDNS configuration
    while (!MDNS.begin("esp32")) // Start mDNS client
    {
        Serial.println("mDNS failed");
        delay(1000); // Try again after 1 sec
    }
    Serial.println("mDNS started");

    // Find mDNS server and get IP
    IPAddress serverIP;
    while (!MDNS.queryHost(SERVER_NAME, serverIP))
    {
        Serial.println("Cannot resolve mDNS server name");
        delay(1000); // Try again after 1 sec
    }
    Serial.println("mDNS resolved");  //Debug message

    // mDNS server information
    Serial.print("Resolved: "); Serial.print(SERVER_NAME); Serial.print(" -> ");
    Serial.println(serverIP);
    

    
}

void loop()
{

}
