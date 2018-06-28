package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.WeakHashMap

interface DefaultSettingsLiveDataProvider {

  fun observe(obj: Any, onSettings: OnDefaultConnectionChanged)

  fun update(connectionSettings: ConnectionSettingsEntity)

  fun removeObservers(obj: Any)
}

typealias OnDefaultConnectionChanged = (ConnectionSettingsEntity) -> Unit

class DefaultSettingsLiveDataProviderImpl : DefaultSettingsLiveDataProvider {
  private val settingsRelay: MutableSharedFlow<ConnectionSettingsEntity> = MutableSharedFlow(0, 1)
  private val map: WeakHashMap<Any, Job> = WeakHashMap()
  private val supervisor: Job = SupervisorJob()
  private val scope: CoroutineScope = CoroutineScope(supervisor + Dispatchers.IO)

  override fun observe(obj: Any, onSettings: OnDefaultConnectionChanged) {
    map[obj] = settingsRelay.distinctUntilChanged().onEach { onSettings(it) }.launchIn(scope)
  }

  override fun update(connectionSettings: ConnectionSettingsEntity) {
    settingsRelay.tryEmit(connectionSettings)
  }

  override fun removeObservers(obj: Any) {
    map.remove(obj)
  }
}
