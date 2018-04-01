package com.kelsos.mbrc.content.activestatus

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import io.reactivex.Completable
import io.reactivex.Single
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
  private val mapper: ObjectMapper,
  private val context: Application
) : PlayingTrackCache {

  override fun persistInfo(track: PlayingTrackModel): Completable {
    return Completable.fromCallable {
      val infoFile = File(context.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        infoFile.delete()
      }
      mapper.writeValue(infoFile, track)
    }
  }

  override fun restoreInfo(): Single<PlayingTrackModel> {
    return Single.fromCallable {
      val infoFile = File(context.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        return@fromCallable mapper.readValue(infoFile, PlayingTrackModel::class.java)
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