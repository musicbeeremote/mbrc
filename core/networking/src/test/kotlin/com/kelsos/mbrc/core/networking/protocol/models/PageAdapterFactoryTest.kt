package com.kelsos.mbrc.core.networking.protocol.models

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.junit.Test

class PageAdapterFactoryTest {

  private val moshi = Moshi.Builder().add(PageAdapterFactory).build()
  private val type = Types.newParameterizedType(Page::class.java, String::class.java)
  private val adapter = moshi.adapter<Page<String>>(type)

  @Test
  fun `parses a normal page payload`() {
    val page = adapter.fromJson(
      """{"total":2,"offset":0,"limit":10,"data":["a","b"]}"""
    )

    assertThat(page).isNotNull()
    assertThat(page!!.total).isEqualTo(2)
    assertThat(page.offset).isEqualTo(0)
    assertThat(page.limit).isEqualTo(10)
    assertThat(page.data).containsExactly("a", "b").inOrder()
  }

  @Test
  fun `treats a bare empty array as a terminal empty page`() {
    val page = adapter.fromJson("""[]""")

    assertThat(page).isNotNull()
    assertThat(page!!.total).isEqualTo(0)
    assertThat(page.offset).isEqualTo(0)
    assertThat(page.data).isEmpty()
    // ApiBase.getAllPages breaks when offset + limit > total. The synthetic
    // page must satisfy that so pagination terminates instead of looping.
    assertThat(page.offset + page.limit).isGreaterThan(page.total)
  }

  @Test
  fun `treats a non-empty top-level array as a terminal empty page`() {
    val page = adapter.fromJson("""["unexpected","payload"]""")

    assertThat(page).isNotNull()
    assertThat(page!!.data).isEmpty()
    assertThat(page.offset + page.limit).isGreaterThan(page.total)
  }
}
