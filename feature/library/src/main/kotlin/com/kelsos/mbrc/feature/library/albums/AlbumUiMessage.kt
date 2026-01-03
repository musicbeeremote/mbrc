package com.kelsos.mbrc.feature.library.albums

import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.core.data.library.album.Album

sealed class AlbumUiMessage : UiMessageBase {
  data class OpenAlbumTracks(val album: Album) : AlbumUiMessage()

  data class QueueSuccess(val tracksCount: Int) : AlbumUiMessage()

  object QueueFailed : AlbumUiMessage()

  object NetworkUnavailable : AlbumUiMessage()
}
