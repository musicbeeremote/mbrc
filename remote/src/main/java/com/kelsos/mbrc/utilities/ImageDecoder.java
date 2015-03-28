package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.model.MainDataModel;
import roboguice.inject.InjectResource;
import roboguice.util.RoboAsyncTask;

public class ImageDecoder extends RoboAsyncTask<Bitmap> {
  @InjectResource(R.drawable.ic_image_no_cover) Drawable noCover;
  @Inject private MainDataModel model;
  private String image;

  public ImageDecoder(Context context, String image) {
    super(context);
    this.image = image;
  }

  public Bitmap call() {
    try {
      byte[] decodedImage = Base64.decode(image, Base64.DEFAULT);
      return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    } catch (Exception ex) {
      if (BuildConfig.DEBUG) {
        Log.d("mbrc-log", "image processing", ex);
      }
    }
    return ((BitmapDrawable) noCover).getBitmap();
  }

  @Override protected void onSuccess(Bitmap result) {
    model.setAlbumCover(result);
  }
}
