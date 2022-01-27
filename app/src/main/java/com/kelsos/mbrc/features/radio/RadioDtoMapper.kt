package com.kelsos.mbrc.features.radio

import com.kelsos.mbrc.common.data.Mapper

object RadioDtoMapper :
  Mapper<RadioStationDto, RadioStationEntity> {
  override fun map(from: RadioStationDto): RadioStationEntity =
    RadioStationEntity(from.name, from.url)
}

fun RadioStationDto.toEntity(): RadioStationEntity = RadioDtoMapper.map(this)
