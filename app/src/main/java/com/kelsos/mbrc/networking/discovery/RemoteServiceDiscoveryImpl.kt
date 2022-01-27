package com.kelsos.mbrc.networking.discovery

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import arrow.core.Either
import arrow.core.left
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.connections.toConnection
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.Locale

class RemoteServiceDiscoveryImpl(
  private val manager: WifiManager,
  private val connectivityManager: ConnectivityManager,
  private val moshi: Moshi,
  private val dispatchers: AppCoroutineDispatchers
) : RemoteServiceDiscovery {
  private var multicastLock: WifiManager.MulticastLock? = null
  private var group: InetAddress? = null

  private val adapter by lazy { moshi.adapter(DiscoveryMessage::class.java) }

  override suspend fun discover(): Either<DiscoveryStop, ConnectionSettingsEntity> {
    if (!isWifiConnected()) {
      return DiscoveryStop.NoWifi.left()
    }

    multicastLock = manager.createMulticastLock("locked").apply {
      setReferenceCounted(true)
      acquire()
    }

    Timber.v("Starting remote service discovery")

    val socket = create()
    val entity = retryIO(times = 4) {
      withContext(dispatchers.io) {
        val buffer = ByteArray(size = 512)
        val discoveryMessage = with(DatagramPacket(buffer, buffer.size)) {
          socket.receive(this)
          val message = String(data.copyOfRange(0, length), Charsets.UTF_8)
          Timber.v(message)
          adapter.fromJson(message)
        }
        if (discoveryMessage == null || discoveryMessage.context != NOTIFY) {
          throw IOException("unexpected message")
        }
        discoveryMessage.toConnection()
      }
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
      return DiscoveryStop.NotFound.left()
    }

    return entity.mapLeft { DiscoveryStop.Complete }
  }

  private fun stopDiscovery() {
    multicastLock?.release()
    multicastLock = null
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
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
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
            message,
            message.size,
            group,
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
    private const val DISCOVERY_ADDRESS = "239.1.5.10"
  }
}
