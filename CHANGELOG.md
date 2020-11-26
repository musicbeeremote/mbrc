Changelog
-----------

## [Unreleased]
### Added
- ([#103]) Adds radio station support. 
- ([#113]) Adds ability to switch radio stations.

[#113]: https://github.com/musicbeeremote/android-app/issues/113
[#103]: https://github.com/musicbeeremote/android-app/issues/103

## [1.2.1] - 2020-11-26
### Changed
- Fixes a background crash with the application service.
- Fixes a crash on cover retrieval.
- Fixes an issue with the service discovery.

## [1.2.0] - 2020-11-19
### Changed
- Fixed a crash on the service when running on the background

## [1.2.0-beta1] - 2020-11-07
### Changed
- Adds support for Android 11.
- Improves the library metadata sync performance.
- Updates to the latest Material Design library.
- Integrates the library search functionality to the library screen.
- Removes the ability to hide the notification due to background execution limitations introduced in Android.
- Improves media integration with Android.
    
### Removed
- Removes fast scrolling due to performance issues.

## [1.1.0] - 2017-01-25
### Added
- ([#100]) Adds the option to enqueue all tracks and play the selected on the library track tab.
- ([#92]) Adds the option to show only album artists in the library view.
- ([#99]) Adds fast scrolling to the library tabs.
- ([#112]) Adds Play Now (Queue All) option for albums.

### Changed
- ([#95]) Refreshes the playlists along with the library data.
- ([#96]) Sorting now starts only from the triple dash icon in the now playing.
- ([#91]) Fixes the library search collapse that did not work previously.
- ([#94]) Reworks the default action option to affect only the track view.
- ([#93]) Fixes the query for showing the albums of an artist.
- ([#90]) Fixes the tab colors in the Help screen.
- ([#105]) Automatically scrolls to the playing track when entering the now playing screen.
- ([#97]) Fixes an issue where the remote session volume slider wouldn't work.
- ([#102]) Fixes the ordering of the Genres to alphabetical.
- Pressing the track info on the main view should now open the Now Playing screen.

[#100]: https://github.com/musicbeeremote/android-app/issues/100
[#92]: https://github.com/musicbeeremote/android-app/issues/92
[#99]: https://github.com/musicbeeremote/android-app/issues/99
[#112]: https://github.com/musicbeeremote/android-app/issues/112
[#95]: https://github.com/musicbeeremote/android-app/issues/95
[#96]: https://github.com/musicbeeremote/android-app/issues/96
[#91]: https://github.com/musicbeeremote/android-app/issues/91
[#94]: https://github.com/musicbeeremote/android-app/issues/94
[#93]: https://github.com/musicbeeremote/android-app/issues/93
[#90]: https://github.com/musicbeeremote/android-app/issues/90
[#105]: https://github.com/musicbeeremote/android-app/issues/105
[#97]: https://github.com/musicbeeremote/android-app/issues/97
[#102]: https://github.com/musicbeeremote/android-app/issues/102

## [1.0.1] - 2016-12-22
### Changed
- Fixes a crash with a null extras payload when updating the widget.
- Fixes a crash with a null key event on the media intent handler.
- Fixes an issue where the artist albums would not display properly.

## [1.0.0] - 2016-12-15
### Added 
- Adds a message on connection to inform the user in case an incompatible version of the plugin is used.

### Changed
- Fixes an issue where the notification would not disappear on a connected wearable after closing MusicBee.
- Fixes an issue where the volume control in the wearable notification would not work.
- Fixes an issue where the temporary stop during track change would make the notification flicker.
- Fixes an issue where exiting the application through an other than MainActivity would navigate to the main activity.


## [1.0.0-beta.2] - 2016-12-03
### Changed
- Fixes a potential crash on the progress updater.

## [1.0.0-beta.1] - 2016-11-28
### Added
- Adds empty view for lists without data.
- Adds a library refresh actionbar action.

### Changed
- Full migration to kotlin language.
- Commands are now instantiated on registration.
- Changes the widget updateNotification mechanism to avoid previous issues where the widgets failed to updateNotification properly.
- Migrates cover loading mechanism to a newer API implementation.
- Migrates lyrics to an newer API implementation.
- Playlists now work through the no broadcast mechanism.

## [1.0.0-alpha.1] - 2016-09-15
### Added
- Adds basic playlist support. View and click two play.
- Adds extra actions on the Incoming call action (Stop and Pause along with Volume reduce).

### Changed
- Increases minimum supported Android version from 2.3 to 4.1
- Reworks part of the application UI
- Replaces Search with Library browser. Library browser has search functionality for the locally cached metadata.
- Merges Help and Feedback views.
- Fixes an issue where the application would start automatically on device boot.
- Fixes an issue with the widgets that stopped to updateNotification after some time.
- Increases the timeout of the automatic discovery function.
- Reworks the connection settings detection functionality. (If the network allows it the detection should happen automatically on the application startup)

### Removed
- Removes the dialogs that used to appear on each new setup.


[Unreleased]: https://github.com/musicbeeremote/android-app/compare/v1.2.1...HEAD
[1.2.1]: https://github.com/musicbeeremote/android-app/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/musicbeeremote/android-app/compare/v1.2.0-beta1...v1.2.0
[1.2.0-beta1]: https://github.com/musicbeeremote/android-app/compare/v1.1.0...v1.2.0-beta1
[1.1.0]: https://github.com/musicbeeremote/android-app/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/musicbeeremote/android-app/compare/v1.0.0...v1.0.1
[1.0.0-beta.2]: https://github.com/musicbeeremote/android-app/compare/v1.0.0-beta.1...v1.0.0-beta.2
[1.0.0-beta.1]: https://github.com/musicbeeremote/android-app/compare/v1.0.0-alpha.1...v1.0.0-beta.1
[1.0.0-alpha.1]: https://github.com/musicbeeremote/android-app/compare/v0.11.2...v1.0.0-alpha.1
