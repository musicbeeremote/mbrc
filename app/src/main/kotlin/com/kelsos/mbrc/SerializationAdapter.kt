package com.kelsos.mbrc

interface SerializationAdapter {
  fun stringify(`object`: Any): String
}
