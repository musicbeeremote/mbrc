package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;
import java.util.List;

public interface DeviceManagerView {
  void showDiscoveryResult(@DiscoveryStopped.Status long reason);

  void dismissLoadingDialog();

  void showNotification(NotifyUser event);

  void updateDevices(List<DeviceSettings> list);
}
