<img src="/app/src/github/Banner_v1.jpg" alt="ArduNet Banner" width="500px" height="auto">

# ArduNet
ArduNet is an android application designed to graph sensor data using BLE (bluetooth low-energy). 

# Features
* Scan for nearby Bluetooth devices
* Record sensor data (x,y) into .csv format
* Export .csv files to Gmail, Outlook or Google Drives

Demo: https://bit.ly/3h1fG3k
Download APK: https://bit.ly/3nuJkQP 

# Demo
## Collect and save Sensor Data using BLE
Scan for devices | Pair Devices | Graph Data | Save to Drives
--- | --- | --- | --- 
<img src="/app/src/github/Scanned.jpg" alt="Scan for devices" width="auto" height="300px"> | <img src="/app/src/github/Connected_Devices.jpg" alt="Graph Data" width="auto" height="300px">| <img src="/app/src/github/Graph.jpg" alt="Graph Data" width="auto" height="300px"> | <img src="/app/src/github/Share.jpg" alt="Save to Drives" width="auto" height="300px"> 

## Analyze BLE Packet Tool 
Scan for devices | Pair Devices | Find Characteristic | Analyze BLE Packet
--- | --- | --- | ---
<img src="/app/src/github/Scanned.jpg" alt="Scan for devices" width="auto" height="300px">|<img src="/app/src/github/Connected_Devices.jpg" alt="Graph Data" width="auto" height="300px">|<img src="/app/src/github/Debug_Mode.jpg" alt="Debug Mode" width="auto" height="300px">|<img src="/app/src/github/Debug_Detailed.jpg" alt="Analyze Packet" width="auto" height="300px">

# Code
## Set up Service and Characteristic
Set your Bluetooth device to transmit the packet under the following default service and characteristic UUID.  The app will only display data if it matches these UUIDs.
* Service UUID: `4fafc201-1fb5-459e-8fcc-c5c9c331914b`
* Characteristic UUID: `beb5483e-36e1-4688-b7f5-ea07361b26a8`

## Characteristic Packet Format
Broadcast the BLE packet with the following packet format\
`AXXX/BXXX/CXXX/DXXX/EXXX/FXXX/GXXX/HXXX/IXXX/JXXX/KXXX/LXXX/MXXX`\

Each packet is delimited using a `/` character and is uniquely identified by a letter ranging from A to M.  \
`XXX` represents the value associated with the letter ID.

A | B | C | D | E | F | G | H | I | J | K | L | M
--- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- 
string | integer | double | double | double | double | double | double | double | double | double | double | double 

