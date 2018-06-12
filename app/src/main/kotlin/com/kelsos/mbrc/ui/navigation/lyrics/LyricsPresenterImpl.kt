package com.kelsos.mbrc.ui.navigation.lyrics

import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProvider
import com.kelsos.mbrc.mvp.BasePresenter
import javax.inject.Inject

@LyricsFragment.Presenter
class LyricsPresenterImpl
@Inject
constructor(
  lyricsLiveDataProvider: LyricsLiveDataProvider
) : BasePresenter<LyricsView>(), LyricsPresenter {
  init {
    lyricsLiveDataProvider.get().observe(this) { lyrics ->
      if (lyrics == null) {
        return@observe
      }

      view().updateLyrics(lyrics)
    }
  }
}
