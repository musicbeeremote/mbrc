MusicBee Remote - for Android
=============================
About
-------
MusicBee is a remote control application that is used to control [MusicBee](http://getmusicbee.com/) player using a network connected Android device. The application is freely available through [Google Play](https://play.google.com/store/apps/details?id=com.kelsos.mbrc) and also requires the associated [plugin](https://github.com/kelsos/mbrc-plugin) (dll) for MusicBee to function.

The application consumes an RESTlike HTTP API provided by the plugin. And uses a websocket connection for small push notifications.

MusicBee Remote was presented as part of my thesis on "*Android and application development for mobile devices*".

You can find help on how to use the application and more info on the dedicated [website](http://kelsos.net/musicbeeremote/) along with links to the binary version of the plugin.

You can also find information about the plugin and the remote to the dedicated topic in the [MusicBee forums](http://getmusicbee.com/forum/index.php?topic=7221.new;topicseen#new).

Building
-------
Clone the repository and import on IntelliJ IDEA or Android Studio.

Credits
-----------
### Artwork

Many of the icons used are created by [Tasos Papazoglou Chalikias](https://github.com/sushiperv) and are licenced under the [Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.](https://creativecommons.org/licenses/by-nc-nd/3.0/deed.en_US) some of them where taken by other projects and some awful icons during the earlier development where created by me. The original logo idea belongs to Jordan Georgiades. Any other icons introduced in version 0.9.7 and later was probably my work. Some design ideas are based on [mockups](https://groups.google.com/forum/#!topic/musicbee-remote/wgm029yfJnU) by Carlos Parga

### Inspiration
Initially a great source of inspiration for this project was the [Cyanogen Apollo player](https://github.com/CyanogenMod/android_packages_apps_Apollo)
along with [Google Play Music for Android](https://play.google.com/store/apps/details?id=com.google.android.music). Nowadays I am looking closely the material design documentation.

### Dependencies

*   [Toothpick](https://github.com/stephanenicolas/toothpick)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [Jackson](http://jackson.codehaus.org/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [RxJava](https://github.com/ReactiveX/RxJava)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [RxAndroid](https://github.com/ReactiveX/RxAndroid)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [Retrofit](https://github.com/square/retrofit)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [Picasso](https://github.com/square/picasso)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [OKHttp](https://github.com/square/okhttp)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*   [DBFlow](https://github.com/Raizlabs/DBFlow)

    License: [MIT](https://github.com/Raizlabs/DBFlow/blob/master/LICENSE)

*   [Material Dialogs](https://github.com/afollestad/material-dialogs/)

    License: [MIT](https://github.com/afollestad/material-dialogs/blob/master/LICENSE.txt)
    
*   [Butterknife](https://github.com/JakeWharton/butterknife)

    License: [Apache 2.0](https://raw.githubusercontent.com/JakeWharton/butterknife/master/LICENSE.txt)
    
*   [Preference Fragment Compat](https://github.com/Machinarius/PreferenceFragment-Compat)

    License: [Apache 2.0](https://raw.githubusercontent.com/Machinarius/PreferenceFragment-Compat/master/LICENSE)
               

License
----------

The source code of the application is licensed under the [GPLv3](https://www.gnu.org/licenses/gpl.html) license.

    MusicBee Remote (for Android)
    Copyright (C) 2011 - 2016  Konstantinos Paparas

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
