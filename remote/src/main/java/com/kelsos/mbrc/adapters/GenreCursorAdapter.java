package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.GenreHelper;

public class GenreCursorAdapter extends CursorAdapter {

	private LayoutInflater inflater;

	@Inject
	public GenreCursorAdapter(Context context) {
		super(context, null, 0);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		final View view = inflater.inflate(R.layout.listitem_single, viewGroup, false);
		ViewHolder holder = new ViewHolder();
		holder.lineOne = (TextView) view.findViewById(R.id.line_one);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(final View view, final Context context, Cursor cursor) {
		final Genre genre = GenreHelper.fromCursor(cursor);
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.lineOne.setText(genre.getName());
	}

	public static class ViewHolder {
		TextView lineOne;
	}
}
