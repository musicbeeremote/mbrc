package com.kelsos.mbrc.core.networking

import java.io.IOException

/**
 * The service on the configured port answered the handshake with something that is not a plugin
 * message, such as another program listening on the same port. It is an [IOException] so every
 * caller that already handles a failed connection handles this one too.
 */
class UnexpectedHandshakeReplyException(cause: Throwable) :
  IOException("The service on this port did not answer like a MusicBee Remote plugin", cause)
