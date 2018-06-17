package com.kelsos.mbrc.networking.discovery

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.kelsos.mbrc.networking.connections.ConnectionMapper
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteServiceDiscoveryImpl
@Inject
internal constructor(
  private val manager: WifiManager,
  private val connectivityManager: ConnectivityManager,
  private val moshi: Moshi
) : RemoteServiceDiscovery {
  private var mLock: WifiManager.MulticastLock? = null
  private var group: InetAddress? = null

  private val disposables = CompositeDisposable()
  private val adapter by lazy { moshi.adapter(DiscoveryMessage::class.java) }

  @SuppressLint("CheckResult")
  override fun discover(callback: (status: Int, setting: ConnectionSettingsEntity?) -> Unit) {
    if (!isWifiConnected) {
      callback(DiscoveryStop.NO_WIFI, null)
      return
    }
    mLock = manager.createMulticastLock("locked")
    mLock?.let {
      it.setReferenceCounted(true)
      it.acquire()
    }

    Timber.v("Starting remote service discovery")

    val mapper = ConnectionMapper()
    discoveryObservable()
      .subscribeOn(Schedulers.io())
      .unsubscribeOn(Schedulers.io())
      .doOnTerminate { this.stopDiscovery() }
      .map { mapper.map(it) }
      .subscribe({
        callback(DiscoveryStop.COMPLETE, it)
      }) {
        callback(DiscoveryStop.NOT_FOUND, null)
        Timber.v(it, "Discovery incomplete")
      }
  }

  private fun stopDiscovery() {
    mLock?.release()
    mLock = null
    disposables.clear()
  }

  private val wifiAddress: String
    get() {
      val mInfo = manager.connectionInfo
      val address = mInfo.ipAddress
      return String.format(
        Locale.getDefault(),
        "%d.%d.%d.%d",
        address and 0xff,
        address shr 8 and 0xff,
        address shr 16 and 0xff,
        address shr 24 and 0xff
      )
    }

  private val isWifiConnected: Boolean
    get() {
      val current = connectivityManager.activeNetworkInfo
      return current != null && current.type == ConnectivityManager.TYPE_WIFI
    }

  private fun discoveryObservable(): Observable<DiscoveryMessage> {
    return Observable.using<DiscoveryMessage, MulticastSocket>({
      this.resource
    }, {
      this.getObservable(it)
    }, { this.cleanup(it) })
  }

  private fun cleanup(resource: MulticastSocket) {
    try {
      resource.leaveGroup(group)
      resource.close()
      stopDiscovery()
    } catch (e: IOException) {
      Timber.v("While cleaning up the discovery ${e.message}")
    }
  }

  private fun getObservable(socket: MulticastSocket): Observable<DiscoveryMessage> {
    return Observable.interval(1000, TimeUnit.MILLISECONDS)
      .take(15)
      .flatMap { getMessage(socket) }
      .filter { message -> NOTIFY == message.context }
      .doOnNext { Timber.v("discovery message -> $it") }
      .firstElement()
      .toObservable()
  }

  private fun getMessage(socket: MulticastSocket): Observable<DiscoveryMessage>? {
    return Observable.fromCallable {
      val buffer = ByteArray(512)
      return@fromCallable with(DatagramPacket(buffer, buffer.size)) {
        socket.receive(this)
        val message = String(data.copyOfRange(0, length), Charsets.UTF_8)
        adapter.fromJson(message)
      }
    }
  }

  private val resource: MulticastSocket
    get() {
      try {
        return MulticastSocket(MULTICAST_PORT).apply {
          soTimeout = SO_TIMEOUT
          joinGroup(InetAddress.getByName(DISCOVERY_ADDRESS).also {
            group = it
          })

          val message = with(DiscoveryMessage()) {
            context = Protocol.DISCOVERY
            address = wifiAddress
            adapter.toJson(this).toByteArray()
          }

          send(
            DatagramPacket(
              message, message.size, group,
              MULTICAST_PORT
            )
          )
        }

      } catch (e: IOException) {
        Timber.v(e, "Failed to open multicast socket")
        throw e
      }
    }

  companion object {
    private const val NOTIFY = "notify"
    private const val SO_TIMEOUT = 15 * 1000
    private const val MULTICAST_PORT = 45345
    private const val DISCOVERY_ADDRESS = "239.1.5.10" //NOPMD
  }
}

