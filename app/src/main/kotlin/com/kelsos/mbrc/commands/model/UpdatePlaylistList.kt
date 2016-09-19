package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import rx.AsyncEmitter
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class UpdatePlaylistList
@Inject constructor(private val model: MainDataModel, private val mapper: ObjectMapper) : ICommand {

  override fun execute(e: IEvent) {
    val nodes = e.data as ArrayNode
    Observable.from(nodes).flatMap {
      Observable.fromEmitter({ it: AsyncEmitter<Playlist> ->
        try {
          val playlist = mapper.treeToValue<Playlist>(nodes, Playlist::class.java)
          it.onNext(playlist)
          it.onCompleted()
        } catch (e1: JsonProcessingException) {
          it.onError(e1)
        }
      }, AsyncEmitter.BackpressureMode.BUFFER)
    }.subscribeOn(Schedulers.io()).toList().subscribe({ model.setPlaylists(it) }) {
      Timber.v(it, "failed to parse the playlists")
    }
  }
}
