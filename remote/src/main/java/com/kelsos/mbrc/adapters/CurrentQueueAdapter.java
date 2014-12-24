package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dao.QueueTrackHelper;
import com.mobeta.android.dslv.DragSortCursorAdapter;

public class CurrentQueueAdapter extends DragSortCursorAdapter {

	@SuppressWarnings("UnusedDeclaration")
	public CurrentQueueAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@SuppressWarnings("UnusedDeclaration")
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

	@SuppressWarnings("ResourceType")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView trackArtist = (TextView) view.findViewById(R.id.trackArtist);
		TextView trackTitle = (TextView) view.findViewById(R.id.trackTitle);

		LinearLayout overflow = (LinearLayout) view.findViewById(R.id.list_overflow);

		final QueueTrack track = QueueTrackHelper.fromCursor(mCursor);

		final String title = track.getTitle();
		final String artist = track.getArtist();
		trackArtist.setText(artist);
		trackTitle.setText(title);

		startPeakAnimation(context, view);

		overflow.setOnClickListener(v -> {
			PopupMenu menu = new PopupMenu(v.getContext(), v);
			menu.inflate(R.menu.popup_current_queue);
			menu.show();
        });

	}

	private void startPeakAnimation(Context context, View view) {
		final View peakView = view.findViewById(R.id.peak_wrapper);
		if (peakView != null) {
			return;
		}

		final View inView = view.findViewById(R.id.track_indicator_view);
		final View inflate = LayoutInflater.from(context).inflate(R.layout.peak_meter, null, false);

		ImageView peakOne = (ImageView) inflate.findViewById(R.id.peak_one);
		ImageView peakTwo = (ImageView) inflate.findViewById(R.id.peak_two);
		ImageView peakThree = (ImageView) inflate.findViewById(R.id.peak_three);

		AnimationDrawable peakOneAnimation = (AnimationDrawable) peakOne.getDrawable();
		AnimationDrawable peakTwoAnimation = (AnimationDrawable) peakTwo.getDrawable();
		AnimationDrawable peakThreeAnimation = (AnimationDrawable) peakThree.getDrawable();

		ViewGroup parent = (ViewGroup) inView.getParent();

		final LinearLayout.LayoutParams params = new LinearLayout
				.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		parent.addView(inflate, parent.indexOfChild(inView) + 1, params);

		peakOneAnimation.start();
		peakTwoAnimation.start();
		peakThreeAnimation.start();
	}
}
