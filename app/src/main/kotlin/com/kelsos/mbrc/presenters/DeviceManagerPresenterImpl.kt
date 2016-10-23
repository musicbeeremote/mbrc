package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.dao.DeviceSettings
import com.kelsos.mbrc.events.ui.DiscoveryStopped
import com.kelsos.mbrc.events.ui.NotifyUser
import com.kelsos.mbrc.repository.DeviceRepository
import com.kelsos.mbrc.ui.views.DeviceManagerView
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.utilities.SettingsManager
import timber.log.Timber
import javax.inject.Inject

class DeviceManagerPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val repository: DeviceRepository,
                    private val settingsManager: SettingsManager) : DeviceManagerPresenter {

  private var view: DeviceManagerView? = null

  override fun bind(view: DeviceManagerView) {
    this.view = view
  }

  override fun onResume() {
    bus.register(DiscoveryStopped::class.java, { this.onDiscoveryStopped(it) }, false)
    bus.register(NotifyUser::class.java, { view?.showNotification(it) }, false)
  }

  override fun onPause() {
    bus.unregister(this)
  }

  override fun saveSettings(settings: DeviceSettings) {
    repository.save(settings)
  }

  override fun loadDevices() {
    repository.getAllObservable().subscribe({ view?.updateDevices(it) }) { Timber.e(it, "Failed") }
  }

  override fun deleteSettings(settings: DeviceSettings) {

  }

  override fun setDefault(settings: DeviceSettings) {
    settingsManager.setDefault(settings.id)
  }

  private fun onDiscoveryStopped(event: DiscoveryStopped) {
    view?.dismissLoadingDialog()
    view?.showDiscoveryResult(event.reason)
  }
}
