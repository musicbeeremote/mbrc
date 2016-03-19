package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.dto.DiscoveryResponse;

public class DeviceSettingsMapper {
  public static DeviceSettings fromResponse(DiscoveryResponse response) {
    DeviceSettings settings = new DeviceSettings();
    settings.setAddress(response.getAddress());
    settings.setName(response.getName());
    settings.setPort(response.getPort());
    settings.setHttp(response.getHttp());
    return settings;
  }
}
