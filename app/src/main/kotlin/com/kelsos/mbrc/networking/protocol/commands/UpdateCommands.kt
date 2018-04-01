package com.kelsos.mbrc.networking.protocol.commands

import android.app.Application
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.Repeat
import com.kelsos.mbrc.content.activestatus.TrackRatingModel
import com.kelsos.mbrc.content.activestatus.livedata.LyricsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.content.lyrics.LyricsPayload
import com.kelsos.mbrc.content.nowplaying.NowPlayingTrack
import com.kelsos.mbrc.events.ShuffleMode
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.platform.widgets.UpdateWidgets
import com.kelsos.mbrc.ui.navigation.main.LfmRating
import timber.log.Timber
import javax.inject.Inject

class UpdateLastFm
@Inject
constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val scrobble = (e.data as BooleanNode).asBoolean()
    playerStatusLiveDataProvider.update({ PlayerStatusModel(scrobbling = scrobble) }) {
      copy(scrobbling = scrobble)
    }
  }
}

class UpdateLfmRating
@Inject
constructor(
  private val trackRatingLiveDataProvider: TrackRatingLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val lfmRating = when (e.dataString) {
      "Love" -> LfmRating.LOVED
      "Ban" -> LfmRating.BANNED
      else -> LfmRating.NORMAL
    }

    trackRatingLiveDataProvider.update({ TrackRatingModel(lfmRating = lfmRating) }) {
      copy(lfmRating = lfmRating)
    }
  }
}

class UpdateLyrics
@Inject
constructor(
  private val mapper: ObjectMapper,
  private val lyricsLiveDataProvider: LyricsLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val payload = mapper.treeToValue((e.data as JsonNode), LyricsPayload::class.java)
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
@Inject
constructor(
  private val statusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val mute = (e.data as BooleanNode).asBoolean()
    statusLiveDataProvider.update({ PlayerStatusModel(mute = mute) }) { copy(mute = mute) }
  }
}

class UpdateNowPlayingTrack
@Inject
constructor(
  private val playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val context: Application,
  private val mapper: ObjectMapper
) : ICommand {

  override fun execute(e: IEvent) {
    val track: NowPlayingTrack = mapper.treeToValue((e.data as JsonNode))

    playingTrackLiveDataProvider.update({
      with(track) {
        PlayingTrackModel(artist, title, album, year, path)
      }
    }) {
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
@Inject
constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    val mute = node.path(Protocol.PlayerMute).asBoolean()
    val playState = node.path(Protocol.PlayerState).asText()
    val repeat = node.path(Protocol.PlayerRepeat).asText().toRepeat()
    //noinspection ResourceType
    val shuffle = node.path(Protocol.PlayerShuffle).asText()
    val scrobbling = node.path(Protocol.PlayerScrobble).asBoolean()
    val volume = Integer.parseInt(node.path(Protocol.PlayerVolume).asText())

    playerStatusLiveDataProvider.update({
      PlayerStatusModel(
        mute = mute,
        playState = playState,
        repeat = repeat,
        shuffle = shuffle,
        scrobbling = scrobbling,
        volume = volume
      )
    }) {
      copy(
        mute = mute,
        playState = playState,
        repeat = repeat,
        shuffle = shuffle,
        scrobbling = scrobbling,
        volume = volume
      )
    }
  }
}

class UpdatePlayState
@Inject
constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val context: Application
) : ICommand {

  override fun execute(e: IEvent) {
    val playState = e.dataString
    playerStatusLiveDataProvider.update({ PlayerStatusModel(playState = playState) }) {
      copy(playState = playState)
    }
    UpdateWidgets.updatePlaystate(context, e.dataString)
  }
}

class UpdatePluginVersionCommand
@Inject
constructor(

) : ICommand {

  override fun execute(e: IEvent) {
    val pluginVersion = e.dataString
    Timber.v("plugin reports $pluginVersion")
  }
}

class UpdateRating
@Inject
constructor(
  private val ratingLiveDataProvider: TrackRatingLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val rating = (e.data as TextNode).asDouble(0.0).toFloat()
    ratingLiveDataProvider.update({ TrackRatingModel(rating = rating) }) {
      copy(rating = rating)
    }
  }
}

class UpdateRepeat
@Inject
constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val repeat = e.dataString.toRepeat()
    playerStatusLiveDataProvider.update({ PlayerStatusModel(repeat = repeat) }) {
      copy(repeat = repeat)
    }
  }
}

class UpdateShuffle
@Inject
constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    var data: String? = e.dataString

    // Older plugin support, where the shuffle had boolean value.
    if (data == null) {
      data = if ((e.data as JsonNode).asBoolean()) {
        ShuffleMode.SHUFFLE
      } else {
        ShuffleMode.OFF
      }
    }

    playerStatusLiveDataProvider.update({ PlayerStatusModel(shuffle = data) }) {
      copy(shuffle = data)
    }
  }
}

class UpdateVolume
@Inject
constructor(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val volume = (e.data as IntNode).asInt()
    playerStatusLiveDataProvider.update({ PlayerStatusModel(volume = volume) }) {
      copy(volume = volume)
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