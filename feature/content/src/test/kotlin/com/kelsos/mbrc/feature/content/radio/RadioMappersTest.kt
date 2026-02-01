package com.kelsos.mbrc.feature.content.radio

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.radio.RadioStationEntity
import com.kelsos.mbrc.core.networking.dto.RadioStationDto
import org.junit.Test

class RadioMappersTest {

  // region RadioStationDto to RadioStationEntity mapping

  @Test
  fun `toEntity should map name from dto`() {
    val dto = RadioStationDto(name = "Rock FM", url = "http://rockfm.stream")
    val entity = dto.toEntity()
    assertThat(entity.name).isEqualTo("Rock FM")
  }

  @Test
  fun `toEntity should map url from dto`() {
    val dto = RadioStationDto(name = "Rock FM", url = "http://rockfm.stream")
    val entity = dto.toEntity()
    assertThat(entity.url).isEqualTo("http://rockfm.stream")
  }

  @Test
  fun `toEntity should handle empty name`() {
    val dto = RadioStationDto(name = "", url = "http://stream.url")
    val entity = dto.toEntity()
    assertThat(entity.name).isEmpty()
  }

  @Test
  fun `toEntity should handle empty url`() {
    val dto = RadioStationDto(name = "Station", url = "")
    val entity = dto.toEntity()
    assertThat(entity.url).isEmpty()
  }

  @Test
  fun `toEntity should handle special characters in name`() {
    val dto = RadioStationDto(name = "BBC Radio 1 (UK)", url = "http://bbc.stream")
    val entity = dto.toEntity()
    assertThat(entity.name).isEqualTo("BBC Radio 1 (UK)")
  }

  @Test
  fun `toEntity should handle unicode characters`() {
    val dto = RadioStationDto(name = "Rádio Brasil", url = "http://brazil.stream")
    val entity = dto.toEntity()
    assertThat(entity.name).isEqualTo("Rádio Brasil")
  }

  @Test
  fun `toEntity should handle complex stream urls`() {
    val complexUrl = "http://stream.example.com:8080/live?key=abc123&format=mp3"
    val dto = RadioStationDto(name = "Test", url = complexUrl)
    val entity = dto.toEntity()
    assertThat(entity.url).isEqualTo(complexUrl)
  }

  @Test
  fun `toEntity should handle https urls`() {
    val dto = RadioStationDto(name = "Test", url = "https://secure.stream/audio")
    val entity = dto.toEntity()
    assertThat(entity.url).isEqualTo("https://secure.stream/audio")
  }

  @Test
  fun `toEntity should set default id to 0`() {
    val dto = RadioStationDto(name = "Test", url = "test")
    val entity = dto.toEntity()
    assertThat(entity.id).isEqualTo(0)
  }

  @Test
  fun `toEntity should set default dateAdded to 0`() {
    val dto = RadioStationDto(name = "Test", url = "test")
    val entity = dto.toEntity()
    assertThat(entity.dateAdded).isEqualTo(0)
  }

  @Test
  fun `toEntity should handle default dto values`() {
    val dto = RadioStationDto()
    val entity = dto.toEntity()
    assertThat(entity.name).isEmpty()
    assertThat(entity.url).isEmpty()
  }

  // endregion

  // region RadioStationEntity to RadioStation mapping

  @Test
  fun `toRadioStation should map id from entity`() {
    val entity = RadioStationEntity(name = "Test", url = "test", id = 42)
    val station = entity.toRadioStation()
    assertThat(station.id).isEqualTo(42)
  }

  @Test
  fun `toRadioStation should map name from entity`() {
    val entity = RadioStationEntity(name = "Jazz FM", url = "http://jazz.stream")
    val station = entity.toRadioStation()
    assertThat(station.name).isEqualTo("Jazz FM")
  }

  @Test
  fun `toRadioStation should map url from entity`() {
    val entity = RadioStationEntity(name = "Test", url = "http://test.stream")
    val station = entity.toRadioStation()
    assertThat(station.url).isEqualTo("http://test.stream")
  }

  @Test
  fun `toRadioStation should handle empty strings`() {
    val entity = RadioStationEntity(name = "", url = "")
    val station = entity.toRadioStation()
    assertThat(station.name).isEmpty()
    assertThat(station.url).isEmpty()
  }

  @Test
  fun `toRadioStation should handle entity with default id`() {
    val entity = RadioStationEntity(name = "Test", url = "test")
    val station = entity.toRadioStation()
    assertThat(station.id).isEqualTo(0)
  }

  @Test
  fun `toRadioStation should not include dateAdded in domain model`() {
    val entity = RadioStationEntity(name = "Test", url = "test", dateAdded = 123456789L, id = 1)
    val station = entity.toRadioStation()
    assertThat(station.id).isEqualTo(1)
    assertThat(station.name).isEqualTo("Test")
    assertThat(station.url).isEqualTo("test")
  }

  // endregion

  // region Object mapper tests

  @Test
  fun `RadioDtoMapper map should work correctly`() {
    val dto = RadioStationDto(name = "Classic Rock", url = "http://classic.stream")
    val entity = RadioDtoMapper.map(dto)
    assertThat(entity.name).isEqualTo("Classic Rock")
    assertThat(entity.url).isEqualTo("http://classic.stream")
  }

  @Test
  fun `RadioDaoMapper map should work correctly`() {
    val entity = RadioStationEntity(name = "Metal FM", url = "http://metal.stream", id = 10)
    val station = RadioDaoMapper.map(entity)
    assertThat(station.name).isEqualTo("Metal FM")
    assertThat(station.url).isEqualTo("http://metal.stream")
    assertThat(station.id).isEqualTo(10)
  }

  // endregion
}
