package com.kelsos.mbrc.content.activestatus.livedata

import javax.inject.Inject

interface LyricsLiveDataProvider: LiveDataProvider<List<String>>

class LyricsLiveDataProviderImpl
@Inject
constructor(): LyricsLiveDataProvider, BaseLiveDataProvider<List<String>>() {
  init {
    update(emptyList())
  }
}