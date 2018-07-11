package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppRxSchedulers
import kotlinx.coroutines.experimental.launch


class BrowseArtistViewModel(
  private val repository: ArtistRepository,
  private val settingsManager: SettingsManager,
  private val appRxSchedulers: AppRxSchedulers
) : ViewModel() {

  private lateinit var artists: LiveData<PagedList<ArtistEntity>>
  private lateinit var indexes: LiveData<List<String>>

  fun reload() {
    launch { repository.getRemote() }

  }
}