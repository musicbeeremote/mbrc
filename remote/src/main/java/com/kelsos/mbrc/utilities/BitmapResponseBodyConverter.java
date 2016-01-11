package com.kelsos.mbrc.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.ResponseBody;

import java.io.IOException;

import retrofit2.Converter;

public class BitmapResponseBodyConverter implements Converter<ResponseBody, Bitmap> {
  @Override
  public Bitmap convert(ResponseBody value) throws IOException {
    return BitmapFactory.decodeStream(value.byteStream());
  }
}
