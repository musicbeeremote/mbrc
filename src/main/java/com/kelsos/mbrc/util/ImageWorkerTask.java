package com.kelsos.mbrc.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;
import com.kelsos.mbrc.data.Cover;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

public class ImageWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String data;
    private final ContentResolver mResolver;

    public ImageWorkerTask(ImageView imageView, ContentResolver mResolver) {
        this.mResolver = mResolver;
        this.imageViewReference = new WeakReference<>(imageView);
    }

    @Override protected Bitmap doInBackground(String... params) {
        data = params[0];
        Bitmap cover = null;
        try {
            final Uri uri = Uri.withAppendedPath(Cover.CONTENT_IMAGE_URI, data);
            final ParcelFileDescriptor descriptor = mResolver.openFileDescriptor(uri, "r");
            if (descriptor != null) {
                final FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
                options.inSampleSize = RemoteUtils.inSampleSize(options, 150, 150);
                options.inJustDecodeBounds = false;
                cover = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return cover;
    }

    @Override protected void onPostExecute(Bitmap bitmap) {
        final ImageView imageView = imageViewReference.get();
        if (bitmap != null) {
            if (imageView != null && imageView.getTag().equals(data)) {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            if (imageView != null) {
                imageView.setImageResource(com.kelsos.mbrc.R.drawable.ic_image_no_cover);
            }
        }
    }
}
