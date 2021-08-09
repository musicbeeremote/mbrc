package com.kelsos.mbrc.features.radio

import com.kelsos.mbrc.common.data.Mapper

object RadioDaoMapper : Mapper<RadioStationEntity, RadioStation> {
  override fun map(from: RadioStationEntity): RadioStation = RadioStation(
    name = from.name,
    url = from.url,
    id = from.id
  )
}

fun RadioStationEntity.toRadioStation(): RadioStation {
  return RadioDaoMapper.map(this)
}
