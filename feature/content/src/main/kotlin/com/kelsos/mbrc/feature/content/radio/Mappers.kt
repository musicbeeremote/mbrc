package com.kelsos.mbrc.feature.content.radio

import com.kelsos.mbrc.core.common.data.Mapper
import com.kelsos.mbrc.core.data.radio.RadioStation
import com.kelsos.mbrc.core.data.radio.RadioStationEntity
import com.kelsos.mbrc.core.networking.dto.RadioStationDto

object RadioDtoMapper : Mapper<RadioStationDto, RadioStationEntity> {
  override fun map(from: RadioStationDto): RadioStationEntity =
    RadioStationEntity(from.name, from.url)
}

object RadioDaoMapper : Mapper<RadioStationEntity, RadioStation> {
  override fun map(from: RadioStationEntity): RadioStation = RadioStation(
    name = from.name,
    url = from.url,
    id = from.id
  )
}

fun RadioStationDto.toEntity(): RadioStationEntity = RadioDtoMapper.map(this)

fun RadioStationEntity.toRadioStation(): RadioStation = RadioDaoMapper.map(this)
