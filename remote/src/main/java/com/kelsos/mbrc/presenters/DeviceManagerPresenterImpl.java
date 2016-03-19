package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.ui.views.DeviceManagerView;
import com.kelsos.mbrc.utilities.RxBus;

public class DeviceManagerPresenterImpl implements DeviceManagerPresenter {

  @Inject private RxBus bus;

  private DeviceManagerView view;

  @Override public void bind(DeviceManagerView view) {

    this.view = view;
  }

  @Override public void onResume() {
    bus.register(ConnectionSettingsChanged.class, this::onConnectionSettingsChange, true);
    bus.register(DiscoveryStopped.class, this::onDiscoveryStopped, false);
    bus.register(NotifyUser.class, view::showNotification, false);
  }

  @Override public void onPause() {
    bus.unregister(this);
  }

  @Override public void saveSettings(DeviceSettings settings) {

  }

  @Override public void loadDevices() {

  }

  @Override public void deleteSettings(DeviceSettings settings) {

  }

  @Override public void setDefault(DeviceSettings settings) {

  }

  private void onConnectionSettingsChange(ConnectionSettingsChanged event) {

  }

  private void onDiscoveryStopped(DiscoveryStopped event) {
    view.dismissLoadingDialog();
    view.showDiscoveryResult(event.getReason());
  }
}
