package com.kelsos.mbrc.feature.library.artists

import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.core.data.library.artist.Artist

sealed class ArtistUiMessage : UiMessageBase {
  data class OpenArtistAlbums(val artist: Artist) : ArtistUiMessage()

  object QueueFailed : ArtistUiMessage()

  data class QueueSuccess(val tracksCount: Int) : ArtistUiMessage()

  object NetworkUnavailable : ArtistUiMessage()
}
