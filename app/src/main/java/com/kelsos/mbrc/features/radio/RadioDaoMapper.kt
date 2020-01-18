package com.kelsos.mbrc.features.radio

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.radio.data.RadioStationEntity
import com.kelsos.mbrc.features.radio.domain.RadioStation

object RadioDaoMapper :
  Mapper<RadioStationEntity, RadioStation> {
  override fun map(from: RadioStationEntity): RadioStation = RadioStation(
    name = from.name,
    url = from.url,
    id = from.id
  )
}