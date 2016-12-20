Change Log
-----------

# Versions

## 1.0.1
- Fixes a crash with a null extras payload when updating the widget.
- Fixes a crash with a null key event on the media intent handler.

## 1.0.0
- Fixes an issue where the notification would not disappear on a connected wearable after closing MusicBee.
- Fixes an issue where the volume control in the wearable notification would not work.
- Fixes an issue where the temporary stop during track change would make the notification flicker.
- Fixes an issue where exiting the application through an other than MainActivity would navigate to the main activity.
- Adds a message on connection to inform the user in case an incompatible version of the plugin is used.

## 1.0.0-beta.2
- Fixes a potential crash on the progress updater.

## 1.0.0-beta.1
- Full migration to kotlin language.
- Commands are now instantiated on registration.
- Adds empty view for lists without data.
- Changes the widget updateNotification mechanism to avoid previous issues where the widgets failed to updateNotification properly.
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
- Fixes an issue with the widgets that stopped to updateNotification after some time.
- Increases the timeout of the automatic discovery function.
- Adds extra actions on the Incoming call action (Stop and Pause along with Volume reduce).
- Reworks the connection settings detection functionality. (If the network allows it the detection should happen automatically on the application startup)
- Removes the dialogs that used to appear on each new setup.
