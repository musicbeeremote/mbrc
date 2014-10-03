package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kelsos.mbrc.R;

public class ArtistCursorAdapter extends CursorAdapter {

    public ArtistCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.ui_list_single, viewGroup, false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

    }
}
