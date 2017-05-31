package com.kelsos.mbrc.preferences

import android.support.annotation.StringDef
import io.reactivex.Single
import java.util.*

interface SettingsManager {

  fun shouldDisplayOnlyAlbumArtists(): Single<Boolean>
  fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
  fun shouldShowChangeLog(): Single<Boolean>
  fun isNotificationControlEnabled(): Boolean
  fun isPluginUpdateCheckEnabled(): Boolean

  @CallAction fun getCallAction(): String

  @StringDef(NONE,
      PAUSE,
      STOP,
      REDUCE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class CallAction

  companion object {
    const val NONE = "none"
    const val PAUSE = "pause"
    const val STOP = "stop"
    const val REDUCE = "reduce"
  }

  fun getLastUpdated(): Date
  fun setLastUpdated(lastChecked: Date)
  fun getClientId(): String
}
