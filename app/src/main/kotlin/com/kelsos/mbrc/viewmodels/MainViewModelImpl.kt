package com.kelsos.mbrc.viewmodels

import android.graphics.Bitmap
import android.support.annotation.IntRange
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition

class MainViewModelImpl : MainViewModel {
  override var trackInfo: TrackInfo = TrackInfo()
  override var trackCover: Bitmap? = null
  override var position: TrackPosition = TrackPosition()
  override var rating: Float = 0.toFloat()
  @Shuffle.State override var shuffle: String = Shuffle.UNDEF
  @PlayerState.State override var playState: String = PlayerState.UNDEFINED
  @Repeat.Mode override var repeat: String = Repeat.UNDEFINED
  override var isMuted: Boolean = false
  @IntRange(from = -1, to = 100) override var volume: Int = 0
  override var isLoaded: Boolean = false
    private set

  init {
    trackInfo = TrackInfo()
    trackCover = null
    position = TrackPosition(0, 0)
    rating = 0f
    shuffle = Shuffle.UNDEF
    playState = PlayerState.UNDEFINED
    repeat = Repeat.UNDEFINED
    isMuted = false
    volume = -1
    isLoaded = false
  }

  override fun loadComplete() {
    isLoaded = true
  }
}
