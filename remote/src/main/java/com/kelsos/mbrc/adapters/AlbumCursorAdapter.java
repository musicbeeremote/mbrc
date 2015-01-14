package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.AlbumHelper;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Cover;
import com.kelsos.mbrc.dao.DaoSession;
import com.squareup.picasso.Picasso;

import java.io.File;

public class AlbumCursorAdapter extends CursorAdapter {
	private final DaoSession daoSession;
	private final File sdCard = Environment.getExternalStorageDirectory();
	private final File dir = new File(String.format("%s/Android/data/%s/cache",
			sdCard.getAbsolutePath(), BuildConfig.APPLICATION_ID));
	private final LayoutInflater inflater;

	@Inject
	public AlbumCursorAdapter(Context context, DaoSession daoSession) {
		super(context, null, 0);
		this.daoSession = daoSession;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = inflater.inflate(R.layout.griditem_album, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.lineOne = (TextView) view.findViewById(R.id.line_one);
		holder.lineTwo = (TextView) view.findViewById(R.id.line_two);
		holder.image = (ImageView) view.findViewById(R.id.ui_grid_image);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(final View view, Context context, Cursor cursor) {
		final Album album = AlbumHelper.fromCursor(cursor);
		album.__setDaoSession(daoSession);
		final Artist artist = album.getArtist();
		final Cover cover = album.getCover();

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.lineOne.setText(album.getName());
		holder.lineTwo.setText(artist != null ? artist.getName() : "");
		if (cover != null) {
			final File image = new File(dir, cover.getHash());

			Picasso.with(context)
					.load(image)
					.placeholder(R.drawable.ic_image_no_cover)
					.fit()
					.centerCrop()
					.tag(context)
					.into(holder.image);
		}
	}

	private static class ViewHolder {
		TextView lineOne;
		TextView lineTwo;
		ImageView image;
	}
}
