package com.kelsos.mbrc.repository

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.domain.TrackInfo
import rx.Completable
import rx.Single
import java.io.*
import javax.inject.Inject

class ModelCacheImpl
@Inject constructor(private val mapper: ObjectMapper,
                    private val context: Application) : ModelCache {

  override fun persistInfo(trackInfo: TrackInfo): Completable {
    return Completable.fromCallable {
      val infoFile = File(context.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        infoFile.delete()
      }
      mapper.writeValue(infoFile, trackInfo)
    }
  }

  override fun restoreInfo(): Single<TrackInfo> {
    return Single.fromCallable {
      val infoFile = File(context.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        return@fromCallable mapper.readValue(infoFile, TrackInfo::class.java)
      }
      throw FileNotFoundException()
    }
  }

  override fun persistCover(cover: String): Completable {
    return Completable.fromCallable {
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

interface ModelCache {
  fun persistInfo(trackInfo: TrackInfo): Completable
  fun restoreInfo(): Single<TrackInfo>
  fun persistCover(cover: String): Completable
  fun restoreCover(): Single<String>
}
