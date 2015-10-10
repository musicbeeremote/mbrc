package com.kelsos.mbrc.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Converter;

public class BitmapResponseBodyConverter implements Converter<ResponseBody, Bitmap> {
  @Override
  public Bitmap convert(ResponseBody value) throws IOException {
    return BitmapFactory.decodeStream(value.byteStream());
  }
}
