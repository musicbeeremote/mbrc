package com.kelsos.mbrc.viewmodels

import android.graphics.Bitmap
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition

interface MainViewModel {
  var shuffle: String

  var playState: String

  var repeat: String

  var isMuted: Boolean

  var trackInfo: TrackInfo

  var trackCover: Bitmap?

  var position: TrackPosition

  var rating: Float

  var volume: Int

  val isLoaded: Boolean

  fun loadComplete()
}
