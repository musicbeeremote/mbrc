package com.kelsos.mbrc.repository

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.domain.TrackInfo
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import javax.inject.Inject

class ModelCacheImpl
@Inject
constructor(
  private val mapper: ObjectMapper,
  private val context: Application,
  private val dispatchers: AppDispatchers
) : ModelCache {

  private fun writeTrackInfo(infoFile: File, trackInfo: TrackInfo) {
    mapper.writeValue(infoFile, trackInfo)
  }

  private fun readTrackInfo(infoFile: File) =
    mapper.readValue(infoFile, TrackInfo::class.java)

  override suspend fun persistInfo(trackInfo: TrackInfo) = withContext(dispatchers.io) {
    val infoFile = File(context.filesDir, TRACK_INFO)
    if (infoFile.exists()) {
      infoFile.delete()
    }
    writeTrackInfo(infoFile, trackInfo)
  }

  override suspend fun restoreInfo(): TrackInfo = withContext(dispatchers.io) {
    val infoFile = File(context.filesDir, TRACK_INFO)
    if (infoFile.exists()) {
      return@withContext readTrackInfo(infoFile)
    }
    throw FileNotFoundException()
  }

  override suspend fun persistCover(cover: String) {
    withContext(dispatchers.io) {
      val coverFile = File(context.filesDir, COVER_INFO)
      if (coverFile.exists()) {
        coverFile.delete()
      }

      writeCover(coverFile, cover)
    }
  }

  private fun writeCover(coverFile: File, cover: String) {
    FileWriter(coverFile).use { fileWriter: FileWriter ->
      BufferedWriter(fileWriter).use { bufferedWriter ->
        bufferedWriter.write(cover)
        bufferedWriter.flush()
      }
    }
  }

  override suspend fun restoreCover(): String {
    return withContext(dispatchers.io) {
      val coverFile = File(context.filesDir, COVER_INFO)
      if (coverFile.exists()) {
        return@withContext readCover(coverFile)
      }
      return@withContext ""
    }
  }

  private fun readCover(coverFile: File): String {
    FileReader(coverFile).use { fileReader ->
      BufferedReader(fileReader).use { bufferedReader ->
        return bufferedReader.readLine()
      }
    }
  }

  companion object {
    const val TRACK_INFO = "track.json"
    const val COVER_INFO = "cover.txt"
  }
}

interface ModelCache {
  suspend fun persistInfo(trackInfo: TrackInfo)
  suspend fun restoreInfo(): TrackInfo
  suspend fun persistCover(cover: String)
  suspend fun restoreCover(): String
}
