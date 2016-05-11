package com.kelsos.mbrc.utilities

import android.content.Context
import com.google.inject.Inject
import com.kelsos.mbrc.dao.CoverDao
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import timber.log.Timber
import java.io.File
import java.io.IOException

class CoverDownloader {
  @Inject private lateinit var client: OkHttpClient
  @Inject private lateinit var manager: SettingsManager
  @Inject private lateinit var context: Context

  private var baseUrl: HttpUrl? = null
  private var coverDirectory: File? = null

  fun download(covers: List<CoverDao>) {
    val filesDir = context.filesDir
    coverDirectory = File(filesDir, "covers")
    val mkdirs = coverDirectory!!.mkdirs()
    Timber.v("[Cover] directory created [%s] [%s]", coverDirectory!!.absolutePath, mkdirs)

    val settings = manager.default ?: return

    baseUrl = HttpUrl.Builder()
        .host(settings.address)
        .scheme("http")
        .port(settings.port)
        .addPathSegment("library")
        .addPathSegment("covers")
        .build()

    rx.Observable.from(covers).window(5).subscribe {
      it.subscribe loop@{
        val file = File(coverDirectory, it.hash)
        if (file.exists()) {
          return@loop
        }

        try {
          download(it, file)
        } catch (e: IOException) {
          Timber.e(e, "On file download")
        }
      }
    }
  }

  @Throws(IOException::class)
  private fun download(cover: CoverDao, file: File) {
    val httpUrl = baseUrl!!.newBuilder()
        .addPathSegment(cover.id.toString())
        .addPathSegment("raw")
        .build()

    val request = Request.Builder().url(httpUrl).get().build()

    val response = client.newCall(request).execute()

    if (response.isSuccessful) {
      val sink = Okio.buffer(Okio.sink(file))
      sink.writeAll(response.body().source())
      sink.close()
      Timber.v("[Cover] downloaded [%s]", file.absolutePath)
    }
  }
}
