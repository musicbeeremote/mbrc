package com.kelsos.mbrc.content.activestatus.livedata

import com.jakewharton.rxrelay2.BehaviorRelay
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.disposables.Disposable
import java.util.WeakHashMap
import javax.inject.Inject

interface DefaultSettingsLiveDataProvider {

  fun observe(obj: Any, onSettings: OnDefaultConnectionChanged)

  fun update(connectionSettings: ConnectionSettingsEntity)

  fun removeObservers(obj: Any)
}

typealias OnDefaultConnectionChanged = (ConnectionSettingsEntity) -> Unit

class DefaultSettingsLiveDataProviderImpl
@Inject
constructor(
  private val schedulerProvider: SchedulerProvider
) : DefaultSettingsLiveDataProvider {
  private val settingsRelay: BehaviorRelay<ConnectionSettingsEntity> = BehaviorRelay.create()
  private val map: WeakHashMap<Any, Disposable> = WeakHashMap()

  override fun observe(obj: Any, onSettings: OnDefaultConnectionChanged) {
    map[obj] = settingsRelay.subscribeOn(schedulerProvider.io())
      .subscribeOn(schedulerProvider.main())
      .subscribe { onSettings(it) }
  }

  override fun update(connectionSettings: ConnectionSettingsEntity) {
    settingsRelay.accept(connectionSettings)
  }

  override fun removeObservers(obj: Any) {
    map.remove(obj)
  }
}