package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.features.library.data.TrackEntity
import com.kelsos.mbrc.interfaces.data.Mapper
import java.util.regex.Pattern

object TrackDtoMapper : Mapper<TrackDto, TrackEntity> {

  private val pattern = Pattern.compile(""".*(\d{4}).*""")
  private val matcher = pattern.matcher("")

  private val parseYear: (year: String) -> String = { year ->
    with(matcher.reset(year)) {
      if (find()) {
        group(1) ?: ""
      } else {
        ""
      }
    }
  }

  override fun map(from: TrackDto): TrackEntity {
    return TrackEntity(
      from.artist,
      from.title,
      from.src,
      from.trackno,
      from.disc,
      from.albumArtist,
      from.album,
      from.genre,
      from.year,
      parseYear(from.year)
    )
  }
}

fun TrackDto.toEntity(): TrackEntity {
  return TrackDtoMapper.map(this)
}
