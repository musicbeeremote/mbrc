package com.kelsos.mbrc.protocol

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.net.toUri
import com.kelsos.mbrc.common.ui.extensions.md5
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerStatus
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingState
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.features.lyrics.LyricsPayload
import com.kelsos.mbrc.features.lyrics.LyricsState
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.features.player.NowPlayingTrack
import com.kelsos.mbrc.features.player.cover.CoverModel
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class UpdateLastFm(
  private val state: PlayerStatusState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    state.set {
      copy(scrobbling = protocolMessage.asBoolean())
    }
  }
}

class UpdateLfmRating(
  private val state: TrackRatingState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    state.set {
      copy(lfmRating = LfmRating.fromString(protocolMessage.data as? String))
    }
  }
}

class UpdateLyrics(
  private val mapper: Moshi,
  private val state: LyricsState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
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

    state.set(lyrics)
  }

  companion object {
    const val LYRICS_NEWLINE = "\r\n|\n"
  }
}

class UpdateMute(
  private val state: PlayerStatusState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    state.set { copy(mute = protocolMessage.asBoolean()) }
  }
}

class UpdateNowPlayingTrack(
  private val state: PlayingTrackState,
  private val updater: WidgetUpdater,
  private val mapper: Moshi
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val adapter = mapper.adapter(NowPlayingTrack::class.java)
    val track = adapter.fromJsonValue(protocolMessage.data) ?: return

    state.set {
      copy(
        artist = track.artist,
        title = track.title,
        album = track.album,
        year = track.year,
        path = track.path
      )
    }

    state.getValue()?.run {
      updater.updatePlayingTrack(this)
    }
  }
}

class UpdatePlayerStatus(
  private val state: PlayerStatusState,
  private val moshi: Moshi
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val adapter = moshi.adapter(PlayerStatus::class.java)
    val status = adapter.fromJsonValue(protocolMessage.data) ?: return

    state.set {
      copy(
        mute = status.mute,
        state = PlayerState.fromString(status.playState),
        repeat = Repeat.fromString(status.repeat),
        shuffle = ShuffleMode.fromString(status.shuffle),
        scrobbling = status.scrobbling,
        volume = status.volume
      )
    }
  }
}

class UpdatePlayState(
  private val state: PlayerStatusState,
  private val updater: WidgetUpdater
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val playState = PlayerState.fromString(protocolMessage.data as? String ?: PlayerState.UNDEFINED)

    state.set {
      copy(state = playState)
    }
    updater.updatePlayState(playState)
  }
}

class UpdatePluginVersionCommand : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val pluginVersion = protocolMessage.data as? String
    Timber.v("plugin reports $pluginVersion")
  }
}

class UpdateRating(
  private val state: TrackRatingState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val rating = protocolMessage.data.toString().toFloatOrNull()

    state.set {
      copy(rating = rating ?: 0.0f)
    }
  }
}

class UpdateRepeat(
  private val state: PlayerStatusState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val repeat = Repeat.fromString((protocolMessage.data as? String) ?: Repeat.NONE)

    state.set {
      copy(repeat = repeat)
    }
  }
}

class UpdateShuffle(
  private val state: PlayerStatusState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val data = ShuffleMode.fromString(protocolMessage.data as? String ?: ShuffleMode.OFF)

    state.set {
      copy(shuffle = data)
    }
  }
}

class UpdateVolume(
  private val state: PlayerStatusState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val volume = protocolMessage.data as Number
    state.set {
      copy(volume = volume.toInt())
    }
  }
}

class UpdateCover(
  private val app: Application,
  private val updater: WidgetUpdater,
  private val moshi: Moshi,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers,
  private val coverModel: CoverModel,
  private val playingTrackLiveDataProvider: PlayingTrackState
) : ProtocolAction {
  private val coverDir: File = File(app.filesDir, COVER_DIR)
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.network)

  init {
    scope.launch(dispatchers.io) {
      playingTrackLiveDataProvider.set {
        copy(coverUrl = coverModel.coverPath)
      }
    }
  }

  override fun execute(protocolMessage: ProtocolMessage) {
    val adapter = moshi.adapter(CoverPayload::class.java)
    val payload = adapter.fromJsonValue(protocolMessage.data) ?: return

    if (payload.status == CoverPayload.NOT_FOUND) {
      playingTrackLiveDataProvider.set { copy(coverUrl = "") }
      updater.updateCover("")
    } else if (payload.status == CoverPayload.READY) {
      scope.launch(dispatchers.io) {
        retrieveCover()
      }
    }
  }

  private suspend fun retrieveCover() {
    withContext(dispatchers.network) {
      try {
        val response = api.getItem(Protocol.NowPlayingCover, CoverPayload::class)
        val bitmap = getBitmap(response.cover)
        val file = storeCover(bitmap)

        playingTrackLiveDataProvider.set {
          val coverUri = file.toUri().toString()
          coverModel.coverPath = coverUri
          copy(coverUrl = coverUri)
        }
        updater.updateCover(file.absolutePath)
      } catch (e: Exception) {
        removeCover(e)
      }
    }

    Timber.v("Message received for available cover")
    return
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

  private fun removeCover(it: Throwable? = null) {
    clearPreviousCovers(0)

    it?.let {
      Timber.v(it, "Failed to store path")
    }

    playingTrackLiveDataProvider.set {
      copy(coverUrl = "")
    }
  }

  private fun storeCover(bitmap: Bitmap): File {
    checkIfExists()
    clearPreviousCovers()

    val file = temporaryCover()
    val fileStream = FileOutputStream(file)
    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
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
  }
}

class ProtocolPingHandle(
  private val messageQueue: MessageQueue,
  private var activityChecker: SocketActivityChecker
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    activityChecker.ping()
    messageQueue.queue(SocketMessage.create(Protocol.Pong))
  }
}

class ProtocolPongHandle : ProtocolAction {
  override fun execute(protocolMessage: ProtocolMessage) {
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

  override fun execute(protocolMessage: ProtocolMessage) {
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

  override fun execute(protocolMessage: ProtocolMessage) {
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
  private val state: TrackPositionState
) : ProtocolAction {

  override fun execute(protocolMessage: ProtocolMessage) {
    val adapter = moshi.adapter(Position::class.java)
    val response = adapter.fromJsonValue(protocolMessage.data) ?: return

    state.set(
      PlayingPosition(
        response.current,
        response.total
      )
    )
  }
}

class ProtocolVersionUpdate() : ProtocolAction {
  override fun execute(protocolMessage: ProtocolMessage) {
    Timber.v(protocolMessage.data.toString())
  }
}

private fun ProtocolMessage.asBoolean(): Boolean {
  return data as? Boolean ?: false
}
