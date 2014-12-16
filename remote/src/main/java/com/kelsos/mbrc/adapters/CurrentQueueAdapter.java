package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.QueueTrackDao;

public class CurrentQueueAdapter extends CursorAdapter {

    public CurrentQueueAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_track, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String title = cursor.getString(cursor.getColumnIndex(QueueTrackDao.Properties.Title.columnName));
        final String artist = cursor.getString(cursor.getColumnIndex(QueueTrackDao.Properties.Artist.columnName));
        ((TextView) view.findViewById(R.id.trackTitle)).setText(title);
        ((TextView) view.findViewById(R.id.trackArtist)).setText(artist);
        //(ImageView) view.findViewById(R.id.listview_item_image);
    }
}
