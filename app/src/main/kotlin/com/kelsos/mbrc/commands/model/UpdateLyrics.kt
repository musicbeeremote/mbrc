package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.data.LyricsPayload
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.LyricsModel
import javax.inject.Inject

class UpdateLyrics
@Inject
constructor(private val model: LyricsModel,
            private val mapper: ObjectMapper) : ICommand {

  override fun execute(e: IEvent) {
    val payload = mapper.treeToValue((e.data as JsonNode), LyricsPayload::class.java)

    if (payload.status == LyricsPayload.SUCCESS) {
      model.lyrics = payload.lyrics
      model.status = payload.status
    } else if (payload.status == LyricsPayload.READING) {
      model.lyrics = ""
      model.status = payload.status
    } else {
      model.lyrics = ""
      model.status = LyricsPayload.NOT_FOUND
    }

  }
}
