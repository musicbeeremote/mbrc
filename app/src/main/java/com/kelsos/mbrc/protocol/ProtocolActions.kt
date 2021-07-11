package com.kelsos.mbrc.protocol

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.net.toUri
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.domain.Repeat
import com.kelsos.mbrc.common.state.models.PlayerStatus
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.common.ui.extensions.md5
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.lyrics.LyricsPayload
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.features.player.NowPlayingTrack
import com.kelsos.mbrc.features.player.cover.CoverPayload
import com.kelsos.mbrc.features.widgets.WidgetUpdater
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingMoveResponse
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingTrackRemoveResponse
import com.kelsos.mbrc.networking.protocol.responses.Position
import com.kelsos.mbrc.ui.navigation.player.LfmRating
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class UpdateLastFm(
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val previousState = appState.playerStatus.firstOrNull() ?: PlayerStatusModel()
    appState.playerStatus.emit(previousState.copy(scrobbling = protocolMessage.asBoolean()))
  }
}

class UpdateLfmRating(
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val previousState = appState.playingTrackRating.firstOrNull() ?: TrackRating()
    val lfmRating = LfmRating.fromString(protocolMessage.data as? String)
    appState.playingTrackRating.emit(previousState.copy(lfmRating = lfmRating))
  }
}

class UpdateLyrics(
  private val mapper: Moshi,
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val adapter = mapper.adapter(LyricsPayload::class.java)
    val payload = adapter.fromJsonValue(protocolMessage.data) ?: return

    val lyrics = if (payload.status == LyricsPayload.SUCCESS) {
      payload.lyrics.replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .trim { it <= ' ' }.split(LYRICS_NEWLINE.toRegex())
        .dropLastWhile(String::isEmpty)
    } else {
      emptyList()
    }

    appState.lyrics.emit(lyrics)
  }

  companion object {
    const val LYRICS_NEWLINE = "\r\n|\n"
  }
}

class UpdateMute(
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val previousState = appState.playerStatus.firstOrNull() ?: PlayerStatusModel()
    appState.playerStatus.emit(previousState.copy(mute = protocolMessage.asBoolean()))
  }
}

class UpdateNowPlayingTrack(
  private val appState: AppState,
  private val updater: WidgetUpdater,
  private val mapper: Moshi,
  private val cache: PlayingTrackCache
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val adapter = mapper.adapter(NowPlayingTrack::class.java)
    val track = adapter.fromJsonValue(protocolMessage.data) ?: return
    val previousState = appState.playingTrack.firstOrNull() ?: PlayingTrack()
    val newState = previousState.copy(
      artist = track.artist,
      title = track.title,
      album = track.album,
      year = track.year,
      path = track.path
    )
    appState.playingTrack.emit(newState)
    updater.updatePlayingTrack(newState)
    cache.persistInfo(newState)
  }
}

class UpdatePlayerStatus(
  private val appState: AppState,
  private val moshi: Moshi
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val adapter = moshi.adapter(PlayerStatus::class.java)
    val status = adapter.fromJsonValue(protocolMessage.data) ?: return
    val previousState = appState.playerStatus.firstOrNull() ?: PlayerStatusModel()
    appState.playerStatus.emit(
      previousState.copy(
        mute = status.mute,
        state = PlayerState.fromString(status.playState),
        repeat = Repeat.fromString(status.repeat),
        shuffle = ShuffleMode.fromString(status.shuffle),
        scrobbling = status.scrobbling,
        volume = status.volume
      )
    )
  }
}

class UpdatePlayState(
  private val appState: AppState,
  private val updater: WidgetUpdater
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val playState = PlayerState.fromString(protocolMessage.data as? String ?: PlayerState.UNDEFINED)
    val previousState = appState.playerStatus.firstOrNull() ?: PlayerStatusModel()
    appState.playerStatus.emit(previousState.copy(state = playState))
    updater.updatePlayState(playState)
  }
}

class UpdatePluginVersionCommand : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val pluginVersion = protocolMessage.data as? String
    Timber.v("plugin reports $pluginVersion")
  }
}

class UpdateRating(
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val rating = protocolMessage.data.toString().toFloatOrNull()
    val previousState = appState.playingTrackRating.firstOrNull() ?: TrackRating()
    appState.playingTrackRating.emit(previousState.copy(rating = rating ?: 0.0f))
  }
}

class UpdateRepeat(
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val repeat = Repeat.fromString((protocolMessage.data as? String) ?: Repeat.NONE)
    val previousState = appState.playerStatus.firstOrNull() ?: PlayerStatusModel()
    appState.playerStatus.emit(previousState.copy(repeat = repeat))
  }
}

class UpdateShuffle(
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val data = ShuffleMode.fromString(protocolMessage.data as? String ?: ShuffleMode.OFF)
    val previousState = appState.playerStatus.firstOrNull() ?: PlayerStatusModel()
    appState.playerStatus.emit(previousState.copy(shuffle = data))
  }
}

class UpdateVolume(
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val volume = protocolMessage.data as Number
    val previousState = appState.playerStatus.firstOrNull() ?: PlayerStatusModel()
    appState.playerStatus.emit(previousState.copy(volume = volume.toInt()))
  }
}

class UpdateCover(
  private val app: Application,
  private val updater: WidgetUpdater,
  private val moshi: Moshi,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers,
  private val appState: AppState
) : ProtocolAction {
  private val coverDir: File = File(app.filesDir, COVER_DIR)

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val adapter = moshi.adapter(CoverPayload::class.java)
    val payload = adapter.fromJsonValue(protocolMessage.data) ?: return
    val previousState = appState.playingTrack.firstOrNull() ?: PlayingTrack()
    if (payload.status == CoverPayload.NOT_FOUND) {
      update(previousState)
    } else if (payload.status == CoverPayload.READY) {
      retrieveCover(previousState)
    }
  }

  private suspend fun retrieveCover(previousState: PlayingTrack) {
    withContext(dispatchers.network) {
      try {
        val response = api.getItem(Protocol.NowPlayingCover, CoverPayload::class)
        val bitmap = getBitmap(response.cover)
        val file = storeCover(bitmap)

        val coverUri = file.toUri().toString()
        update(previousState, coverUri)
      } catch (e: Exception) {
        removeCover(e, previousState)
      }
    }

    Timber.v("Message received for available cover")
    return
  }

  private suspend fun update(
    previousState: PlayingTrack,
    coverUri: String = ""
  ) {
    val newState = previousState.copy(coverUrl = coverUri)
    appState.playingTrack.emit(newState)
    updater.updateCover(coverUri)
  }

  private fun getBitmap(base64: String): Bitmap {
    val decodedImage = Base64.decode(base64, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
    if (bitmap != null) {
      return bitmap
    } else {
      throw RuntimeException("Base64 was not an image")
    }
  }

  private suspend fun removeCover(it: Throwable? = null, previousState: PlayingTrack) {
    clearPreviousCovers(0)

    it?.let {
      Timber.v(it, "Failed to store path")
    }

    update(previousState)
  }

  private fun storeCover(bitmap: Bitmap): File {
    checkIfExists()
    clearPreviousCovers()

    val file = temporaryCover()
    val fileStream = FileOutputStream(file)
    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fileStream)
    fileStream.close()

    val md5 = file.md5()
    val extension = file.extension
    val newFile = File(app.filesDir, "$md5.$extension")
    if (newFile.exists()) {
      file.delete()
      return newFile
    }

    if (success) {
      file.renameTo(newFile)
      Timber.v("file was renamed to ${newFile.absolutePath}")
      return newFile
    } else {
      throw RuntimeException("unable to store cover")
    }
  }

  private fun checkIfExists() {
    if (!coverDir.exists()) {
      coverDir.mkdir()
    }
  }

  private fun clearPreviousCovers(keep: Int = 1) {
    if (!coverDir.exists()) {
      return
    }
    val storedCovers = coverDir.listFiles() ?: return
    storedCovers.sortByDescending(File::lastModified)
    val elementsToKeep = if (storedCovers.size - keep < 0) 0 else storedCovers.size - keep
    storedCovers.takeLast(elementsToKeep).forEach {
      it.delete()
    }
  }

  private fun temporaryCover(): File {
    val file = File(app.cacheDir, TEMP_COVER)
    if (file.exists()) {
      file.delete()
    }
    return file
  }

  companion object {
    const val COVER_DIR = "cover"
    const val TEMP_COVER = "temp_cover.jpg"
    const val JPEG_QUALITY = 100
  }
}

class ProtocolPingHandle(
  private val messageQueue: MessageQueue,
  private var activityChecker: SocketActivityChecker
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    activityChecker.ping()
    messageQueue.queue(SocketMessage.create(Protocol.Pong))
  }
}

class ProtocolPongHandle : ProtocolAction {
  override suspend fun execute(protocolMessage: ProtocolMessage) {
    Timber.d(protocolMessage.data.toString())
  }
}

class UpdateNowPlayingTrackMoved(
  moshi: Moshi,
  dispatchers: AppCoroutineDispatchers,
  private val nowPlayingRepository: NowPlayingRepository
) : ProtocolAction {
  private val scope = CoroutineScope(dispatchers.network)
  private val adapter = moshi.adapter(NowPlayingMoveResponse::class.java)

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    scope.launch {
      val response = adapter.fromJsonValue(protocolMessage.data)
      if (response != null && response.success) {
        nowPlayingRepository.move(from = response.from + 1, to = response.to + 1)
      }
    }
  }
}

class UpdateNowPlayingTrackRemoval(
  moshi: Moshi,
  dispatchers: AppCoroutineDispatchers,
  private val nowPlayingRepository: NowPlayingRepository
) : ProtocolAction {
  private val scope = CoroutineScope(dispatchers.network)
  private val adapter = moshi.adapter(NowPlayingTrackRemoveResponse::class.java)

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    scope.launch {
      val response = adapter.fromJsonValue(protocolMessage.data)
      if (response != null && response.success) {
        nowPlayingRepository.remove(response.index + 1)
      }
    }
  }
}

class UpdatePlaybackPositionCommand(
  private val moshi: Moshi,
  private val appState: AppState
) : ProtocolAction {

  override suspend fun execute(protocolMessage: ProtocolMessage) {
    val adapter = moshi.adapter(Position::class.java)
    val response = adapter.fromJsonValue(protocolMessage.data) ?: return

    appState.playingPosition.emit(
      PlayingPosition(
        response.current,
        response.total
      )
    )
    val track = appState.playingTrack.first()
    if (track.duration != response.total) {
      appState.playingTrack.emit(track.copy(duration = response.total))
    }
  }
}

class ProtocolVersionUpdate : ProtocolAction {
  override suspend fun execute(protocolMessage: ProtocolMessage) {
    Timber.v(protocolMessage.data.toString())
  }
}

private fun ProtocolMessage.asBoolean(): Boolean {
  return data as? Boolean ?: false
}
