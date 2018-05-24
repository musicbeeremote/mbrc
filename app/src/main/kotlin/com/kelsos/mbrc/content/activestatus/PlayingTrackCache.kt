package com.kelsos.mbrc.content.activestatus

import android.app.Application
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.squareup.moshi.Moshi
import io.reactivex.Completable
import io.reactivex.Single
import okio.Okio
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import javax.inject.Inject

class PlayingTrackCacheImpl
@Inject
constructor(
  private val mapper: Moshi,
  private val context: Application
) : PlayingTrackCache {

  private val adapter by lazy { mapper.adapter(PlayingTrackModel::class.java) }

  override fun persistInfo(track: PlayingTrackModel): Completable {
    return Completable.fromCallable {
      val infoFile = File(context.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        infoFile.delete()
      }
      adapter.toJson(Okio.buffer(Okio.sink(infoFile)), track)
    }
  }

  override fun restoreInfo(): Single<PlayingTrackModel> {
    return Single.fromCallable {
      val infoFile = File(context.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        return@fromCallable adapter.fromJson(Okio.buffer(Okio.source(infoFile)))
      }
      throw FileNotFoundException()
    }
  }

  override fun persistCover(cover: String): Completable = Completable.fromCallable {
    val coverFile = File(context.filesDir, COVER_INFO)
    if (coverFile.exists()) {
      coverFile.delete()
    }

    val writer = FileWriter(coverFile)
    val bufferedWriter = BufferedWriter(writer)
    bufferedWriter.write(cover)
    bufferedWriter.flush()
    bufferedWriter.close()
    writer.close()
  }

  override fun restoreCover(): Single<String> {
    return Single.fromCallable {
      val coverFile = File(context.filesDir, COVER_INFO)
      if (coverFile.exists()) {
        val reader = FileReader(coverFile)
        val bufferedReader = BufferedReader(reader)
        val cover = bufferedReader.readLine()
        bufferedReader.close()
        reader.close()
        return@fromCallable cover
      }
      return@fromCallable ""
    }
  }

  companion object {
    const val TRACK_INFO = "track.json"
    const val COVER_INFO = "cover.txt"
  }
}

interface PlayingTrackCache {
  fun persistInfo(track: PlayingTrackModel): Completable
  fun restoreInfo(): Single<PlayingTrackModel>
  fun persistCover(cover: String): Completable
  fun restoreCover(): Single<String>
}