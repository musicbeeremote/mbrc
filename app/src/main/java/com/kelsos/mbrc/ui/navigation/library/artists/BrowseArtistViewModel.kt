package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class BrowseArtistViewModel(
  private val repository: ArtistRepository,
  private val settingsManager: SettingsManager,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val viewModelJob: Job = Job()
  private val networkScope = CoroutineScope(dispatchers.network + viewModelJob)

  private lateinit var artists: LiveData<PagedList<ArtistEntity>>
  private lateinit var indexes: LiveData<List<String>>

  fun reload() {
    networkScope.launch { repository.getRemote() }
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }
}