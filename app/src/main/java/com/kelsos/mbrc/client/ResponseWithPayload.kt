package com.kelsos.mbrc.client

data class ResponseWithPayload<P, T>(
  val payload: P,
  val response: T,
)
