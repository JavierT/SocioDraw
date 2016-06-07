# SocioDraw
Android app to connect several devices under the same wireless network.

Android min required version: 4.0

Framework used: Alljoyn https://allseenalliance.org/framework

The application interconnect multiple android devices to allow play a drawing game. One devices acts as a server creating the game and the others join. The game is limited to 6 players due to playability but the connection can hold more than 6 players. 

Devices must be connected under the same wireless network. This network does not require internet access. Another device can be used as a private hostpot.

Two game modes: Collaborative and competitive.

The server mode receives a picture that must be described to the other players. 
The other players, painters from now on, must draw in accordance to the server / leader's descriptions. 

In competitive mode, each player creates a version of the pattern picture.
In the collaborative mode, the leader must divide the picture amongst all the players. The painters share a canvas to replicate the pattern picture. They do NOT see what the others are doing. Only the server device contains the collaborative picture in real-time.

The game allows the devices to create their own pictures to be used in the upcoming games. 

Drawing time is limited to 3 minutes.

There is not score, players must decide the best picture in the competitive mode. The collaborative mode is a team-work activity where players must coordinate to achieve the best outcome possible. 

A user study and more detailed information can be found here: 
http://dspace.cc.tut.fi/dpub/handle/123456789/23787

Feel free to contact me if you find it interesting!
contact@javiertresaco.com
