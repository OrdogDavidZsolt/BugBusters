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
#include <MFRC522.h>


// ------------- Defines -------------
#define SERVER_NAME "bence-Mint"
#define SERVER_PORT 54321

// Pheriferias
#define SPI_SCK  4
#define SPI_MISO 3
#define SPI_MOSI 2
#define W5500_CS  5
#define W5500_RST 6
#define MFRC_CS  7
#define MFRC_RST 8
#define LED_R 10
#define LED_G 20
#define LED_B 21

// Mdns
#define MDNS_PORT 5353
#define MDNS_MULTICAST_IP IPAddress(224, 0, 0, 251)
#define MDNS_TIMEOUT 2000  // ms


// ------------ Globals -------------
byte mac[6]; // MAC address of W5500, walue from ESP's MAC
EthernetClient client;
IPAddress serverIP(172, 22, 225, 174);    // ez kell megoldani névfeloldással
MFRC522 mfrc(MFRC_CS, MFRC_RST);
String uidStr;

void setup()
{
    Serial.begin(115200);
    while (!Serial)
    {
        ;
    }
    SPI.begin(SPI_SCK, SPI_MISO, SPI_MOSI);

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

    Ethernet.init(W5500_CS);
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
    
    mfrc.PCD_Init();

}

void loop()
{
    uidStr = readUID();
    if (uidStr != "")
    {
        sendUIDToServer(serverIP, uidStr);
    }
}

// Card Reader
String readUID()
{
    // Check for new card 
    if ( ! mfrc.PICC_IsNewCardPresent()) return "";
    if ( ! mfrc.PICC_ReadCardSerial()) return "";

    // Read UID from RFID tag
    String uidStr = "";
    for (byte i = 0; i < mfrc.uid.size; i++) {
        if (uidStr.length()) uidStr += ":";
        if (mfrc.uid.uidByte[i] < 0x10) uidStr += "0";
        uidStr += String(mfrc.uid.uidByte[i], HEX);
    }
    uidStr.toUpperCase();

    // Debug Message
    Serial.print("Tag UID: ");
    Serial.println(uidStr);

    return uidStr;
}

bool sendUIDToServer(IPAddress serverIp, String uidStr)
{
    // Check connection and reconnect if nececerry
    if (!client.connected()) {
        client.stop();
        Serial.print("Connect to: ");
        Serial.print(serverIp);
        Serial.print(":");
        Serial.println(SERVER_PORT);
        if (!client.connect(serverIp, SERVER_PORT)) {
            Serial.println("Server connection request failed");
            return false; // Cannot send data
        }
    }

    // Data format: "UID=AA:BB:CC\n"
    String outMsg = "UID=" + uidStr + "\n";
    client.print(outMsg);
    Serial.print("Sent: "); Serial.println(outMsg);
}
