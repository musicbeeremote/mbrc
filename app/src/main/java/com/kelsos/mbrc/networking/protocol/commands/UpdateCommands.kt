package com.kelsos.mbrc.networking.protocol.commands

import android.app.Application
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerStatus
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.livedata.LyricsState
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingState
import com.kelsos.mbrc.content.lyrics.LyricsPayload
import com.kelsos.mbrc.content.nowplaying.NowPlayingTrack
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.ProtocolMessage
import com.kelsos.mbrc.networking.SocketActivityChecker
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingMoveResponse
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingTrackRemoveResponse
import com.kelsos.mbrc.networking.protocol.responses.Position
import com.kelsos.mbrc.platform.widgets.UpdateWidgets
import com.kelsos.mbrc.ui.navigation.player.LfmRating
import com.squareup.moshi.Moshi
import timber.log.Timber

class UpdateLastFm(
  private val playerStatusLiveDataProvider: PlayerStatusState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val scrobble = message.data as? Boolean ?: false
    playerStatusLiveDataProvider.set {
      copy(scrobbling = scrobble)
    }
  }
}

class UpdateLfmRating(
  private val trackRatingLiveDataProvider: TrackRatingState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val lfmRating = when (message.data as? String) {
      "Love" -> LfmRating.LOVED
      "Ban" -> LfmRating.BANNED
      else -> LfmRating.NORMAL
    }

    trackRatingLiveDataProvider.set {
      copy(lfmRating = lfmRating)
    }
  }
}

class UpdateLyrics(
  private val mapper: Moshi,
  private val lyricsLiveDataProvider: LyricsState
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

    lyricsLiveDataProvider.set(lyrics)
  }

  companion object {
    const val LYRICS_NEWLINE = "\r\n|\n"
  }
}

class UpdateMute(
  private val statusLiveDataProvider: PlayerStatusState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val mute = message.data as? Boolean ?: false
    statusLiveDataProvider.set { copy(mute = mute) }
  }
}

class UpdateNowPlayingTrack(
  private val playingTrackLiveDataProvider: PlayingTrackState,
  private val context: Application,
  private val mapper: Moshi
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = mapper.adapter(NowPlayingTrack::class.java)
    val track = adapter.fromJsonValue(message.data) ?: return

    playingTrackLiveDataProvider.set {
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
  private val playerStatusLiveDataProvider: PlayerStatusState,
  private val moshi: Moshi
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(PlayerStatus::class.java)
    val status = adapter.fromJsonValue(message.data) ?: return

    playerStatusLiveDataProvider.set {
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
  private val playerStatusLiveDataProvider: PlayerStatusState,
  private val context: Application
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val playState = message.data as? String ?: PlayerState.UNDEFINED

    playerStatusLiveDataProvider.set {
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
  private val ratingLiveDataProvider: TrackRatingState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val rating = message.data.toString().toFloatOrNull()

    ratingLiveDataProvider.set {
      copy(rating = rating ?: 0.0f)
    }
  }
}

class UpdateRepeat(
  private val playerStatusLiveDataProvider: PlayerStatusState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val repeat = (message.data as? String)?.toRepeat() ?: Repeat.NONE

    playerStatusLiveDataProvider.set {
      copy(repeat = repeat)
    }
  }
}

class UpdateShuffle(
  private val playerStatusLiveDataProvider: PlayerStatusState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val data = message.data as? String ?: ShuffleMode.OFF

    playerStatusLiveDataProvider.set {
      copy(shuffle = data)
    }
  }
}

class UpdateVolume(
  private val playerStatusLiveDataProvider: PlayerStatusState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val volume = message.data as Number
    playerStatusLiveDataProvider.set {
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

class ProtocolPingHandle(
  private val messageQueue: MessageQueue,
  private var activityChecker: SocketActivityChecker
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    activityChecker.ping()
    messageQueue.queue(SocketMessage.create(Protocol.PONG))
  }
}

class ProtocolPongHandle : ICommand {
  override fun execute(message: ProtocolMessage) {
    Timber.d(message.data.toString())
  }
}

class UpdateNowPlayingTrackMoved(
  private val moshi: Moshi
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(NowPlayingMoveResponse::class.java)
    val response = adapter.fromJsonValue(message.data)

    // bus.post(TrackMovedEvent(response.from, response.to, response.success))
  }
}

class UpdateNowPlayingTrackRemoval(
  private val moshi: Moshi
) : ICommand {
  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(NowPlayingTrackRemoveResponse::class.java)
    val response = adapter.fromJsonValue(message.data)
    // bus.post(TrackRemovalEvent(response.index, response.success))
  }
}

class UpdatePlaybackPositionCommand(
  private val moshi: Moshi,
  private val trackPositionLiveDataProvider: TrackPositionState
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(Position::class.java)
    val response = adapter.fromJsonValue(message.data) ?: return

    trackPositionLiveDataProvider.set(
      PlayingPosition(
        response.current,
        response.total
      )
    )
  }
}