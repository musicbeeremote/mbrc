package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.interfaces.data.Mapper

object RadioDaoMapper : Mapper<RadioStationEntity, RadioStation> {
  override fun map(from: RadioStationEntity): RadioStation {
    return RadioStation(
      from.name,
      from.url,
      from.id
    )
  }
}

fun RadioStationEntity.toRadioStation(): RadioStation {
  return RadioDaoMapper.map(this)
}
