package com.kelsos.mbrc.core.common.data

interface Mapper<From, To> {
  fun map(from: From): To
}
