package com.kelsos.mbrc.core.networking

/**
 * Whether the app is allowed to reach MusicBee over the local network.
 *
 * Android 17 (API 37) put local network access behind a runtime permission. Without it every
 * connection attempt fails in a way that is indistinguishable from the server being unreachable, so
 * the connection layer has to be able to tell the two apart: retrying cannot fix a missing
 * permission, and presenting it as a reconnection blames the network for something only the user
 * can resolve.
 *
 * The Android implementation lives in the app module; everything below it only asks the question.
 */
fun interface LocalNetworkAccess {
  fun isPermitted(): Boolean
}
