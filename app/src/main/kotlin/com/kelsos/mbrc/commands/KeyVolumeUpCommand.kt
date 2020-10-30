package com.kelsos.mbrc.commands

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class KeyVolumeUpCommand
@Inject
constructor(
  private val model: MainDataModel,
  private val bus: RxBus
) : ICommand {

  override fun execute(e: IEvent) {
    if (model.volume <= 90) {
      val mod = model.volume % 10
      val volume: Int

      volume = when {
        mod == 0 -> model.volume + 10
        mod < 5 -> model.volume + (10 - mod)
        else -> model.volume + (20 - mod)
      }

      bus.post(MessageEvent.action(UserAction(Protocol.PlayerVolume, volume)))
    }
  }
}
