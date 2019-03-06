package com.kelsos.mbrc.networking.protocol.commands

import android.app.Application
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerStatus
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.content.lyrics.LyricsPayload
import com.kelsos.mbrc.content.nowplaying.NowPlayingTrack
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.ProtocolMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.platform.widgets.UpdateWidgets
import com.kelsos.mbrc.ui.navigation.player.LfmRating
import com.squareup.moshi.Moshi
import timber.log.Timber

class UpdateLastFm

constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val scrobble = message.data as? Boolean ?: false
    playerStatusLiveDataProvider.update {
      copy(scrobbling = scrobble)
    }
  }
}

class UpdateLfmRating

constructor(
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

class UpdateLyrics

constructor(
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

class UpdateMute

constructor(
  private val statusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val mute = message.data as? Boolean ?: false
    statusLiveDataProvider.update { copy(mute = mute) }
  }
}

class UpdateNowPlayingTrack

constructor(
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

class UpdatePlayerStatus

constructor(
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

class UpdatePlayState

constructor(
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

class UpdatePluginVersionCommand

  : ICommand {

  override fun execute(message: ProtocolMessage) {
    val pluginVersion = message.data as? String
    Timber.v("plugin reports $pluginVersion")
  }
}

class UpdateRating

constructor(
  private val ratingLiveDataProvider: TrackRatingLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val rating = message.data.toString().toFloatOrNull()

    ratingLiveDataProvider.update {
      copy(rating = rating ?: 0.0f)
    }
  }
}

class UpdateRepeat

constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val repeat = (message.data as? String)?.toRepeat() ?: Repeat.NONE

    playerStatusLiveDataProvider.update {
      copy(repeat = repeat)
    }
  }
}

class UpdateShuffle

constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val data = message.data as? String ?: ShuffleMode.OFF

    playerStatusLiveDataProvider.update {
      copy(shuffle = data)
    }
  }
}

class UpdateVolume

constructor(
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