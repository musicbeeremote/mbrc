package com.kelsos.mbrc.features.radio

import com.kelsos.mbrc.features.radio.data.RadioStationEntity
import com.kelsos.mbrc.interfaces.data.Mapper

object RadioDtoMapper : Mapper<RadioStationDto, RadioStationEntity> {
  override fun map(from: RadioStationDto): RadioStationEntity {
    return RadioStationEntity(from.name, from.url)
  }
}