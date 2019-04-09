package com.kelsos.mbrc.networking.client

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GenericSocketMessage<T>(
  @param:Json(name = "context")
  var context: String,

  @param:Json(name = "data")
  var data: T
) where T : Any