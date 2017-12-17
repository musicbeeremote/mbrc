package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.interfaces.data.Data

interface RadioStation : Data {
  val name: String
  val url: String
  var id: Long
}
