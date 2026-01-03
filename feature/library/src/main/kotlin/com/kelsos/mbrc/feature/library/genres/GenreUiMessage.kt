package com.kelsos.mbrc.feature.library.genres

import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.core.data.library.genre.Genre

sealed class GenreUiMessage : UiMessageBase {
  data class OpenArtists(val genre: Genre) : GenreUiMessage()

  data class QueueSuccess(val tracksCount: Int) : GenreUiMessage()

  object QueueFailed : GenreUiMessage()

  object NetworkUnavailable : GenreUiMessage()
}
