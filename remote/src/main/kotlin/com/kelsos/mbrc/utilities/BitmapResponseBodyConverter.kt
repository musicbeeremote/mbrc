package com.kelsos.mbrc.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

class BitmapResponseBodyConverter : Converter<ResponseBody, Bitmap> {
  @Throws(IOException::class)
  override fun convert(value: ResponseBody): Bitmap {
    return BitmapFactory.decodeStream(value.byteStream())
  }
}
