package com.kelsos.mbrc.ui.views

import android.graphics.Bitmap

import com.kelsos.mbrc.annotations.PlayerState

interface MiniControlView {
  fun updatePlayerState(@PlayerState.State state: String)
  fun updateTrack(artist: String, title: String)
  fun updateCover(cover: Bitmap?)
}
