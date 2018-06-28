package com.kelsos.mbrc.metrics

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import timber.log.Timber

class SyncMetricsImpl : SyncMetrics {

  private val traces = HashMap<String, Trace>()
  private val times = HashMap<String, Long>()

  private val firebasePerformance by lazy { FirebasePerformance.getInstance() }

  private fun now() = System.currentTimeMillis()

  override fun librarySyncComplete(metrics: SyncedData) {
    traces.remove(LIBRARY_SYNC)?.run {
      putMetric(GENRES, metrics.genres)
      putMetric(ARTISTS, metrics.artists)
      putMetric(ALBUMS, metrics.albums)
      putMetric(TRACKS, metrics.tracks)
      putMetric(PLAYLISTS, metrics.playlists)
      putAttribute(SUCCESS, true.toString())
      stop()
    }
    times.remove(LIBRARY_SYNC)?.run {
      Timber.v("library sync duration: ${now() - this} ms")
    }
  }

  override fun librarySyncStarted() {
    firebasePerformance.newTrace(LIBRARY_SYNC).run {
      start()
      traces[LIBRARY_SYNC] = this
    }

    times[LIBRARY_SYNC] = now()
  }

  override fun librarySyncFailed() {
    traces.remove(LIBRARY_SYNC)?.run {
      putAttribute(SUCCESS, false.toString())
      stop()
    }
    times.remove(LIBRARY_SYNC)
  }

  companion object {
    const val LIBRARY_SYNC = "library_sync"
    const val GENRES = "genres"
    const val ARTISTS = "artists"
    const val ALBUMS = "albums"
    const val TRACKS = "tracks"
    const val PLAYLISTS = "playlists"
    const val SUCCESS = "success"
  }
}
