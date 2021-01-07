package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge

data class ArtistSearchParams(val keyword: String, val albumArtists: Boolean)

class ArtistViewModel(
  private val repository: ArtistRepository,
  settingsManager: SettingsManager,
  searchModel: LibrarySearchModel
) : BaseViewModel<UiMessageBase>() {
  @OptIn(FlowPreview::class)
  val artists: Flow<PagingData<Artist>> = searchModel.search
    .combine(settingsManager.onlyAlbumArtists()) { keyword, albumArtists ->
      ArtistSearchParams(keyword, albumArtists)
    }.flatMapMerge { (keyword, albumArtists) ->
      if (keyword.isEmpty()) {
        if (albumArtists) {
          repository.getAlbumArtistsOnly()
        } else {
          repository.getAll()
        }
      } else {
        repository.search(keyword)
      }
    }.cachedIn(viewModelScope)
}
