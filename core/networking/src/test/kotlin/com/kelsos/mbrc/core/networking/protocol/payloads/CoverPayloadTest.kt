package com.kelsos.mbrc.core.networking.protocol.payloads

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import org.junit.Test

class CoverPayloadTest {

  private val adapter = Moshi.Builder().build().adapter(CoverPayload::class.java)

  @Test
  fun `parses payload with populated cover`() {
    val payload = adapter.fromJson("""{"status":200,"cover":"base64data"}""")

    assertThat(payload).isNotNull()
    assertThat(payload!!.status).isEqualTo(CoverPayload.SUCCESS)
    assertThat(payload.cover).isEqualTo("base64data")
  }

  @Test
  fun `parses payload when cover field is explicitly null`() {
    val payload = adapter.fromJson("""{"status":404,"cover":null}""")

    assertThat(payload).isNotNull()
    assertThat(payload!!.status).isEqualTo(CoverPayload.NOT_FOUND)
    assertThat(payload.cover).isNull()
  }

  @Test
  fun `parses payload when cover field is missing`() {
    val payload = adapter.fromJson("""{"status":1}""")

    assertThat(payload).isNotNull()
    assertThat(payload!!.status).isEqualTo(CoverPayload.READY)
    assertThat(payload.cover).isNull()
  }
}
