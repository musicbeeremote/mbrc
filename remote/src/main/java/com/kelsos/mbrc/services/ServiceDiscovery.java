package com.kelsos.mbrc.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.enums.DiscoveryStop;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Hashtable;
import java.util.Locale;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class ServiceDiscovery {
  public static final String NOTIFY = "notify";
  private WifiManager manager;
  private WifiManager.MulticastLock mLock;
  private ObjectMapper mapper;
  private Context mContext;
  private MainThreadBusWrapper bus;

  @Inject public ServiceDiscovery(Context context, ObjectMapper mapper, MainThreadBusWrapper bus) {
    this.mContext = context;
    manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    this.mapper = mapper;
    this.bus = bus;
  }

  public void startDiscovery() {
    if (!isWifiConnected()) {
      bus.post(new DiscoveryStopped(DiscoveryStop.NO_WIFI));
      return;
    }
    mLock = manager.createMulticastLock("locked");
    mLock.setReferenceCounted(true);
    mLock.acquire();

    Thread mThread = new Thread(new ServiceListener());
    mThread.start();
  }

  public void stopDiscovery() {
    if (mLock != null) {
      mLock.release();
      mLock = null;
    }
  }

  private String getWifiAddress() {
    WifiInfo mInfo = manager.getConnectionInfo();
    int address = mInfo.getIpAddress();
    return String.format(Locale.getDefault(), "%d.%d.%d.%d", (address & 0xff),
        (address >> 8 & 0xff), (address >> 16 & 0xff), (address >> 24 & 0xff));
  }

  private boolean isWifiConnected() {
    ConnectivityManager cMan =
        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo current = cMan.getActiveNetworkInfo();
    return current != null && current.getType() == ConnectivityManager.TYPE_WIFI;
  }

  private class ServiceListener implements Runnable {

    public static final String DISCOVERY_ADDRESS = "239.1.5.10"; //NOPMD

    @Override public void run() {
      try {

        MulticastSocket mSocket = new MulticastSocket(45345);
        mSocket.setSoTimeout(2000);
        InetAddress group = InetAddress.getByName(DISCOVERY_ADDRESS);
        mSocket.joinGroup(group);

        DatagramPacket mPacket;
        byte[] buffer = new byte[512];
        mPacket = new DatagramPacket(buffer, buffer.length);
        Hashtable<String, String> discoveryMessage = new Hashtable<>();
        discoveryMessage.put(Protocol.CONTEXT, Protocol.DISCOVERY);
        discoveryMessage.put(Protocol.ADDRESS, getWifiAddress());
        byte[] discovery = mapper.writeValueAsBytes(discoveryMessage);
        mSocket.send(new DatagramPacket(discovery, discovery.length, group, 45345));
        String incoming;

        while (true) {
          mSocket.receive(mPacket);
          incoming = new String(mPacket.getData(), Const.UTF_8);

          JsonNode node = mapper.readValue(incoming, JsonNode.class);
          if (NOTIFY.equals(node.path(Protocol.CONTEXT).asText())) {
            ConnectionSettings settings = new ConnectionSettings(node);
            bus.post(settings);
            break;
          }
        }

        bus.post(new DiscoveryStopped(DiscoveryStop.COMPLETE));
        mSocket.leaveGroup(group);
        mSocket.close();
        stopDiscovery();
      } catch (InterruptedIOException e) {
        bus.post(new DiscoveryStopped(DiscoveryStop.NOT_FOUND));
        stopDiscovery();
      } catch (IOException e) {
        bus.post(new DiscoveryStopped(DiscoveryStop.NOT_FOUND));
      }
    }
  }
}
