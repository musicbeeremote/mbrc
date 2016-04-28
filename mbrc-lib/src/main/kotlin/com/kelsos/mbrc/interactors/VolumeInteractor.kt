package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.dto.player.Volume
import com.kelsos.mbrc.dto.requests.VolumeRequest
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class VolumeInteractor {
    @Inject private lateinit var api: PlayerService

    fun getVolume(): Observable<Int> {
        return api.getVolume()
                .map<Int>(Func1<Volume, Int> { it.value })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun setVolume(volume: Int): Observable<Int> {
        val volumeRequest = VolumeRequest()
        volumeRequest.value = volume
        return api.updateVolume(volumeRequest)
                .map<Int>(Func1<Volume, Int> { it.value })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
