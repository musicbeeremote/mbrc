package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.kelsos.mbrc.R;

public class NowPlayingAdapter extends CursorAdapter {


    public NowPlayingAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.ui_list_track_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ((TextView) view.findViewById(R.id.trackTitle)).setText('d');
        ((TextView) view.findViewById(R.id.trackArtist)).setText('d');
        //(ImageView) view.findViewById(R.id.listview_item_image);
    }
}
