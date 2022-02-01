package com.kelsos.mbrc.features.widgets

import android.os.Bundle
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.features.library.PlayingTrack

class BundleData(private val bundle: Bundle) {
  fun isState() = bundle.getBoolean(WidgetUpdater.STATE, false)

  fun isInfo() = bundle.getBoolean(WidgetUpdater.INFO, false)

  fun isCover() = bundle.getBoolean(WidgetUpdater.COVER, false)

  fun cover(): String = bundle.getString(WidgetUpdater.COVER_PATH, "")

  fun state(): PlayerState {
    val state = bundle.getString(WidgetUpdater.PLAYER_STATE, PlayerState.UNDEFINED)
    return PlayerState.fromString(state)
  }

  fun playingTrack(): PlayingTrack {
    return bundle.getParcelable(WidgetUpdater.TRACK_INFO) ?: PlayingTrack()
  }
}
