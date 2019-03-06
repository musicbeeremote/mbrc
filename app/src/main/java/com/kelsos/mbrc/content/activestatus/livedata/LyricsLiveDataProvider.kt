package com.kelsos.mbrc.content.activestatus.livedata

interface LyricsLiveDataProvider : LiveDataProvider<List<String>>

class LyricsLiveDataProviderImpl : LyricsLiveDataProvider, BaseLiveDataProvider<List<String>>() {
  init {
    update(emptyList())
  }
}