package com.kelsos.mbrc.commands.model

import com.fasterxml.jackson.databind.node.ArrayNode
import com.kelsos.mbrc.data.MusicTrack
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.MainDataModel
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class UpdateNowPlayingList
@Inject constructor(private val model: MainDataModel) : ICommand {

  override fun execute(e: IEvent) {

    Observable.create { subscriber: Subscriber<in ArrayList<MusicTrack>> ->
      val node = e.data as ArrayNode
      val playList = ArrayList<MusicTrack>()
      for (i in 0..node.size() - 1) {
        val jNode = node.get(i)
        playList.add(MusicTrack(jNode))
      }
      subscriber.onNext(playList)
      subscriber.onCompleted()
    }.subscribeOn(Schedulers.io()).subscribe({ model.setNowPlayingList(it) }) {
      Timber.e(it, "Failure during now playing parsing")
    }
  }
}
