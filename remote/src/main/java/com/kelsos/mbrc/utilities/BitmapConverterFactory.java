package com.kelsos.mbrc.utilities;

import android.graphics.Bitmap;

import okhttp3.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain retrofit2.Converter.Factory} which decodes bitmaps.
 * <p>
 * This converter only applies to {@link Bitmap} items.
 */
public class BitmapConverterFactory extends Converter.Factory {

  public static BitmapConverterFactory create() {
    return new BitmapConverterFactory();
  }

  private BitmapConverterFactory() {

  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
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
