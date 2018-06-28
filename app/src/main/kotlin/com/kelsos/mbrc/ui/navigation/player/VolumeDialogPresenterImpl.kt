package com.kelsos.mbrc.ui.navigation.player

import com.jakewharton.rxrelay2.PublishRelay
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit


class VolumeDialogPresenterImpl

constructor(
  private val userActionUseCase: UserActionUseCase,
  private val appRxSchedulers: AppRxSchedulers,
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : VolumeDialogPresenter, BasePresenter<VolumeView>() {

  private val volumeRelay: PublishRelay<Int> = PublishRelay.create()

  init {
    playerStatusLiveDataProvider.observe(this) {
      view().update(it)
    }
  }

  override fun attach(view: VolumeView) {
    super.attach(view)
    disposables += volumeRelay.throttleLast(
      800,
      TimeUnit.MILLISECONDS,
      appRxSchedulers.network
    )
      .subscribeOn(appRxSchedulers.network)
      .subscribe { volume ->
        userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, volume))
      }
  }

  override fun mute() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
  }

  override fun changeVolume(volume: Int) {
    volumeRelay.accept(volume)
  }
}
