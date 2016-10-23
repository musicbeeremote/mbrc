package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.dto.requests.VolumeRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import javax.inject.Inject

class VolumeInteractor
@Inject constructor(private val api: PlayerService) {

  fun getVolume(): Observable<Int> {
    return api.getVolume()
        .map { it.value }
        .task()
  }

  fun setVolume(volume: Int): Observable<Int> {
    val volumeRequest = VolumeRequest()
    volumeRequest.value = volume
    return api.updateVolume(volumeRequest)
        .map { it.value }
        .task()
  }
}
