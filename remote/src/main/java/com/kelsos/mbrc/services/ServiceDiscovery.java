package com.kelsos.mbrc.services;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.dto.DiscoveryResponse;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.mappers.DeviceSettingsMapper;
import com.kelsos.mbrc.repository.DeviceRepository;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.SettingsManager;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Hashtable;
import java.util.Locale;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class ServiceDiscovery {
  public static final String NOTIFY = "notify";
  public static final String UTF_8 = "UTF-8";
  public static final String CONTEXT = "context";
  public static final String DISCOVERY = "discovery";
  public static final String ADDRESS = "address";

  public static final String DISCOVERY_ADDRESS = "239.1.5.10"; //NOPMD
  public static final int PORT = 45345;
  public static final int BUFFER = 512;
  public static final int DISCOVERY_TIMEOUT = 5000;
  public static final int MULTICAST_PORT = 45345;

  private WifiManager manager;
  private WifiManager.MulticastLock multicastLock;
  private ConnectivityManager connectivityManager;
  private ObjectMapper mapper;
  private RxBus bus;
  private DeviceRepository repository;
  private SettingsManager settingsManager;

  @Inject
  public ServiceDiscovery(WifiManager manager,
      ConnectivityManager connectivityManager,
      ObjectMapper mapper,
      RxBus bus, DeviceRepository repository, SettingsManager settingsManager) {
    this.manager = manager;
    this.connectivityManager = connectivityManager;
    this.mapper = mapper;
    this.bus = bus;
    this.repository = repository;
    this.settingsManager = settingsManager;

    bus.register(this, MessageEvent.class, this::onDiscoveryMessage);
  }

  public void onDiscoveryMessage(MessageEvent messageEvent) {
    if (!UserInputEventType.StartDiscovery.equals(messageEvent.getType())) {
      return;
    }
    startDiscovery().subscribe(connectionSettings -> {

    }, Ln::v);
  }

  public Observable<DeviceSettings> startDiscovery() {

    if (!isWifiConnected()) {
      bus.post(new DiscoveryStopped(DiscoveryStopped.NO_WIFI, null));
      return Observable.empty();
    }

    return discover().subscribeOn(Schedulers.io())
        .doOnTerminate(this::stopDiscovery)
        .doOnNext(connectionSettings -> {
          repository.save(connectionSettings);

          if (repository.count() == 1) {
            Timber.v("Only one entry found, setting it as default");
            settingsManager.setDefault(1);
          }

          bus.post(new DiscoveryStopped(DiscoveryStopped.SUCCESS, connectionSettings));
        }).doOnError(t -> {
          bus.post(new DiscoveryStopped(DiscoveryStopped.NOT_FOUND, null));
          Timber.e(t, "During service discovery");
        });
  }

  public void stopDiscovery() {
    if (multicastLock != null) {
      multicastLock.release();
      multicastLock = null;
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

  public Observable<DeviceSettings> discover() {
    return Observable.create(subscriber -> {
      try {
        multicastLock = manager.createMulticastLock("locked");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();

        MulticastSocket mSocket = new MulticastSocket(MULTICAST_PORT);
        mSocket.setSoTimeout(DISCOVERY_TIMEOUT);
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

          DiscoveryResponse node = mapper.readValue(incoming, DiscoveryResponse.class);
          if (NOTIFY.equals(node.getContext())) {
            subscriber.onNext(DeviceSettingsMapper.fromResponse(node));
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
