package com.kelsos.mbrc.networking.discovery

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import arrow.core.Either
import arrow.core.flatMap
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.release
import arrow.resilience.Schedule
import arrow.resilience.retry
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.connections.toConnection
import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.DatagramPacket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.MulticastSocket
import kotlin.time.Duration.Companion.seconds

class RemoteServiceDiscoveryImpl(
  private val manager: WifiManager,
  private val connectivityManager: ConnectivityManager,
  private val moshi: Moshi,
  private val dispatchers: AppCoroutineDispatchers,
) : RemoteServiceDiscovery {
  private var group: InetAddress = InetAddress.getByName(DISCOVERY_ADDRESS)
  private val adapter by lazy { moshi.adapter(DiscoveryMessage::class.java) }

  private val lock: Resource<WifiManager.MulticastLock>
    get() =
      resource<WifiManager.MulticastLock> {
        Timber.v("Acquiring multicast lock")
        manager.createMulticastLock("locked").apply {
          setReferenceCounted(true)
          acquire()
        }
      } release {
        Timber.v("Releasing multicast lock")
        it.release()
      }

  private val socket: Resource<MulticastSocket>
    get() =
      resource {
        Timber.v("Joining multicast group $group")
        return@resource Either
          .catch {
            MulticastSocket(MULTICAST_PORT).apply {
              soTimeout = SO_TIMEOUT
              joinGroup(group)

              val message =
                adapter
                  .toJson(
                    DiscoveryMessage(
                      context = Protocol.DISCOVERY,
                      address = getWifiAddress() ?: "",
                    ),
                  ).toByteArray()

              val packet =
                DatagramPacket(
                  message,
                  message.size,
                  group,
                  MULTICAST_PORT,
                )
              send(packet)
            }
          }.fold({ throw it }, { it })
      } release { socket ->
        Timber.v("Leaving multicast group")
        socket.leaveGroup(group)
        socket.close()
      }

  private suspend fun MulticastSocket.listen(): Either<DiscoveryStop, ConnectionSettingsEntity> =
    Either
      .catch {
        Schedule
          .recurs<DiscoveryMessage>(RETRIES)
          .and(Schedule.spaced(1.seconds))
          .and(Schedule.doUntil { _, message -> message.context == NOTIFY })
          .zipRight(Schedule.identity())
          .repeat {
            Schedule
              .recurs<Throwable>(RETRIES)
              .and(Schedule.spaced(1.seconds))
              .retry { readMessage() }
          }.toConnection()
      }.mapLeft { DiscoveryStop.NotFound }

  private fun MulticastSocket.readMessage(): DiscoveryMessage {
    val buffer = ByteArray(size = 512)
    val packet = DatagramPacket(buffer, buffer.size)
    receive(packet)
    val message = String(packet.data.copyOfRange(0, packet.length), Charsets.UTF_8)
    Timber.v("received discovery message $message")
    return checkNotNull(adapter.fromJson(message))
  }

  override suspend fun discover(): Either<DiscoveryStop, ConnectionSettingsEntity> =
    withContext(dispatchers.io) {
      isWifiConnected.flatMap {
        resource {
          lock.bind()
          socket.bind()
        } use {
          it.listen()
        }
      }
    }

  private fun getWifiAddress(): String? {
    val activeNetwork = checkNotNull(connectivityManager.activeNetwork)
    val linkProperties = checkNotNull(connectivityManager.getLinkProperties(activeNetwork))
    return linkProperties.linkAddresses
      .filter {
        it.address is Inet4Address
      }.map { it.address.hostAddress }
      .first()
  }

  private val isWifiConnected: Either<DiscoveryStop, Boolean>
    get() =
      Either
        .catch {
          val activeNetwork = checkNotNull(connectivityManager.activeNetwork)
          val capabilities = checkNotNull(connectivityManager.getNetworkCapabilities(activeNetwork))
          return@catch checkNotNull(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
        }.mapLeft { DiscoveryStop.NoWifi }

  companion object {
    private const val RETRIES = 4L
    private const val NOTIFY = "notify"
    private const val SO_TIMEOUT = 15 * 1000
    private const val MULTICAST_PORT = 45345
    private const val DISCOVERY_ADDRESS = "239.1.5.10"
  }
}
