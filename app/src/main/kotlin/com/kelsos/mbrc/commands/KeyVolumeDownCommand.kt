package com.kelsos.mbrc.commands

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class KeyVolumeDownCommand
@Inject constructor(private val model: MainDataModel, private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    if (model.getVolume() >= 10) {
      val mod = model.getVolume() % 10
      val volume: Int

      if (mod == 0) {
        volume = model.getVolume() - 10
      } else if (mod < 5) {
        volume = model.getVolume() - (10 + mod)
      } else {
        volume = model.getVolume() - mod
      }

      bus.post(MessageEvent.action(UserAction(Protocol.PlayerVolume, volume)))
    }
  }
}
