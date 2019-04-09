package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.interfaces.data.Mapper

class RadioDtoMapper : Mapper<RadioStationDto, RadioStationEntity> {
  override fun map(from: RadioStationDto): RadioStationEntity {
    return RadioStationEntity(from.name, from.url)
  }
}