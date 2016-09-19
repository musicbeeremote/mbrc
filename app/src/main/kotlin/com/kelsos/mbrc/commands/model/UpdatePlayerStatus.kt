package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class UpdatePlayerStatus
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    model.setPlayState(node.path(Protocol.PlayerState).asText())
    model.setMuteState(node.path(Protocol.PlayerMute).asBoolean())
    model.setRepeatState(node.path(Protocol.PlayerRepeat).asText())
    val shuffleState = node.path(Protocol.PlayerShuffle).asText()
    //noinspection ResourceType
    model.setShuffleState(shuffleState)
    model.setScrobbleState(node.path(Protocol.PlayerScrobble).asBoolean())
    model.setVolume(Integer.parseInt(node.path(Protocol.PlayerVolume).asText()))
  }
}
