package com.kelsos.mbrc.interfaces.data

internal interface Mapper<in From, out To> {
  fun map(from: From): To
}