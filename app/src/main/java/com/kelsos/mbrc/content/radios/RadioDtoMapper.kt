package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.interfaces.data.Mapper

object RadioDtoMapper : Mapper<RadioStationDto, RadioStationEntity> {
  override fun map(from: RadioStationDto): RadioStationEntity {
    return RadioStationEntity(from.name, from.url)
  }
}

fun RadioStationDto.toEntity(): RadioStationEntity {
  return RadioDtoMapper.map(this)
}
