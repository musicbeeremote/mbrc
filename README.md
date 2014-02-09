MusicBee Remote - for Android
=============================
About
-------
MusicBee is a remote control application that is used to control [MusicBee](http://getmusicbee.com/) player using a network connected Android device. The application is freely available through [Google Play](https://play.google.com/store/apps/details?id=com.kelsos.mbrc) and also requires the associated [plugin](https://github.com/kelsos/mbrc-plugin) (dll) for MusicBee to function. The Application acts as a client over a TCP socket connection. The application protocol used for communication is based on JSON formatted messages passed as text over the socket. 

MusicBee Remote was presented as part of my thesis on "*Android and application development for mobile devices*".

You can find help on how to use the application and more info on the dedicated [website](http://kelsos.net/musicbeeremote/) along with links to the binary version of the plugin.

You can also find information about the plugin and the remote to the dedicated topic in the [MusicBee forums](http://getmusicbee.com/forum/index.php?topic=7221.new;topicseen#new).

Building
-------
### Older version
To build the application the usage of [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer) is required. After cloning the SDK Deployer repository in a directory you need to run:
``mvn install -P 4.3`` to install the necessary libraries to build the application with Maven and the Android Maven Plugin. The application also depends on **DragSortListView**. You should use the [fork](https://github.com/kelsos/drag-sort-listview) since the library is no longer maintained and the original will require some modifications to build.
### Latest development
The latest development version have moved to the new Gradle based Android build system.

Credits
-----------
### Artwork

Many of the icons used are created by [Tasos Papazoglou Chalikias](https://github.com/sushiperv) and are licenced under the [Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.](https://creativecommons.org/licenses/by-nc-nd/3.0/deed.en_US) some of them where taken by other projects and some awful icons during the earlier development where created by me. The original logo idea belongs to Jordan Georgiades. Any other icons introduced in version 0.9.7 and later was probably my work. The newest design after version 0.9.8 is based on [mockups](https://groups.google.com/forum/#!topic/musicbee-remote/wgm029yfJnU) by Carlos Parga

### Inspiration
A great source of inspiration for this project was the [Cyanogen Apollo player](https://github.com/CyanogenMod/android_packages_apps_Apollo)
along with [Google Play Music for Android](https://play.google.com/store/apps/details?id=com.google.android.music).

### Runtime dependencies

*   [Guice](http://code.google.com/p/google-guice/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [RoboGuice](http://code.google.com/p/roboguice/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [Otto](http://square.github.io/otto/)
 
    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [Jackson](http://jackson.codehaus.org/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [DragSortListView](https://github.com/bauerca/drag-sort-listview)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
    
*   [Crouton](https://github.com/keyboardsurfer/Crouton)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

    
### Build dependencies

*   [Maven Android Plugin](http://code.google.com/p/maven-android-plugin/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

### Dependencies of older versions

    
*   [roboguice-sherlock](https://github.com/rtyley/roboguice-sherlock)
 
    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)


License
----------
The source code of the application is licensed under the [GPLv3](https://www.gnu.org/licenses/gpl.html) license.

    MusicBee Remote (for Android)
    Copyright (C) 2013  Konstantinos Paparas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


