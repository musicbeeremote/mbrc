package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.node.TextNode
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class UpdateCover
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    model.setCover((e.data as TextNode).textValue())
  }
}
