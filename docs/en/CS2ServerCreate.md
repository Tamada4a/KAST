# What is the file about?
This file contains information on how to create, configure and run a local CS2 server

# Instructions
1. Download and install <a href="https://developer .valvesoftware.com/wiki/SteamCMD#Downloading_SteamCMD">SteamCMD</a>.
2. Launch SteamCMD and after `Steam>` specify the path to install the server:
   1. Example for Windows: `force_install_dir c:\cs2-ds\` or `force_install_dir .\cs2-ds\`.
   2. Example for Linux: `force_install_dir /full/path/to/cs2-ds/`.
3. Log in anonymously by entering `login anonymous`.
4. Install or update CS2. If this is your first installation or if you are trying to verify the integrity of the server files: `app_update 730 validate`. If necessary, simply upgrade an existing server: `app_update 730`.
5. Link the game server to your Steam account - go to <a href="https://steamcommunity.com/dev/managegameservers">link</a>. If the account meets the requirements specified on the page, you can proceed to the next steps.
6. In the upper right corner, click "Log in" and log in to your Steam account if you haven't done so yet. In the "App ID" field, you need to enter the game ID - 730.
7. In the "Memo" field, enter any text (you can use only numbers and Latin letters). It is advisable to write the text according to which you will later be able to remember for which game server you generated this token.
8. After clicking on the "Create" button, you will see a table with the generated tokens.
9. Copy the received token, open `path/to/cs2/server/game/csgo/cfg` and, if necessary, create the file `autoexec.cfg`. Insert the token into this file in the <b>sv_setsteamaccount</b> parameter, that is: <i>sv_setsteamaccount "TOKEN"</i>, where the received token must be entered instead of `TOKEN`.
10. The following lines must also be inserted into the `autoexec.cfg` file:
```
sv_logfile 1                                                                // Logging to a file
log on								                                                      // Log output
mp_logmoney 1						                                                    // Logging money change to console
mp_logdetail 3                                                              // Logging damage change to console
mp_logdetail_items 1				                                                // Logging information about the player's weapon to the console
logaddress_add_http "http://localhost:8080/parseLogs/<cup-name>/<matchID>"  // Sending logs from the server
developer "1"						                                                    // Developer Mode
mp_teamname_1 “Team tag for team 1”                                         // The tag of the first team. Must match the tag in the database
mp_teamname_2 “Team tag for team 2”                                         // The tag of the second team. Must match the tag in the database
```
where `<cup-name>` is the name of the tournament with spaces replaced by `-`, `<match ID>` is the ID of the match for which logs are sent, and instead of `http://localhost:8080` you can specify the address where your server is located.

11. Starting the server:
    1. For Windows. Go to the folder where you downloaded the server, open `/game/bin/win64` and enter the following command in the console: `.\cs2.exe -dedicated +map de_dust2`.
    2. For Linux. Go to the folder where you downloaded the server, open `/game/bin/linuxsteamrt64/cs2` and enter the following command in the console: `./cs2 -dedicated +map de_dust2`.
