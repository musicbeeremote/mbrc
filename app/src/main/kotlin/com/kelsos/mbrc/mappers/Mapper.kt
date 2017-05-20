package com.kelsos.mbrc.mappers

internal interface Mapper<in From, out To> {
  fun map(from: From): To
}
