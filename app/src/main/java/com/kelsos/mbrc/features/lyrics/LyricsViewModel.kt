package com.kelsos.mbrc.features.lyrics

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.common.state.AppState
import kotlinx.coroutines.flow.Flow

class LyricsViewModel(appState: AppState) : ViewModel() {
  val lyrics: Flow<List<String>> = appState.lyrics
}
