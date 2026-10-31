package com.kelsos.mbrc.core.networking.connectlink

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkResult.Invalid
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkResult.Valid
import org.junit.Test

class ConnectLinkParserTest {

  private val serverId = "3f2b8c1e-9d4a-4b6f-8e2d-1a7c5b9e0f34"

  private fun valid(link: String): ConnectLink {
    val result = ConnectLinkParser.parse(link)
    assertWithMessage("parse($link)").that(result).isInstanceOf(Valid::class.java)
    return (result as Valid).link
  }

  private fun errorOf(link: String): ConnectLinkError? =
    (ConnectLinkParser.parse(link) as? Invalid)?.error

  @Test
  fun `the single host canonical example parses`() {
    val link = valid("https://mbrc.kelsos.net/connect#v=1&h=192.168.1.5&p=3000")

    assertThat(link).isEqualTo(
      ConnectLink(
        hosts = listOf("192.168.1.5"),
        port = 3000,
        name = null,
        serverId = null,
        pairingCode = null
      )
    )
  }

  @Test
  fun `the two host canonical example keeps the hosts in link order`() {
    val link = valid(
      "https://mbrc.kelsos.net/connect#v=1&h=192.168.1.5,172.20.48.1&p=3000" +
        "&id=$serverId&n=LIVING-ROOM-PC"
    )

    assertThat(link.hosts).containsExactly("192.168.1.5", "172.20.48.1").inOrder()
    assertThat(link.name).isEqualTo("LIVING-ROOM-PC")
    assertThat(link.serverId).isEqualTo(serverId)
  }

  @Test
  fun `the pairing code canonical example decodes the name and keeps the code`() {
    val link = valid(
      "https://mbrc.kelsos.net/connect#v=1&h=192.168.1.5&p=3000&id=$serverId&n=Den%20PC&c=482913"
    )

    assertThat(link.name).isEqualTo("Den PC")
    assertThat(link.pairingCode).isEqualTo("482913")
  }

  @Test
  fun `the payload is read from the query string when there is no fragment`() {
    val link = valid("https://mbrc.kelsos.net/connect?v=1&h=10.0.0.3&p=3000&n=Den%20PC")

    assertThat(link.hosts).containsExactly("10.0.0.3")
    assertThat(link.name).isEqualTo("Den PC")
  }

  @Test
  fun `the fragment wins over the query string`() {
    val link = valid("https://mbrc.kelsos.net/connect?v=1&h=10.0.0.3&p=1#v=1&h=10.0.0.4&p=3000")

    assertThat(link.hosts).containsExactly("10.0.0.4")
    assertThat(link.port).isEqualTo(3000)
  }

  @Test
  fun `a plus in the name stays a plus`() {
    assertThat(valid("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000&n=A+B").name)
      .isEqualTo("A+B")
  }

  @Test
  fun `unknown keys are ignored`() {
    val link = valid("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000&k=abc&h6=fe80%3A%3A1")

    assertThat(link.hosts).containsExactly("10.0.0.3")
  }

  @Test
  fun `a newer schema asks for an app update`() {
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=2&h=10.0.0.3&p=3000"))
      .isEqualTo(ConnectLinkError.NewerVersion)
  }

  @Test
  fun `a missing or broken version is malformed`() {
    assertThat(errorOf("https://mbrc.kelsos.net/connect#h=10.0.0.3&p=3000"))
      .isEqualTo(ConnectLinkError.Malformed)
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=0&h=10.0.0.3&p=3000"))
      .isEqualTo(ConnectLinkError.Malformed)
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=one&h=10.0.0.3&p=3000"))
      .isEqualTo(ConnectLinkError.Malformed)
  }

  @Test
  fun `a missing host or port is malformed`() {
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=1&p=3000"))
      .isEqualTo(ConnectLinkError.Malformed)
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3"))
      .isEqualTo(ConnectLinkError.Malformed)
    assertThat(errorOf("https://mbrc.kelsos.net/connect"))
      .isEqualTo(ConnectLinkError.Malformed)
  }

  @Test
  fun `public addresses are dropped and the private ones kept`() {
    val link = valid("https://mbrc.kelsos.net/connect#v=1&h=8.8.8.8,192.168.1.5,172.32.0.1&p=3000")

    assertThat(link.hosts).containsExactly("192.168.1.5")
  }

  @Test
  fun `a link with only public addresses is refused`() {
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=1&h=8.8.8.8,1.1.1.1&p=3000"))
      .isEqualTo(ConnectLinkError.NoLocalAddress)
  }

  @Test
  fun `every private range is accepted, including the Tailscale overlay`() {
    val link = valid(
      "https://mbrc.kelsos.net/connect#v=1&h=10.1.2.3,172.16.0.1,172.31.255.254," +
        "192.168.0.1,100.64.0.1,100.127.255.1&p=3000"
    )

    assertThat(link.hosts).hasSize(6)
  }

  @Test
  fun `more than eight hosts is malformed`() {
    val nine = (1..9).joinToString(",") { "10.0.0.$it" }

    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=1&h=$nine&p=3000"))
      .isEqualTo(ConnectLinkError.Malformed)
  }

  @Test
  fun `a host name or a badly formed address is malformed rather than looked up`() {
    listOf("musicbee.local", "10.0.0", "10.0.0.256", "010.0.0.1", "10.0.0.-1", "").forEach { host ->
      assertWithMessage("h=$host")
        .that(errorOf("https://mbrc.kelsos.net/connect#v=1&h=$host&p=3000"))
        .isEqualTo(ConnectLinkError.Malformed)
    }
  }

  @Test
  fun `a port out of range is malformed`() {
    listOf("0", "65536", "-1", "port").forEach { port ->
      assertWithMessage("p=$port")
        .that(errorOf("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=$port"))
        .isEqualTo(ConnectLinkError.Malformed)
    }
  }

  @Test
  fun `a pairing code that is not six digits is malformed`() {
    listOf("12345", "1234567", "12345a").forEach { code ->
      assertWithMessage("c=$code")
        .that(errorOf("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000&c=$code"))
        .isEqualTo(ConnectLinkError.Malformed)
    }
  }

  @Test
  fun `a server id that is not a uuid is malformed`() {
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000&id=not-a-uuid"))
      .isEqualTo(ConnectLinkError.Malformed)
  }

  @Test
  fun `a name longer than 64 characters is malformed`() {
    val name = "x".repeat(ConnectLinkParser.MAX_NAME_LENGTH + 1)

    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000&n=$name"))
      .isEqualTo(ConnectLinkError.Malformed)
  }

  @Test
  fun `a broken percent escape is malformed`() {
    assertThat(errorOf("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000&n=%zz"))
      .isEqualTo(ConnectLinkError.Malformed)
  }

  @Test
  fun `a link to another site or page is not a connect link`() {
    listOf(
      "https://example.com/connect#v=1&h=10.0.0.3&p=3000",
      "https://mbrc.kelsos.net/help#v=1&h=10.0.0.3&p=3000",
      "http://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000",
      "mbrc://connect#v=1&h=10.0.0.3&p=3000",
      "not a link"
    ).forEach { link ->
      assertWithMessage(link).that(errorOf(link)).isEqualTo(ConnectLinkError.NotAConnectLink)
    }
  }
}
