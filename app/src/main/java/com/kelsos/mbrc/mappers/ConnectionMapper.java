package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.data.DiscoveryMessage;

public class ConnectionMapper implements Mapper<DiscoveryMessage, ConnectionSettings> {
  @Override
  public ConnectionSettings map(DiscoveryMessage discoveryMessage) {
    ConnectionSettings settings = new ConnectionSettings();
    settings.setAddress(discoveryMessage.getAddress());
    settings.setPort(discoveryMessage.getPort());
    settings.setName(discoveryMessage.getName());
    return settings;
  }
}
