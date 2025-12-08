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
// Serial
#define SERIAL_SPEED 115200

// Pheriferias
#define SPI_SCK 4
#define SPI_MISO 3
#define SPI_MOSI 2
#define W5500_CS 5
#define W5500_RST 6
#define MFRC_CS 7
#define MFRC_RST 8
#define LED_R 10
#define LED_G 20
#define LED_B 21
#define LED_DELAY 1000

// Server
#define SERVER_DATA_PORT 54321
#define SERVER_HEARTBEAT_PORT 54322
#define SERVER_NAME "bence-Mint.local"

// mDNS
#define MDNS_PORT 5353
#define MDNS_MULTICAST_IP IPAddress(224, 0, 0, 251)
#define MDNS_TIMEOUT 2000  // ms

// RFID related
#define UID_BYTE_SIZE 10

// Status related
#define HEARTBEAT_INTERVAL 5000


// ------------ Globals -------------
byte mac[6];                            // MAC address of W5500, walue from ESP's MAC
EthernetClient client;                  // Local client
EthernetUDP Udp;                        // UDP client for mDNS resolve
IPAddress serverIP;                     // The mDNS fills it in setup()
MFRC522 mfrc(MFRC_CS, MFRC_RST);        // RFID Reader object
int readerID = __INT_MAX__;             // Unique ID for the reader unit, gathered from the server
byte uidBytes[UID_BYTE_SIZE] = { 0 };   // Global array for UID data, prefilled with 0s
const char DATA_SEPARATOR[] = "-UID=";  // [ID]-UID=[UID] format
byte outputBuffer[sizeof(int) + sizeof(DATA_SEPARATOR) -1 + UID_BYTE_SIZE] = { 0 };  // Output buffer for uid sending
unsigned long lastHeartbeat = 0;        // Status check heartbeat timing variable

// ------------ Functions -------------
void setupMAC();
void setupEthernet();
void mdnsResolve();
int readUID();
bool sendUIDToServer(IPAddress serverIp, byte uid[UID_BYTE_SIZE]);
bool sendUIDToServer(IPAddress serverIp, String uidStr);
int encodeName(const char* hostname, uint8_t* buf, int bufSize);
int skipName(const uint8_t* msg, int msgLen, int offset);
bool resolveMDNS(const char* hostname, IPAddress& outIp, uint32_t timeoutMs, IPAddress mdnsMulticast);
void setLEDs(bool red, bool green, bool blue);


void setup() {

  // Set up LED pins
  pinMode(LED_R, OUTPUT);
  pinMode(LED_G, OUTPUT);
  pinMode(LED_B, OUTPUT);
  setLEDs(true, false, false);
  delay(LED_DELAY);
  setLEDs(false, true, false);
  delay(LED_DELAY);
  setLEDs(false, false, true);
  delay(LED_DELAY);
  setLEDs(false, true, true);
  delay(LED_DELAY);
  setLEDs(true, true, true);
  delay(LED_DELAY);
  setLEDs(true, true, false);
  delay(LED_DELAY);
  setLEDs(true, false, false);
  delay(LED_DELAY);

  // Start Serial connection
  Serial.begin(SERIAL_SPEED);
  while (!Serial) { ; }

  // Start SPI connection
  SPI.begin(SPI_SCK, SPI_MISO, SPI_MOSI);

  // Start and Setup Ethernet connection
  setupMAC();
  setupEthernet();

  // Resolve the server name via mDNS
  mdnsResolve();

  // Get ID for reader unit from server
  while (readerID == __INT_MAX__) {
    readerID = getReaderID(serverIP);

    if (readerID == __INT_MAX__) {
      Serial.println("Server unreachable, retrying...");
      // Villogó piros LED 1 másodperc intervallummal
      setLEDs(false, false, false);
      delay(500);
      setLEDs(true, false, false);
      delay(500);
    }
  }
  Serial.print("ID Gathered: "); Serial.println(readerID);

  // Initialize RFID reader
  mfrc.PCD_Init();
  setLEDs(false, false, true);
}

void loop() {

  if (readerID == __INT_MAX__)
  {
    //setLEDs(true, false, false);
    Serial.println("Reader not configured");
    while (readerID == __INT_MAX__)  // Blocking
    {
      readerID = getReaderID(serverIP);
      if (readerID == __INT_MAX__)
      {
        Serial.println("Reconnect failed");
        for (int i = 0; i < 3; i++)
        {
          setLEDs(false, false, false);
          delay(500);
          setLEDs(true, false, false);
          delay(500);
        }
      }
    }

    Serial.print("Reconnected, new ID: ");
    Serial.println(readerID);
    setLEDs(false, false, true);
  }
  

  unsigned long now = millis();

  // RFID reader
  int result = readUID();
  if (result > 0) {
    sendUIDToServer(serverIP, uidBytes);
  }
  
  // Heartbeat sending
  if (now - lastHeartbeat > HEARTBEAT_INTERVAL)
  {
    if (!sendHeartbeat(serverIP, readerID))
    {
      // No heartbeat
      Serial.println("Heartbeat failed, reconnection....");
      readerID = __INT_MAX__;
    }
    lastHeartbeat = now;
  }

  delay(10);
  
}

// Setting up MAC address from ESP's own MAC
void setupMAC() {
  uint64_t chipMac = ESP.getEfuseMac();  // ESP's factory MAC, 48 bits
  mac[0] = (chipMac >> 40) & 0xFF;
  mac[1] = (chipMac >> 32) & 0xFF;
  mac[2] = (chipMac >> 24) & 0xFF;
  mac[3] = (chipMac >> 16) & 0xFF;
  mac[4] = (chipMac >> 8) & 0xFF;
  mac[5] = chipMac & 0xFF;

  // DEBUG {
  Serial.print("ESP mac: ");
  for (int i = 0; i < 6; i++) {
    Serial.print(mac[i], HEX);
  }
  Serial.println();
  // DEBUG }
}

// Setting up Ethernet connection
void setupEthernet() {
  Ethernet.init(W5500_CS);
  // Initialize W5500 via DHCP
  while (Ethernet.begin(mac) == 0) {
    Serial.println("DHCP failed");  // Error msg
    delay(1000);                    // Try again after 1 sec
  }
  Serial.println("DHCP configured");  // Debug msg

  // Network infromations for debug
  Serial.print("Assigned IP: ");
  Serial.println(Ethernet.localIP());  // IP address from DHCP
  Serial.print("Subnet mask: ");
  Serial.println(Ethernet.subnetMask());  // Netmask from DHCP
  Serial.print("Gateway IP:  ");
  Serial.println(Ethernet.gatewayIP());  // Gateway from DHCP
}

// resulve mDNS name
void mdnsResolve() {
  Udp.beginMulticast(MDNS_MULTICAST_IP, MDNS_PORT);


  while (!resolveMDNS(SERVER_NAME, serverIP, MDNS_TIMEOUT, MDNS_MULTICAST_IP)) {
    Serial.println("MDNS Resolution failed");
    delay(1000);
  }

  //sendUIDToServer(serverIP, "Resolved Server IP: " + serverIP);
  Serial.print("Resolved Server IP: ");
  Serial.println(serverIP);
}

// Get unique ID for the reader unit from the server
int getReaderID(IPAddress serverIp) {
  // Debug
  Serial.print("Connecting to: ");
  Serial.print(serverIp);
  Serial.print(":");
  Serial.println(SERVER_DATA_PORT);

  // Connect to server
  if (!client.connect(serverIp, SERVER_DATA_PORT)) {
    Serial.println("Server connection failed");
    return __INT_MAX__;
  }

  // Data format: "[ID]-UID=[uid] like '12-UID=12345678'"
  // Creating output format
  int offset = 0;
  memcpy(outputBuffer + offset, &readerID, sizeof(int));
  offset += sizeof(int);
  memcpy(outputBuffer + offset, DATA_SEPARATOR, sizeof(DATA_SEPARATOR) - 1);
  offset += sizeof(DATA_SEPARATOR) - 1;
  memcpy(outputBuffer + offset, uidBytes, UID_BYTE_SIZE);
  offset += UID_BYTE_SIZE;

  size_t written = client.write(outputBuffer, sizeof(outputBuffer));
  // Check if full data has been sent to server
  if (written != offset) {
    Serial.println("Failed to send full data");
    client.stop();
    return __INT_MAX__;
  }

  uint8_t bytes[4];
  int received = 0;
  while (received < 4) {
    if (client.available()) {
      bytes[received++] = client.read();
    } else {
      delay(1); // rövid várakozás
    }
  }

  // big-endian -> little-endian
  int receivedID = ((int)bytes[0] << 24) | ((int)bytes[1] << 16) | ((int)bytes[2] << 8) | (int)bytes[3];
  
  client.stop();
  return receivedID;
}

// Card Reader
int readUID() {
  // returns UID len in bytes
  // Check for new card
  if (!mfrc.PICC_IsNewCardPresent()) return -1;
  if (!mfrc.PICC_ReadCardSerial()) return -2;

  // Save previous UID to a new byte[]
  byte uidBytesOld[UID_BYTE_SIZE] = { 0 };
  memcpy(uidBytesOld, uidBytes, UID_BYTE_SIZE); // copy the old byte[] to the new one  

  // Byte array clearing, clear the old uid
  memset(uidBytes, 0, sizeof(uidBytes));

  for (byte i = 0; i < mfrc.uid.size; i++) {
    uidBytes[i] = mfrc.uid.uidByte[i];
  }

  // Check if the ui read previously is new
  if (memcmp(uidBytes, uidBytesOld, UID_BYTE_SIZE) == 0)
  {
    return -3;  // not new uid present
  }

  // Debug:
  Serial.print("UDI = ");
  for (int i = 0; i < mfrc.uid.size; i++) {
    if (mfrc.uid.uidByte[i] < 0x10) Serial.print("0");
    Serial.print(mfrc.uid.uidByte[i], HEX);
    if (i != mfrc.uid.size - 1) {
      Serial.print(":");
    }
  }
  Serial.println();

  return mfrc.uid.size;
}

bool sendUIDToServer(IPAddress serverIp, byte uid[UID_BYTE_SIZE]) {
  // Debug
  Serial.print("Connecting to: ");
  Serial.print(serverIp);
  Serial.print(":");
  Serial.println(SERVER_DATA_PORT);

  // Connect to server
  if (!client.connect(serverIp, SERVER_DATA_PORT)) {
    Serial.println("Server connection failed");
    return false;
  }

  // Data format: "[ID]-UID=[uid] like '12-UID=12345678'"
  // Creating output format
  int offset = 0;
  memcpy(outputBuffer + offset, &readerID, sizeof(int));
  offset += sizeof(int);
  memcpy(outputBuffer + offset, DATA_SEPARATOR, sizeof(DATA_SEPARATOR) - 1);
  offset += sizeof(DATA_SEPARATOR) - 1;
  memcpy(outputBuffer + offset, uidBytes, UID_BYTE_SIZE);
  offset += UID_BYTE_SIZE;

  size_t written = client.write(outputBuffer, sizeof(outputBuffer));
  // Check if full data has been sent to server
  if (written != offset) {
    Serial.println("Failed to send full data");
    client.stop();
    return false;
  }

  // Debug 
  Serial.print("Sent ");
  Serial.print(written);
  Serial.println(" bytes");

  // Close TCP connection
  client.stop();
  delay(20);

  // Return success
  return true;
}

// --- segédfüggvények: DNS név encode / parse ---
int encodeName(const char* hostname, uint8_t* buf, int bufSize) {
  // hostname pl: "bence-Mint.local"
  // encode: [len]label...[0]
  int pos = 0;
  const char* start = hostname;
  while (*start) {
    const char* dot = strchr(start, '.');
    int labelLen = dot ? (dot - start) : strlen(start);
    if (labelLen == 0 || labelLen > 63) return -1;
    if (pos + 1 + labelLen >= bufSize) return -1;
    buf[pos++] = (uint8_t)labelLen;
    memcpy(buf + pos, start, labelLen);
    pos += labelLen;
    if (!dot) break;
    start = dot + 1;
  }
  if (pos >= bufSize) return -1;
  buf[pos++] = 0;  // terminator
  return pos;
}

int skipName(const uint8_t* msg, int msgLen, int offset) {
  // lépked a név fölött (figyelem: pointerek lehetnek)
  int p = offset;
  while (p < msgLen) {
    uint8_t len = msg[p];
    if ((len & 0xC0) == 0xC0) {  // pointer (2 bytes)
      p += 2;
      return p;
    }
    if (len == 0) {
      p += 1;
      return p;
    }
    p += 1 + len;
  }
  return -1;
}

// --- mDNS resolve függvény ---
bool resolveMDNS(const char* hostname, IPAddress& outIp, uint32_t timeoutMs = 3000, IPAddress mdnsMulticast = MDNS_MULTICAST_IP) {
  // packet buffer
  uint8_t packet[512];
  // build DNS header
  memset(packet, 0, sizeof(packet));
  // mDNS can use zero ID; használhatunk akár véletlent, de nem fontos
  packet[0] = 0x00;
  packet[1] = 0x00;  // ID
  packet[2] = 0x00;
  packet[3] = 0x00;  // flags (standard query)
  packet[4] = 0x00;
  packet[5] = 0x01;  // QDCOUNT = 1
  packet[6] = 0x00;
  packet[7] = 0x00;  // ANCOUNT = 0
  packet[8] = 0x00;
  packet[9] = 0x00;  // NSCOUNT = 0
  packet[10] = 0x00;
  packet[11] = 0x00;  // ARCOUNT = 0 (we don't set additional)

  int pos = 12;
  int encLen = encodeName(hostname, packet + pos, sizeof(packet) - pos);
  if (encLen < 0) return false;
  pos += encLen;
  // QTYPE = A (1)
  packet[pos++] = 0x00;
  packet[pos++] = 0x01;
  // QCLASS = IN (1) -- for mDNS top bit (cache-flush) not relevant in query
  packet[pos++] = 0x00;
  packet[pos++] = 0x01;

  // send multicast query
  // próbálunk multicast packetet küldeni mdnsMulticast:5353
  if (!Udp.beginPacket(mdnsMulticast, MDNS_PORT)) {
    //ha a library támogatja, else: beginPacketMulticast lehet külön
    // de sok EthernetUDP implementáció elfogadja beginPacket(multicastIP, 5353)
  }
  Udp.write(packet, pos);
  Udp.endPacket();

  // vár válaszra (timeout)
  uint32_t start = millis();
  while (millis() - start < timeoutMs) {
    int packetSize = Udp.parsePacket();
    if (packetSize > 0) {
      // olvassuk be a választ egy bufferbe (max 512)
      if (packetSize > (int)sizeof(packet)) packetSize = sizeof(packet);
      Udp.read(packet, packetSize);

      // ellenőrzés: header ANCOUNT
      int ancount = (packet[6] << 8) | packet[7];
      int offset = 12;
      // skip kérdés(ek)
      // QDCOUNT:
      int qdcount = (packet[4] << 8) | packet[5];
      for (int i = 0; i < qdcount; ++i) {
        int newOff = skipName(packet, packetSize, offset);
        if (newOff < 0) break;
        offset = newOff + 4;  // skip QTYPE(2) + QCLASS(2)
      }

      // bejárjuk az answer rekordokat és keressük az A rekordot az adott névre
      for (int i = 0; i < ancount; ++i) {
        if (offset >= packetSize) break;
        // név (lehet pointer)
        int nameEnd = skipName(packet, packetSize, offset);
        if (nameEnd < 0) break;
        // read type,class,ttl,rdlength
        if (nameEnd + 10 > packetSize) break;
        uint16_t type = (packet[nameEnd] << 8) | packet[nameEnd + 1];
        uint16_t cls = (packet[nameEnd + 2] << 8) | packet[nameEnd + 3];
        //uint32_t ttl = (packet[nameEnd+4]<<24)|(packet[nameEnd+5]<<16)|(packet[nameEnd+6]<<8)|packet[nameEnd+7];
        uint16_t rdlen = (packet[nameEnd + 8] << 8) | packet[nameEnd + 9];
        int rdataOff = nameEnd + 10;
        if (rdataOff + rdlen > packetSize) break;

        // mDNS: class may have the cache-flush bit in top bit; mask it
        uint16_t cls_masked = cls & 0x7FFF;

        // type A = 1, class IN = 1, rdata_len should be 4
        if (type == 1 && cls_masked == 1 && rdlen == 4) {
          // found IPv4 address
          outIp = IPAddress(packet[rdataOff], packet[rdataOff + 1], packet[rdataOff + 2], packet[rdataOff + 3]);
          return true;
        }

        // lépés a következő resource recordra
        offset = rdataOff + rdlen;
      }
      // ha nem találtunk A rekordot a válaszban, folytatjuk a várakozást (több válasz jöhet multicaston)
    }

    delay(10);
  }  // timeout vége

  return false;  // timeout, nem talált A rekordot
}

// this method sets the led (R, G, B) to te given value
void setLEDs(bool red, bool green, bool blue) {
  digitalWrite(LED_R, red);
  digitalWrite(LED_G, green);
  digitalWrite(LED_B, blue);
}

// this method sends heartbeat
bool sendHeartbeat(IPAddress serverIP, int readerID) {
  if (!client.connect(serverIP, SERVER_HEARTBEAT_PORT)) {
    return false;
  }

  // Simeple message: ID in binary, 4 byte, big endian
  byte buffer[4];
  buffer[0] = (readerID >> 24) & 0xFF;
  buffer[1] = (readerID >> 16) & 0xFF;
  buffer[2] = (readerID >> 8) & 0xFF;
  buffer[3] = readerID & 0xFF;

  client.write(buffer, 4);

  client.stop();
  return true;
}
