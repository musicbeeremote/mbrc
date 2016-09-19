package com.kelsos.mbrc.commands.model

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import javax.inject.Inject

class UpdatePlayState
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {
    model.setPlayState(e.dataString)
  }
}
