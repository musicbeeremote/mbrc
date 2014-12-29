package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.Playlist;
import com.kelsos.mbrc.dao.PlaylistHelper;

public class PlaylistCursorAdapter extends CursorAdapter {
	private Typeface robotoRegular;
    public PlaylistCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
		robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_single, viewGroup, false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
		final Playlist playlist = PlaylistHelper.fromCursor(cursor);
		TextView lineOne = (TextView) view.findViewById(R.id.line_one);
		LinearLayout overflow = (LinearLayout) view.findViewById(R.id.ui_item_context_indicator);
		lineOne.setTypeface(robotoRegular);
		lineOne.setText(playlist.getName());

		overflow.setOnClickListener(v -> {
			PopupMenu menu = new PopupMenu(v.getContext(), v);
			menu.inflate(R.menu.popup_playlist);
			menu.show();
        });
    }
}
