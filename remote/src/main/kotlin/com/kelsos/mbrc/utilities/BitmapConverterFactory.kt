package com.kelsos.mbrc.utilities

import android.graphics.Bitmap
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * A [retrofit2.Converter.Factory] which decodes bitmaps.
 *
 *
 * This converter only applies to [Bitmap] items.
 */
class BitmapConverterFactory private constructor() : Converter.Factory() {

  override fun responseBodyConverter(type: Type?,
                                     annotations: Array<Annotation>?,
                                     retrofit: Retrofit?): Converter<ResponseBody, *>? {
    if (type !is Class<*>) {
      return null
    }

    if (!Bitmap::class.java.isAssignableFrom(type)) {
      return null
    }

    return BitmapResponseBodyConverter()
  }

  companion object {

    fun create(): BitmapConverterFactory {
      return BitmapConverterFactory()
    }
  }
}
