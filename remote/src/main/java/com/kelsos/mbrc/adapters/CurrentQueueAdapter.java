package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
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
	private String nowPlayingPath;
	private AnimationDrawable peakOneAnimation;
	private AnimationDrawable peakTwoAnimation;
	private AnimationDrawable peakThreeAnimation;

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

		if (!TextUtils.isEmpty(nowPlayingPath)
				&& nowPlayingPath.equals(track.getPath())) {
			addPeakIfNotExists(context, view);
		} else {
			final View peakView = view.findViewById(R.id.peak_wrapper);
			if (peakView != null) {
				((ViewGroup) peakView.getParent()).removeView(peakView);
			}
		}


		overflow.setOnClickListener(v -> {
			PopupMenu menu = new PopupMenu(v.getContext(), v);
			menu.inflate(R.menu.popup_current_queue);
			menu.show();
        });

	}

	private void addPeakIfNotExists(Context context, View view) {
		final View peakView = view.findViewById(R.id.peak_wrapper);
		if (peakView != null) {
			return;
		}

		final View inView = view.findViewById(R.id.track_indicator_view);

		attachView(inView, inflatePeakMeter(context));

		startAnimation();
	}

	private void startAnimation() {
		if (peakOneAnimation == null) {
			return;
		}
		peakOneAnimation.start();
		peakTwoAnimation.start();
		peakThreeAnimation.start();
	}

	private void stopAnimation() {
		if (peakOneAnimation == null) {
			return;
		}
		peakOneAnimation.stop();
		peakTwoAnimation.stop();
		peakThreeAnimation.stop();
	}


	private View inflatePeakMeter(Context context) {
		final View inflate = LayoutInflater.from(context).inflate(R.layout.peak_meter, null, false);

		ImageView peakOne = (ImageView) inflate.findViewById(R.id.peak_one);
		ImageView peakTwo = (ImageView) inflate.findViewById(R.id.peak_two);
		ImageView peakThree = (ImageView) inflate.findViewById(R.id.peak_three);

		peakOneAnimation = (AnimationDrawable) peakOne.getDrawable();
		peakTwoAnimation = (AnimationDrawable) peakTwo.getDrawable();
		peakThreeAnimation = (AnimationDrawable) peakThree.getDrawable();
		return inflate;
	}

	private void attachView(View anchorView, View newView) {
		ViewGroup parent = (ViewGroup) anchorView.getParent();

		final LinearLayout.LayoutParams params = new LinearLayout
				.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		parent.addView(newView, parent.indexOfChild(anchorView) + 1, params);
	}


	public void setNowPlayingPath(String nowPlayingPath) {
		this.nowPlayingPath = nowPlayingPath;
	}
}
