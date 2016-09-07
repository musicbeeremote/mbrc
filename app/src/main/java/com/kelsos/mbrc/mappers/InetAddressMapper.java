package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.data.ConnectionSettings;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class InetAddressMapper implements Mapper<ConnectionSettings, SocketAddress> {
  @Override
  public SocketAddress map(ConnectionSettings connectionSettings) {
    return new InetSocketAddress(connectionSettings.getAddress(), connectionSettings.getPort());
  }
}
