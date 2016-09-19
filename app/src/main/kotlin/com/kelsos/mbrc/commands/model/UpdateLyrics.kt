package com.kelsos.mbrc.commands.model

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.LyricsModel
import javax.inject.Inject

class UpdateLyrics
@Inject
constructor(private val model: LyricsModel) : ICommand {

  override fun execute(e: IEvent) {
    model.setLyrics(e.dataString)
  }
}
