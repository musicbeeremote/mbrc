package com.kelsos.mbrc.core.networking

/**
 * Readies the app for a connection the user asked for, before [ClientConnectionUseCase.connect].
 *
 * It clears what an earlier "disconnect" tap or a run of failed retries left behind, so the
 * reconnection logic is armed again, and starts the remote service that owns the connection, which
 * stops after a manual disconnect. Without it a connection made by switching hosts runs with no
 * notification or media session, and its first drop is ignored as if the user had asked for it.
 *
 * The Android implementation lives in the app module, next to the service it starts.
 */
fun interface UserConnectPreparer {
  fun prepare()
}
