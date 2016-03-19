package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.events.ui.NotifyUser;

public interface DeviceManagerView {
  void showDiscoveryResult(@DiscoveryStopped.Status long reason);

  void dismissLoadingDialog();

  void showNotification(NotifyUser event);
}
