package kelsos.mbremote.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.google.inject.Inject;
import kelsos.mbremote.Models.MainDataModel;
import roboguice.util.RoboAsyncTask;

public class ImageDecoder extends RoboAsyncTask<Bitmap> {

    @Inject private MainDataModel model;
    private String image;

    public ImageDecoder(Context context, String image) {
        super(context);
        this.image = image;
    }

    public Bitmap call() throws Exception {
        byte[] decodedImage = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    }

    @Override
    protected void onSuccess(Bitmap result)
    {
        model.setAlbumCover(result);
    }
}
