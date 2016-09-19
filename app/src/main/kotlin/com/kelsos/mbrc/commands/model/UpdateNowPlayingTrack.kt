package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class UpdateNowPlayingTrack
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    model.setTrackInfo(node.path("Artist").textValue(),
        node.path("Album").textValue(),
        node.path("Title").textValue(),
        node.path("Year").textValue())
  }
}
