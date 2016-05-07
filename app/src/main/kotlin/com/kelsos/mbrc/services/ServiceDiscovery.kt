package com.kelsos.mbrc.services

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Singleton
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.dao.DeviceSettings
import com.kelsos.mbrc.dto.DiscoveryResponse
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.ui.DiscoveryStopped
import com.kelsos.mbrc.mappers.DeviceSettingsMapper
import com.kelsos.mbrc.repository.DeviceRepository
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.utilities.SettingsManager
import roboguice.util.Ln
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.*

@Singleton
class ServiceDiscovery
@Inject
constructor(private val manager: WifiManager,
            private val connectivityManager: ConnectivityManager,
            private val mapper: ObjectMapper,
            private val bus: RxBus, private val repository: DeviceRepository, private val settingsManager: SettingsManager) {
  private var multicastLock: WifiManager.MulticastLock? = null

  init {
    bus.register(this, MessageEvent::class.java, { this.onDiscoveryMessage(it) })
  }

  fun onDiscoveryMessage(messageEvent: MessageEvent) {
    if (UserInputEventType.StartDiscovery != messageEvent.type) {
      return
    }
    startDiscovery().subscribe({

    }) { Ln.v(it) }
  }

  fun startDiscovery(): Observable<DeviceSettings> {

    if (!isWifiConnected) {
      bus.post(DiscoveryStopped(DiscoveryStopped.NO_WIFI, null))
      return Observable.empty<DeviceSettings>()
    }

    return discover().subscribeOn(Schedulers.io()).doOnTerminate({ this.stopDiscovery() }).doOnNext { connectionSettings ->
      repository.save(connectionSettings)

      if (repository.count() == 1L) {
        Timber.v("Only one entry found, setting it as default")
        settingsManager.setDefault(1)
      }

      bus.post(DiscoveryStopped(DiscoveryStopped.SUCCESS, connectionSettings))
    }.doOnError { t ->
      bus.post(DiscoveryStopped(DiscoveryStopped.NOT_FOUND, null))
      Timber.e(t, "During service discovery")
    }
  }

  fun stopDiscovery() {
    if (multicastLock != null) {
      multicastLock!!.release()
      multicastLock = null
    }
  }

  private val wifiAddress: String
    get() {
      val mInfo = manager.connectionInfo
      val address = mInfo.ipAddress
      return String.format(Locale.getDefault(),
          "%d.%d.%d.%d",
          address and 0xff,
          address shr 8 and 0xff,
          address shr 16 and 0xff,
          address shr 24 and 0xff)
    }

  private val isWifiConnected: Boolean
    get() {
      val current = connectivityManager.activeNetworkInfo
      return current != null && current.type == ConnectivityManager.TYPE_WIFI
    }

  fun discover(): Observable<DeviceSettings> {
    return Observable.create<DeviceSettings> {
      try {
        multicastLock = manager.createMulticastLock("locked")
        multicastLock!!.setReferenceCounted(true)
        multicastLock!!.acquire()

        val mSocket = MulticastSocket(MULTICAST_PORT)
        mSocket.soTimeout = DISCOVERY_TIMEOUT
        val group = InetAddress.getByName(DISCOVERY_ADDRESS)
        mSocket.joinGroup(group)

        val mPacket: DatagramPacket
        val buffer = ByteArray(BUFFER)
        mPacket = DatagramPacket(buffer, buffer.size)
        val discoveryMessage = Hashtable<String, String>()
        discoveryMessage.put(CONTEXT, DISCOVERY)
        discoveryMessage.put(ADDRESS, wifiAddress)
        val discovery = mapper.writeValueAsBytes(discoveryMessage)
        mSocket.send(DatagramPacket(discovery, discovery.size, group, PORT))
        var incoming: String

        while (true) {
          mSocket.receive(mPacket)
          incoming = mPacket.data.toString(charset(UTF_8))

          val node = mapper.readValue(incoming, DiscoveryResponse::class.java)
          if (NOTIFY == node.context) {
            it.onNext(DeviceSettingsMapper.fromResponse(node))
            it.onCompleted()
            break
          }
        }

        mSocket.leaveGroup(group)
        mSocket.close()
      } catch (e: IOException) {
        it.onError(e)
      }
    }
  }

  companion object {
    const val NOTIFY = "notify"
    const val UTF_8 = "UTF-8"
    const val CONTEXT = "context"
    const val DISCOVERY = "discovery"
    const val ADDRESS = "address"

    const val DISCOVERY_ADDRESS = "239.1.5.10" //NOPMD
    const val PORT = 45345
    const val BUFFER = 512
    const val DISCOVERY_TIMEOUT = 8000
    const val MULTICAST_PORT = 45345
  }
}
