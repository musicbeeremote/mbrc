Change Log
-----------

# Versions

## 1.0.0-beta.3
-Fixes an issue where the notification would not disappear on a connected wearable after closing MusicBee.


## 1.0.0-beta.2
- Fixes a potential crash on the progress updater.

## 1.0.0-beta.1
- Full migration to kotlin language.
- Commands are now instantiated on registration.
- Adds empty view for lists without data.
- Changes the widget update mechanism to avoid previous issues where the widgets failed to update properly.
- Migrates cover loading mechanism to a newer API implementation.
- Migrates lyrics to an newer API implementation.
- Playlists now work through the no broadcast mechanism.
- Adds a library refresh actionbar action.

## 1.0.0-alpha.1
- Increases minimum supported Android version from 2.3 to 4.1
- Reworks part of the application UI
- Replaces Search with Library browser. Library browser has search functionality for the locally cached metadata.
- Adds basic playlist support. View and click two play.
- Merges Help and Feedback views.
- Fixes an issue where the application would start automatically on device boot.
- Fixes an issue with the widgets that stopped to update after some time.
- Increases the timeout of the automatic discovery function.
- Adds extra actions on the Incoming call action (Stop and Pause along with Volume reduce).
- Reworks the connection settings detection functionality. (If the network allows it the detection should happen automatically on the application startup)
- Removes the dialogs that used to appear on each new setup.
