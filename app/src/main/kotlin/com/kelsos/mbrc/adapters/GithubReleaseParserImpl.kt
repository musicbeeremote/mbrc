package com.kelsos.mbrc.adapters

import com.kelsos.mbrc.feature.settings.domain.GithubReleaseParser
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class GithubRelease(
  @Json(name = "tag_name")
  val tagName: String
)

class GithubReleaseParserImpl(moshi: Moshi) : GithubReleaseParser {
  private val adapter = moshi.adapter(GithubRelease::class.java)

  override fun parseTagName(json: String): String? = adapter.fromJson(json)?.tagName
}
