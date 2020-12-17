<img src="/app/src/github/Banner_v1.jpg" alt="ArduNet Banner" width="500px" height="auto">

# ArduNet
ArduNet is an android application designed to graph sensor data using BLE (bluetooth low-energy). This application was designed to work with the ESP 32 LoRa V2 microcontroller, but is also compatible with any device with a BLE chip capable of braodcasting.

# Features
* Scan for nearby Bluetooth devices
* Record sensor data (x,y) into .csv format
* Export .csv files to Gmail, Outlook or Google Drives

Demo: https://bit.ly/3h1fG3k  \
Download APK: https://bit.ly/3nuJkQP 

# Demo
## Collect and save Sensor Data using BLE
Scan for devices | Pair Devices | Graph Data | Save to Drives
--- | --- | --- | --- 
<img src="/app/src/github/Scanned.jpg" alt="Scan for devices" width="auto" height="300px"> | <img src="/app/src/github/Connected_Devices.jpg" alt="Graph Data" width="auto" height="300px">| <img src="/app/src/github/Graph.jpg" alt="Graph Data" width="auto" height="300px"> | <img src="/app/src/github/Share.jpg" alt="Save to Drives" width="auto" height="300px"> 

# File Saving
After recording data, the files will automatically be saved in the following format:\
Example: `ESP32_10-30-2020(9_30_02).csv` \\ 
ESP32 is the device name, 10-30-2020 is the date, and 9_30_02 corresponds to 9:30:02 military time.  These values will change depending on the packet transmitted. 

## Analyze BLE Packet Tool 
Scan for devices | Pair Devices | Find Characteristic | Analyze BLE Packet
--- | --- | --- | ---
<img src="/app/src/github/Scanned.jpg" alt="Scan for devices" width="auto" height="300px">|<img src="/app/src/github/Connected_Devices.jpg" alt="Graph Data" width="auto" height="300px">|<img src="/app/src/github/Debug_Mode.jpg" alt="Debug Mode" width="auto" height="300px">|<img src="/app/src/github/Debug_Detailed.jpg" alt="Analyze Packet" width="auto" height="300px">

# How to Use
## Set up Service and Characteristic
Set your Bluetooth device to transmit the packet under the following default service and characteristic UUID.  The app will only display data if it matches these UUIDs.
* Service UUID: `4fafc201-1fb5-459e-8fcc-c5c9c331914b`
* Characteristic UUID: `beb5483e-36e1-4688-b7f5-ea07361b26a8` 
\
For instructions on how to set up your Arduino to broadcast BLE:
https://github.com/a2ruan/ESP32-Sensor-Array

## Characteristic Packet Format
Broadcast the BLE packet with the following packet format\
`AXXX/BXXX/CXXX/DXXX/EXXX/FXXX/GXXX/HXXX/IXXX/JXXX/KXXX/LXXX/MXXX`
\\
Each packet is delimited using a `/` character and is uniquely identified by a letter ranging from A to M.  \
`XXX` represents the value associated with the letter ID.

A | B | C | D | E | F | G | H | I | J | K | L | M
--- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- 
string | integer | double | double | double | double | double | double | double | double | double | double | double 
Device Name | Time | Temperature | Humidity | Concentration |Resistance 1 | Delta Resistance 1 | Resistance 2 | Delta Resistance 2 | Resistance 3 | Delta Resistance 3 | Resistance 4 | Delta Resistance 4 
