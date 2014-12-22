package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.QueueTrackDao;
import com.mobeta.android.dslv.DragSortCursorAdapter;

public class CurrentQueueAdapter extends DragSortCursorAdapter {

	private int i = 1;

	public CurrentQueueAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public CurrentQueueAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public CurrentQueueAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_track, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView trackArtist = (TextView) view.findViewById(R.id.trackArtist);
		TextView trackTitle = (TextView) view.findViewById(R.id.trackTitle);
		final String title = mCursor.getString(mCursor.getColumnIndex(QueueTrackDao.Properties.Title.columnName));
		final String artist = mCursor.getString(mCursor.getColumnIndex(QueueTrackDao.Properties.Artist.columnName));
		trackArtist.setText(artist);
		trackTitle.setText(title);

	}
}
