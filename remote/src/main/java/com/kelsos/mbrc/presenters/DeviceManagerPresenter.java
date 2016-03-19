package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.ui.views.DeviceManagerView;

public interface DeviceManagerPresenter {
  void bind(DeviceManagerView view);

  void onResume();

  void onPause();

  void saveSettings(DeviceSettings settings);

  void loadDevices();

  void deleteSettings(DeviceSettings settings);

  void setDefault(DeviceSettings settings);
}
