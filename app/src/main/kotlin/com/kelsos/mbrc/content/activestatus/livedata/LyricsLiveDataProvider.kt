package com.kelsos.mbrc.content.activestatus.livedata



interface LyricsLiveDataProvider: LiveDataProvider<List<String>>

class LyricsLiveDataProviderImpl

constructor(): LyricsLiveDataProvider, BaseLiveDataProvider<List<String>>() {
  init {
    update(emptyList())
  }
}