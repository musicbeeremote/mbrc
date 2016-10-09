package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class UpdateNowPlayingTrack
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    val artist = node.path("artist").textValue()
    val album = node.path("album").textValue()
    val title = node.path("title").textValue()
    val year = node.path("year").textValue()
    val path = node.path("path").textValue()
    model.trackInfo = TrackInfo(artist, title, album, year, path)
  }
}
