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


// ------------ Globals -------------
byte mac[6]; // MAC address of W5500, walue from ESP's MAC
 
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
        delay(1000);    // Try again after 1 sec
    }
    Serial.println("DHCP configured:");  // Debug msg
    Serial.print("Assigned IP: "); Serial.println(Ethernet.localIP()); // IP address from DHCP
    
}

void loop()
{

}
