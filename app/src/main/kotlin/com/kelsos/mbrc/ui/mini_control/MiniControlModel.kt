package com.kelsos.mbrc.ui.mini_control

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.extensions.empty

class MiniControlModel {
  private var cover: Bitmap? = null
  private var title: String = String.empty
  private var artist: String = String.empty
  @State private var playerState: String = PlayerState.UNDEFINED

  fun setCover(cover: Bitmap?) {
    this.cover = cover
  }

  fun setTitle(title: String) {
    this.title = title
  }

  fun setArtist(artist: String) {
    this.artist = artist
  }

  fun setPlayerState(@State playerState: String) {
    this.playerState = playerState
  }
}
