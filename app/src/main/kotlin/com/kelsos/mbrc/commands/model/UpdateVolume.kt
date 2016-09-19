package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.node.IntNode
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class UpdateVolume
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    model.setVolume((e.data as IntNode).asInt())
  }
}
