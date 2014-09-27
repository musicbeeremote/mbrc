package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Cover;
import com.squareup.picasso.Picasso;

public class AlbumCursorAdapter extends CursorAdapter {
    public AlbumCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.ui_grid_square, parent, false);
    }

    @Override public void bindView(final View view, Context context, Cursor cursor) {
        final Album album = new Album();
        final ImageView imageView = ((ImageView)view.findViewById(R.id.ui_grid_image));
        final Uri uri = Uri.withAppendedPath(Cover.CONTENT_IMAGE_URI, album.getCoverHash());
        ((TextView) view.findViewById(R.id.line_one)).setText(album.getAlbumName());
        ((TextView) view.findViewById(R.id.line_two)).setText(album.getArtist().getArtistName());
        view.findViewById(R.id.ui_item_context_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.showContextMenu();
            }
        });

        Picasso.with(context)
                .load(uri)
                .fit()
                .placeholder(R.color.mbrc_transparent_dark)
                .error(R.drawable.ic_image_no_cover)
                .into(imageView);
    }
}
