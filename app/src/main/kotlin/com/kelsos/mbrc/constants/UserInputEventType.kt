package com.kelsos.mbrc.constants

import android.support.annotation.StringDef

object UserInputEventType {
  const val StartConnection = "StartConnection"
  const val SettingsChanged = "SettingsChanged"
  const val ResetConnection = "ResetConnection"
  const val CancelNotification = "CancelNotification"
  const val StartDiscovery = "StartDiscovery"
  const val KeyVolumeUp = "KeyVolumeUp"
  const val KeyVolumeDown = "KeyVolumeDown"
  val TerminateConnection = "TerminateConnection"

  @StringDef(StartConnection,
      SettingsChanged,
      ResetConnection,
      CancelNotification,
      StartDiscovery,
      KeyVolumeDown,
      KeyVolumeUp)
  @Retention(AnnotationRetention.SOURCE)
  annotation class Event
}
