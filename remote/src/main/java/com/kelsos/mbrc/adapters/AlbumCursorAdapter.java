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
import com.kelsos.mbrc.data.dbdata.LibraryAlbum;
import com.kelsos.mbrc.data.dbdata.LibraryCover;
import com.squareup.picasso.Picasso;

public class AlbumCursorAdapter extends CursorAdapter {
    public AlbumCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.ui_grid_square, parent, false);
    }

    @Override public void bindView(final View view, Context context, Cursor cursor) {

    }
}
