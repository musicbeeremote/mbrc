package com.kelsos.mbrc.networking.protocol.commands

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerStatus
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.content.lyrics.LyricsPayload
import com.kelsos.mbrc.content.nowplaying.NowPlayingTrack
import com.kelsos.mbrc.content.nowplaying.cover.CoverPayload
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.extensions.getDimens
import com.kelsos.mbrc.extensions.md5
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.ProtocolMessage
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.platform.widgets.UpdateWidgets
import com.kelsos.mbrc.ui.navigation.player.LfmRating
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.squareup.moshi.Moshi
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resumeWithException

class UpdateLastFm(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val scrobble = message.data as? Boolean ?: false
    playerStatusLiveDataProvider.update {
      copy(scrobbling = scrobble)
    }
  }
}

class UpdateLfmRating(
  private val trackRatingLiveDataProvider: TrackRatingLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val lfmRating = when (message.data as? String) {
      "Love" -> LfmRating.LOVED
      "Ban" -> LfmRating.BANNED
      else -> LfmRating.NORMAL
    }

    trackRatingLiveDataProvider.update {
      copy(lfmRating = lfmRating)
    }
  }
}

class UpdateLyrics(
  private val mapper: Moshi,
  private val lyricsLiveDataProvider: LyricsLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = mapper.adapter(LyricsPayload::class.java)
    val payload = adapter.fromJsonValue(message.data) ?: return

    val lyrics = if (payload.status == LyricsPayload.SUCCESS) {
      payload.lyrics.replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .trim { it <= ' ' }.split(LYRICS_NEWLINE.toRegex())
        .dropLastWhile(String::isEmpty)
    } else {
      emptyList()
    }

    lyricsLiveDataProvider.update(lyrics)
  }

  companion object {
    const val LYRICS_NEWLINE = "\r\n|\n"
  }
}

class UpdateMute(
  private val statusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val mute = message.data as? Boolean ?: false
    statusLiveDataProvider.update { copy(mute = mute) }
  }
}

class UpdateNowPlayingTrack(
  private val playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val context: Application,
  private val mapper: Moshi
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = mapper.adapter(NowPlayingTrack::class.java)
    val track = adapter.fromJsonValue(message.data) ?: return

    playingTrackLiveDataProvider.update {
      copy(
        artist = track.artist,
        title = track.title,
        album = track.album,
        year = track.year,
        path = track.path
      )
    }

    playingTrackLiveDataProvider.getValue()?.run {
      UpdateWidgets.updateTrackInfo(context, this)
    }
  }
}

class UpdatePlayerStatus(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val moshi: Moshi
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(PlayerStatus::class.java)
    val status = adapter.fromJsonValue(message.data) ?: return

    playerStatusLiveDataProvider.update {
      copy(
        mute = status.mute,
        state = status.playState,
        repeat = status.repeat,
        shuffle = status.shuffle,
        scrobbling = status.scrobbling,
        volume = status.volume
      )
    }
  }
}

class UpdatePlayState(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val context: Application
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val playState = message.data as? String ?: PlayerState.UNDEFINED

    playerStatusLiveDataProvider.update {
      copy(state = playState)
    }

    UpdateWidgets.updatePlaystate(context, playState)
  }
}

class UpdatePluginVersionCommand : ICommand {

  override fun execute(message: ProtocolMessage) {
    val pluginVersion = message.data as? String
    Timber.v("plugin reports $pluginVersion")
  }
}

class UpdateRating(
  private val ratingLiveDataProvider: TrackRatingLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val rating = message.data.toString().toFloatOrNull()

    ratingLiveDataProvider.update {
      copy(rating = rating ?: 0.0f)
    }
  }
}

class UpdateRepeat(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val repeat = (message.data as? String)?.toRepeat() ?: Repeat.NONE

    playerStatusLiveDataProvider.update {
      copy(repeat = repeat)
    }
  }
}

class UpdateShuffle(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val data = message.data as? String ?: ShuffleMode.OFF

    playerStatusLiveDataProvider.update {
      copy(shuffle = data)
    }
  }
}

class UpdateVolume(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val volume = message.data as Number
    playerStatusLiveDataProvider.update {
      copy(volume = volume.toInt())
    }
  }
}

@Repeat.Mode
private fun String.toRepeat(): String {
  return when {
    Protocol.ALL.equals(this, ignoreCase = true) -> Repeat.ALL
    Protocol.ONE.equals(this, ignoreCase = true) -> Repeat.ONE
    else -> Repeat.NONE
  }
}

class UpdateCover(
  private val context: Application,
  private val moshi: Moshi,
  private val coverService: ApiBase,
  private val cache: PlayingTrackCache,
  appDispatchers: AppCoroutineDispatchers
) : ICommand {
  private val coverDir: File = File(context.filesDir, COVER_DIR)
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + appDispatchers.network)

  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(CoverPayload::class.java)
    val payload = adapter.fromJsonValue(message.data)

    if (payload?.status == CoverPayload.NOT_FOUND) {
      UpdateWidgets.updateCover(context)
    } else if (payload?.status == CoverPayload.READY) {
      scope.launch {
        retrieveCover()
      }
    }
  }

  private suspend fun retrieveCover() {
    val (status, cover) = try {
      coverService.getItem(Protocol.NowPlayingCover, CoverPayload::class)
    } catch (e: Exception) {
      return
    }

    if (status != CoverPayload.SUCCESS) {
      removeCover()
      return
    }

    try {
      val bitmap = getBitmap(cover)
      val file = storeCover(bitmap)
      val pre = prefetch(file)
      val path = pre.absolutePath
      savePath(path)
      UpdateWidgets.updateCover(context, path)
    } catch (e: Exception) {
      removeCover(e)
    }

    Timber.v("Message received for available cover")
    return
  }

  private fun savePath(path: String) {
    scope.launch {
      try {
        cache.persistCover(path)
        Timber.v("Playing track cover $path successfully persisted")
      } catch (e: Exception) {
        Timber.v(e, "Failed to persist the playing track cover")
      }
    }
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
  }

  private fun storeCover(bitmap: Bitmap): File {
    checkIfExists()
    clearPreviousCovers()
    val file = temporaryCover()
    val fileStream = FileOutputStream(file)
    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
    fileStream.close()
    if (success) {
      val md5 = file.md5()
      val extension = file.extension
      val newFile = File(coverDir, "$md5.$extension")
      file.renameTo(newFile)
      Timber.v("file was renamed to %s", newFile.absolutePath)
      return newFile
    } else {
      throw RuntimeException("unable to store cover")
    }
  }

  private suspend fun prefetch(newFile: File): File = suspendCancellableCoroutine { cont ->
    val dimens = context.getDimens()
    Picasso.get().load(newFile)
      .config(Bitmap.Config.RGB_565)
      .resize(dimens, dimens)
      .centerCrop()
      .fetch(object : Callback {
        override fun onSuccess() {
          cont.resume(newFile, onCancellation = {})
        }

        override fun onError(e: Exception) {
          cont.resumeWithException(e)
        }
      })
  }

  private fun checkIfExists() {
    if (!coverDir.exists()) {
      coverDir.mkdir()
    }
  }

  private fun clearPreviousCovers(keep: Int = 2) {
    coverDir.listFiles()?.run {
      sortByDescending(File::lastModified)
      Timber.v(size.toString())
      val toDelete = if (size - keep < 0) 0 else size - keep
      Timber.v("deleting last $toDelete items")
      takeLast(toDelete).forEach {
        it.delete()
      }
    }
  }

  private fun temporaryCover(): File {
    val file = File(context.cacheDir, TEMP_COVER)
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
