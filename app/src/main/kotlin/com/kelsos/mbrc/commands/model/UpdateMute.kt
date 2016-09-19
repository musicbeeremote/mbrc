package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.node.BooleanNode
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class UpdateMute
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    model.setMuteState((e.data as BooleanNode).asBoolean())
  }
}
