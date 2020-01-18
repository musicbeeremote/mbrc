package com.kelsos.mbrc.common.data

internal interface Mapper<in From, out To> {
  fun map(from: From): To
}