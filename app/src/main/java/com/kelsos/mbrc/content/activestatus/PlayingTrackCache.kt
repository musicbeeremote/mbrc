package com.kelsos.mbrc.content.activestatus

import android.app.Application
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import okio.Okio
import java.io.File
import java.nio.charset.Charset


class PlayingTrackCacheImpl

constructor(
  private val mapper: Moshi,
  private val appContext: Application,
  private val appCoroutineDispatchers: AppCoroutineDispatchers
) : PlayingTrackCache {

  private val adapter by lazy { mapper.adapter(PlayingTrack::class.java) }

  override suspend fun persistInfo(track: PlayingTrack) {
    withContext(appCoroutineDispatchers.disk) {
      val infoFile = File(appContext.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        infoFile.delete()
      }
      adapter.toJson(Okio.buffer(Okio.sink(infoFile)), track)
    }
  }

  override suspend fun restoreInfo(): PlayingTrack? {
    return withContext(appCoroutineDispatchers.disk) {
      val infoFile = File(appContext.filesDir, TRACK_INFO)
      return@withContext if (infoFile.exists()) {
        adapter.fromJson(Okio.buffer(Okio.source(infoFile)))
      } else {
        null
      }
    }
  }

  override suspend fun persistCover(cover: String) {
    withContext(appCoroutineDispatchers.disk) {
      val coverFile = File(appContext.filesDir, COVER_INFO)
      if (coverFile.exists()) {
        coverFile.delete()
      }

      Okio.buffer(Okio.sink(coverFile)).use {
        it.write(coverFile.readBytes())
        it.emit()
        it.close()
      }
    }
  }

  override suspend fun restoreCover(): String? {
    return withContext(appCoroutineDispatchers.disk) {
      val coverFile = File(appContext.filesDir, COVER_INFO)
      return@withContext if (!coverFile.exists()) {
        null
      } else {
        Okio.buffer(Okio.source(coverFile)).use {
          return@use it.readString(Charset.forName("UTF-8"))
        }
      }
    }
  }

  companion object {
    const val TRACK_INFO = "track.json"
    const val COVER_INFO = "cover.txt"
  }
}

interface PlayingTrackCache {
  suspend fun persistInfo(track: PlayingTrack)
  suspend fun restoreInfo(): PlayingTrack?
  suspend fun persistCover(cover: String)
  suspend fun restoreCover(): String?
}