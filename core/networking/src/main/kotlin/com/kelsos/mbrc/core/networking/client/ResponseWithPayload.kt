package com.kelsos.mbrc.core.networking.client

data class ResponseWithPayload<P, T>(val payload: P, val response: T)
