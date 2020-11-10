package com.kelsos.mbrc.networking.discovery

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import arrow.core.Either
import com.kelsos.mbrc.networking.connections.ConnectionMapper
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.Moshi
import timber.log.Timber
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.*

class RemoteServiceDiscoveryImpl(
  private val manager: WifiManager,
  private val connectivityManager: ConnectivityManager,
  private val moshi: Moshi
) : RemoteServiceDiscovery {
  private var mLock: WifiManager.MulticastLock? = null
  private var group: InetAddress? = null

  private val adapter by lazy { moshi.adapter(DiscoveryMessage::class.java) }

  override suspend fun discover(): Either<DiscoveryStop, ConnectionSettingsEntity> {
    if (!isWifiConnected()) {
      return Either.left(DiscoveryStop.NoWifi)
    }

    mLock = manager.createMulticastLock("locked")
    mLock?.let {
      it.setReferenceCounted(true)
      it.acquire()
    }

    Timber.v("Starting remote service discovery")

    val mapper = ConnectionMapper()
    val socket = create()
    val entity = retryIO(times = 4) {
      val buffer = ByteArray(512)
      val discoveryMessage = with(DatagramPacket(buffer, buffer.size)) {
        socket.receive(this)
        val message = String(data.copyOfRange(0, length), Charsets.UTF_8)
        Timber.v(message)
        adapter.fromJson(message)
      }
      if (discoveryMessage == null || discoveryMessage.context != NOTIFY) {
        throw IOException("unexpected message")
      }
      mapper.map(discoveryMessage)
    }

    try {
      socket.leaveGroup(group)
      socket.close()
    } catch (e: IOException) {
      Timber.v("While cleaning up the discovery ${e.message}")
    } finally {
      stopDiscovery()
    }

    if (entity.isLeft()) {
      return Either.left(DiscoveryStop.NotFound)
    }

    return entity.mapLeft { DiscoveryStop.Complete }
  }

  private fun stopDiscovery() {
    mLock?.release()
    mLock = null
  }

  private fun getWifiAddress(): String {
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

  private fun isWifiConnected(): Boolean {
    val current = connectivityManager.activeNetworkInfo
    return current != null && current.type == ConnectivityManager.TYPE_WIFI
  }

  private fun create(): MulticastSocket {
    try {
      return MulticastSocket(MULTICAST_PORT).apply {
        soTimeout = SO_TIMEOUT
        joinGroup(
          InetAddress.getByName(DISCOVERY_ADDRESS).also {
            group = it
          }
        )

        val message = with(DiscoveryMessage()) {
          context = Protocol.DISCOVERY
          address = getWifiAddress()
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
    private const val DISCOVERY_ADDRESS = "239.1.5.10" // NOPMD
  }
}