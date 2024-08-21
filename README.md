[![](https://dcbadge.vercel.app/api/server/hw3j3RwfJf) ](https://discord.gg/hw3j3RwfJf)
 [![Donate](https://img.shields.io/badge/donate-$-brown.svg?style=for-the-badge)](http://paypal.me/mtpsilva)
<a href="https://github.com/sponsors/aeonSolutions">
   <img height="40" src="https://github.com/aeonSolutions/PCB-Prototyping-Catalogue/blob/main/media/become_a_github_sponsor.png">
</a>
[<img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" data-canonical-src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" height="30" />](https://www.buymeacoffee.com/migueltomas)
![](https://img.shields.io/github/last-commit/aeonSolutions/aeonlabs-open-software-catalogue?style=for-the-badge)
<a href="https://trackgit.com">
<img src="https://us-central1-trackgit-analytics.cloudfunctions.net/token/ping/lgeu3mh7autbw0q1rjhl" alt="trackgit-views" />
</a>
![](https://views.whatilearened.today/views/github/aeonSolutions/aeonlabs-open-software-catalogue.svg)
[![Open Source Love svg1](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](#)

 **Navigation** | [AeonLabs Main Index](https://github.com/aeonSolutions/aeonSolutions/blob/main/aeonSolutions-Main-Index.md)  >> [Open Source Code Software](https://github.com/aeonSolutions/aeonlabs-open-software-catalogue)  >>  Smart Concrete Android App
 
<p align="right">
 <a href="https://github-com.translate.goog/aeonSolutions/aeonlabs-open-software-catalogue?_x_tr_sl=en&_x_tr_tl=pt&_x_tr_hl=en&_x_tr_pto=wapp">Change Language</a> <br>
Last update: 20-08-2024
</p>

# Smart Concrete  Android App
The Smart Concrete App  is a fork of the [ArduNet App](https://github.com/a2ruan/ArduNetApp), an Android application designed to graph sensor data using BLE (Bluetooth low-energy). This application was designed to work with the ESP 32 LoRa V2 microcontroller but is also compatible with any device with a BLE chip capable of broadcasting.
\
Graphing is done on a separate multi-threaded process, enabling data recording in the background even when the app is not in focus.

#### Requirements
- Android SDK version 29 ( Android 10)

# Features
* Scan for nearby Bluetooth devices
* Record sensor data (x,y) into .csv format
* Export .csv files to Gmail, Outlook, or Google Drives

Demo:   \
Download APK: 


## Collect and save Sensor Data using BLE
Scan for devices | Pair Devices | Graph Data | Save to Drives
--- | --- | --- | --- 
<img src="/app/src/github/Scanned.jpg" alt="Scan for devices" width="auto" height="300px"> | <img src="/app/src/github/Connected_Devices.jpg" alt="Graph Data" width="auto" height="300px">| <img src="/app/src/github/Graph.jpg" alt="Graph Data" width="auto" height="300px"> | <img src="/app/src/github/Share.jpg" alt="Save to Drives" width="auto" height="300px"> 

# File Saving
After recording data, the files will automatically be saved in the following format:\
Example: `ESP32_10-30-2020(9_30_02).csv` \
\
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
https://github.com/aeonSolutions/AeonLabs-ESP32-Sensor-Array

## Characteristic Packet Format
Broadcast the BLE packet with the following packet format\
`AXXX/BXXX/CXXX/DXXX/EXXX/FXXX/GXXX/HXXX/IXXX/JXXX/KXXX/LXXX/MXXX`
\
\
Each packet is delimited using a `/` character and is uniquely identified by a letter ranging from A to M.  \
`XXX` represents the value associated with the letter ID.

A | B | C | D | E | F | G | H | I | J | K | L | M
--- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- 
string | integer | double | double | double | double | double | double | double | double | double | double | double 
Device Name | Time | Temperature | Humidity | Concentration |Resistance 1 | Delta Resistance 1 | Resistance 2 | Delta Resistance 2 | Resistance 3 | Delta Resistance 3 | Resistance 4 | Delta Resistance 4 

<br />


## Author

You can get in touch with me on my LinkedIn Profile:

#### Miguel Tomas

[![LinkedIn Link](https://img.shields.io/badge/Connect-Miguel--Tomas-blue.svg?logo=linkedin&longCache=true&style=social&label=Connect)](https://www.linkedin.com/in/migueltomas/)

<a href="https://stackexchange.com/users/18907312/miguel-silva"><img src="https://stackexchange.com/users/flair/18907312.png" width="208" height="58" alt="profile for Miguel Silva on Stack Exchange, a network of free, community-driven Q&amp;A sites" title="profile for Miguel Silva on Stack Exchange, a network of free, community-driven Q&amp;A sites" /></a>

<a href="https://app.userfeel.com/t/2f6cb1e0" target="_blank"><img src="https://app.userfeel.com/tester/737648/image?.png" width="257" class="no-b-lazy"></a>

You can also follow my GitHub Profile to stay updated about my latest projects: [![GitHub Follow](https://img.shields.io/badge/Connect-Miguel--Tomas-blue.svg?logo=Github&longCache=true&style=social&label=Follow)](https://github.com/aeonSolutions)

<br>

**Hire me** <br>
See [here](https://github.com/aeonSolutions/PCB-Prototyping-Catalogue/wiki/How-to-Hire-AeonLabs) how to hire AeonLabs.

<br>

### Be supportive of my dedication and work towards technology education and buy me a cup of coffee
The PCB design files I provide here are free. If you like this Smart Device or use it, please consider buying me a cup of coffee, a slice of pizza, or a book to help me study, eat, and think new PCB design files.

<p align="center">
    <a href="https://www.buymeacoffee.com/migueltomas">
        <img height="35" src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png">
    </a>
</p>


### Make a donation on PayPal
Make a donation on PayPal and get a TAX refund*.

<p align="center">
    <a href="http://paypal.me/mtpsilva">
        <img height="35" src="https://github.com/aeonSolutions/PCB-Prototyping-Catalogue/blob/main/media/paypal_small.png">
    </a>
</p>

### Support all these open hardware projects and become a GitHub sponsor  
Did you like any of my PCB KiCad Designs? Help and Support my open work to all by becoming a GitHub sponsor.

<p align="center">
    <a href="https://github.com/aeonSolutions/PCB-Prototyping-Catalogue/blob/main/become_a_sponsor/aeonlabs-github-sponsorship-agreement.docx">
        <img height="50" src="https://github.com/aeonSolutions/PCB-Prototyping-Catalogue/blob/main/media/want_to_become_a_sponsor.png">
    </a>
    <a href="https://github.com/sponsors/aeonSolutions">
        <img height="50" src="https://github.com/aeonSolutions/PCB-Prototyping-Catalogue/blob/main/media/become_a_github_sponsor.png">
    </a>
</p>

## License

Before proceeding to download any of AeonLab's open-source code for software solutions and/or open hardware electronics for smart devices and data acquisition make sure you are choosing the right license for your project. See [AeonLabs Solutions for Open Hardware & Open Source Code](https://github.com/aeonSolutions/aeonSolutions/wiki/AeonLabs-Licensing) for more information. For commercial business solutions contact AeonLabs ℹ️ using the contacts above. Thank you 🙏.
