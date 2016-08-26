package com.kelsos.mbrc.services;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.DiscoveryMessage;
import com.kelsos.mbrc.enums.DiscoveryStop;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DiscoveryStopped;
import com.kelsos.mbrc.mappers.ConnectionMapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ServiceDiscovery {
  private static final String NOTIFY = "notify";
  private static final int SO_TIMEOUT = 6 * 1000;
  private static final int MULTICASTPORT = 45345;
  private static final String DISCOVERY_ADDRESS = "239.1.5.10"; //NOPMD

  private WifiManager manager;
  private WifiManager.MulticastLock mLock;
  private ConnectivityManager connectivityManager;
  private ObjectMapper mapper;
  private RxBus bus;
  private InetAddress group;

  @Inject
  public ServiceDiscovery(WifiManager manager,
      ConnectivityManager connectivityManager,
      ObjectMapper mapper,
      RxBus bus) {
    this.manager = manager;
    this.connectivityManager = connectivityManager;
    this.mapper = mapper;
    this.bus = bus;
  }

  public void startDiscovery() {
    startDiscovery(null);
  }

  public void startDiscovery(OnDiscoveryComplete callback) {
    if (!isWifiConnected()) {
      bus.post(new DiscoveryStopped(DiscoveryStop.NO_WIFI));
      return;
    }
    mLock = manager.createMulticastLock("locked");
    mLock.setReferenceCounted(true);
    mLock.acquire();

    Timber.v("Starting remote service discovery");

    ConnectionMapper mapper = new ConnectionMapper();
    discoveryObservable().subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .doOnTerminate(this::stopDiscovery)
        .map(mapper::map)
        .subscribe(settings -> {
          settings.save();
          bus.post(new DiscoveryStopped(DiscoveryStop.COMPLETE));
          bus.post(ConnectionSettingsChanged.newInstance(settings.getId()));
          if (callback == null) {
            return;
          }

          callback.complete();

        }, throwable -> {
          Timber.v(throwable, "Discovery incomplete");
          bus.post(new DiscoveryStopped(DiscoveryStop.NOT_FOUND));
        });
  }

  private void stopDiscovery() {
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

  @NonNull
  private Observable<DiscoveryMessage> discoveryObservable() {
    return Observable.using(this::getResource, this::getObservable, this::cleanup);
  }

  private void cleanup(MulticastSocket resource) {
    try {
      resource.leaveGroup(group);
      resource.close();
      stopDiscovery();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NonNull
  private Observable<DiscoveryMessage> getObservable(MulticastSocket socket) {
    return Observable.interval(600, TimeUnit.MILLISECONDS)
        .take(6)
        .flatMap(aLong -> Observable.create((Subscriber<? super DiscoveryMessage> subscriber) -> {
          try {
            DatagramPacket mPacket;
            byte[] buffer = new byte[512];
            mPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(mPacket);
            String incoming = new String(mPacket.getData(), Const.UTF_8);
            DiscoveryMessage node = mapper.readValue(incoming, DiscoveryMessage.class);
            Timber.v("Discovery received -> %s", node);
            subscriber.onNext(node);
            subscriber.onCompleted();
          } catch (IOException e) {
            subscriber.onError(e);
          }
        }))
        .filter(message -> NOTIFY.equals(message.getContext()))
        .first();
  }

  private MulticastSocket getResource() {
    try {
      MulticastSocket multicastSocket = new MulticastSocket(MULTICASTPORT);
      multicastSocket.setSoTimeout(SO_TIMEOUT);
      group = InetAddress.getByName(DISCOVERY_ADDRESS);
      multicastSocket.joinGroup(group);
      DiscoveryMessage message = new DiscoveryMessage();
      message.setContext(Protocol.DISCOVERY);
      message.setAddress(getWifiAddress());
      byte[] discovery = mapper.writeValueAsBytes(message);
      multicastSocket.send(new DatagramPacket(discovery, discovery.length, group, MULTICASTPORT));
      return multicastSocket;
    } catch (IOException e) {
      Timber.v(e, "Failed to open multicast socket");
      throw new RuntimeException(e);
    }
  }

  public interface OnDiscoveryComplete {
    void complete();
  }
}
