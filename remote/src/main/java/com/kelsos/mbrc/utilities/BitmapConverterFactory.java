package com.kelsos.mbrc.utilities;

import android.graphics.Bitmap;

import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

/**
 * A {@linkplain retrofit.Converter.Factory} which decodes bitmaps.
 * <p>
 *  This converter only applies to {@link Bitmap} items.
 */
public class BitmapConverterFactory extends Converter.Factory {

  public static BitmapConverterFactory create() {
    return new BitmapConverterFactory();
  }

  private BitmapConverterFactory() {

  }

  @Override
  public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {

    if (!(type instanceof Class<?>)) {
      return null;
    }

    Class<?> c = (Class<?>) type;
    if (!Bitmap.class.isAssignableFrom(c)) {
      return null;
    }

    return new BitmapResponseBodyConverter();
  }
}
