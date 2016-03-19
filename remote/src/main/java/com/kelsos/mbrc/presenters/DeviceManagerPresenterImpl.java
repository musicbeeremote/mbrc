package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.repository.DeviceRepository;
import com.kelsos.mbrc.ui.views.DeviceManagerView;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.SettingsManager;
import timber.log.Timber;

public class DeviceManagerPresenterImpl implements DeviceManagerPresenter {

  @Inject private RxBus bus;
  @Inject private DeviceRepository repository;
  @Inject private SettingsManager settingsManager;

  private DeviceManagerView view;

  @Override public void bind(DeviceManagerView view) {

    this.view = view;
  }

  @Override public void onResume() {
    bus.register(DiscoveryStopped.class, this::onDiscoveryStopped, false);
    bus.register(NotifyUser.class, view::showNotification, false);
  }

  @Override public void onPause() {
    bus.unregister(this);
  }

  @Override public void saveSettings(DeviceSettings settings) {
    repository.save(settings);
  }

  @Override public void loadDevices() {
    repository.getAll().subscribe(list -> {
      view.updateDevices(list);
    }, t -> {
      Timber.e(t, "Failed");
    });
  }

  @Override public void deleteSettings(DeviceSettings settings) {

  }

  @Override public void setDefault(DeviceSettings settings) {
    settingsManager.setDefault(settings.getId());
  }

  private void onDiscoveryStopped(DiscoveryStopped event) {
    view.dismissLoadingDialog();
    view.showDiscoveryResult(event.getReason());
  }
}
