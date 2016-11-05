package com.kelsos.mbrc.commands.model

import android.app.Application
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.widgets.UpdateWidgets
import javax.inject.Inject

class UpdatePlayState
@Inject constructor(private val model: MainDataModel,
                    private val context: Application) : ICommand {

  override fun execute(e: IEvent) {
    model.playState = e.dataString
    UpdateWidgets.update(context)
  }
}
