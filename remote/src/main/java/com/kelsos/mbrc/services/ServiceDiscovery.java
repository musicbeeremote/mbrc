package com.kelsos.mbrc.services;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.utilities.MainThreadBus;
import com.squareup.otto.Subscribe;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Hashtable;
import java.util.Locale;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.kelsos.mbrc.events.ui.DiscoveryStopped.DiscoveryStop;

public class ServiceDiscovery {
  public static final String NOTIFY = "notify";
  public static final String UTF_8 = "UTF-8";
  public static final String CONTEXT = "context";
  public static final String DISCOVERY = "discovery";
  public static final String ADDRESS = "address";

  public static final String DISCOVERY_ADDRESS = "239.1.5.10"; //NOPMD
  public static final int PORT = 45345;
  public static final int BUFFER = 512;

  private WifiManager manager;
  private WifiManager.MulticastLock mLock;
  private ConnectivityManager connectivityManager;
  private ObjectMapper mapper;
  private MainThreadBus bus;

  @Inject
  public ServiceDiscovery(WifiManager manager,
      ConnectivityManager connectivityManager,
      ObjectMapper mapper,
      MainThreadBus bus) {
    this.manager = manager;
    this.connectivityManager = connectivityManager;
    this.mapper = mapper;
    this.bus = bus;

    bus.register(this);
  }

  @Subscribe public void onDiscoveryMessage(MessageEvent messageEvent) {
    if (!UserInputEventType.StartDiscovery.equals(messageEvent.getType())) {
      return;
    }
    startDiscovery();
  }

  public void startDiscovery() {

    if (!isWifiConnected()) {
      bus.post(new DiscoveryStopped(DiscoveryStop.NO_WIFI));
      return;
    }

    discover().subscribeOn(Schedulers.io()).subscribe(connectionSettings -> bus.post(connectionSettings), throwable -> {
      bus.post(new DiscoveryStopped(DiscoveryStop.COMPLETE));
    }, () -> {
      stopDiscovery();
    });
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
    return String.format(Locale.getDefault(),
        "%d.%d.%d.%d",
        (address & 0xff),
        (address >> 8 & 0xff),
        (address >> 16 & 0xff),
        (address >> 24 & 0xff));
  }

  private boolean isWifiConnected() {
    NetworkInfo current = connectivityManager.getActiveNetworkInfo();
    return current != null && current.getType() == ConnectivityManager.TYPE_WIFI;
  }

  public Observable<ConnectionSettings> discover() {
    return Observable.create(subscriber -> {
      try {
        mLock = manager.createMulticastLock("locked");
        mLock.setReferenceCounted(true);
        mLock.acquire();

        MulticastSocket mSocket = new MulticastSocket(45345);
        mSocket.setSoTimeout(2000);
        InetAddress group = InetAddress.getByName(DISCOVERY_ADDRESS);
        mSocket.joinGroup(group);

        DatagramPacket mPacket;
        byte[] buffer = new byte[BUFFER];
        mPacket = new DatagramPacket(buffer, buffer.length);
        Hashtable<String, String> discoveryMessage = new Hashtable<>();
        discoveryMessage.put(CONTEXT, DISCOVERY);
        discoveryMessage.put(ADDRESS, getWifiAddress());
        byte[] discovery = mapper.writeValueAsBytes(discoveryMessage);
        mSocket.send(new DatagramPacket(discovery, discovery.length, group, PORT));
        String incoming;

        while (true) {
          mSocket.receive(mPacket);
          incoming = new String(mPacket.getData(), UTF_8);

          JsonNode node = mapper.readValue(incoming, JsonNode.class);
          if (NOTIFY.equals(node.path(CONTEXT).asText())) {
            ConnectionSettings settings = new ConnectionSettings(node);
            subscriber.onNext(settings);
            subscriber.onCompleted();
            break;
          }
        }

        mSocket.leaveGroup(group);
        mSocket.close();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    });
  }
}
