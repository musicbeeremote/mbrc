package com.kelsos.mbrc.ui.fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.data.model.PlayerState;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.Logger;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class MainFragment extends RoboFragment {
    /**
     * Total milliseconds in a second (1000)
     */
    public static final int MILLISECONDS = 1000;
    /**
     * Total seconds in a minute (60)
     */
    public static final int SECONDS = 60;
    public static final int TIME_PERIOD = 1;
	public static final int DELAY = 20;
	private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;

    @InjectView(R.id.main_track_progress_current)
    private TextView trackProgressCurrent;
    @InjectView(R.id.main_track_duration_total)
    private TextView trackDuration;
    @InjectView(R.id.main_track_progress_seeker)
    private SeekBar trackProgressSlider;
    @InjectView(R.id.main_album_cover_image_view)
    private ImageView albumCover;
    @InjectView(R.id.ratingWrapper)
    private LinearLayout ratingWrapper;
    @InjectView(R.id.track_rating_bar)
    private RatingBar trackRating;
    @Inject
    private RemoteApi api;
    @Inject
    private PlayerState playerStateModel;

    private SeekBar.OnSeekBarChangeListener durationSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                api.updatePosition(progress)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(resp -> handlePositionUpdate(resp.getPosition(), resp.getDuration()),
								Logger::LogThrowable);

            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    };
    private ScheduledFuture mProgressUpdateHandler;

	private RatingBar.OnRatingBarChangeListener ratingChangeListener = (ratingBar, v, b) -> {
        if (b) {

            AndroidObservable.bindFragment(this, api.updateRating(v))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(resp -> Ln.d("Success %b", resp.isSuccess()),
							error -> Ln.d("error %s", error.getMessage()));

        }
    };
    private ImageView.OnClickListener coverOnClick = new ImageView.OnClickListener() {

        private boolean isActive = false;

        @Override
        public void onClick(View view) {

            if (!isActive) {
                animateRatingBar();
            }
        }

        private void animateRatingBar() {
            final int fadeInDuration = 300;
            final int timeBetween = 3000;
            final int fadeOutDuration = 800;

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(fadeInDuration);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setStartOffset(fadeInDuration + timeBetween);
            fadeOut.setDuration(fadeOutDuration);

            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            animation.addAnimation(fadeOut);
            animation.setRepeatCount(1);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isActive = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isActive = false;
                    ratingWrapper.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            ratingWrapper.setVisibility(View.VISIBLE);
            ratingWrapper.startAnimation(animation);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ui_fragment_main, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		FragmentManager fragmentManager = getFragmentManager();
		final ButtonFragment fragment = ButtonFragment.newInstance();
		fragmentManager.beginTransaction()
				.replace(R.id.mbrc_controls, fragment)
				.commit();
	}

	@Override
    public void onStart() {
        super.onStart();
        setTextViewTypeface();
        registerListeners();

        AndroidObservable.bindFragment(this, Events.CoverAvailableNotification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notification -> updateAlbumCover(notification.getCover()),
						Logger::LogThrowable);

        AndroidObservable.bindFragment(this, api.getCurrentPosition())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.repeatWhen(a -> a.flatMap(n -> Observable.timer(DELAY, TimeUnit.SECONDS)))
				.subscribe(update -> handlePositionUpdate(update.getPosition(), update.getDuration()),
						Logger::LogThrowable);

        AndroidObservable.bindFragment(this, Events.TrackInfoChangeNotification)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleTrackInfoChange,
						Logger::LogThrowable);

        AndroidObservable.bindFragment(this, playerStateModel.observePlaystate())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(this::handlePlayStateChange, Logger::LogThrowable);


    }

    private void updateAlbumCover(final Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void registerListeners() {
		ratingWrapper.setVisibility(View.INVISIBLE);
		trackRating.setOnRatingBarChangeListener(ratingChangeListener);
		trackProgressSlider.setOnSeekBarChangeListener(durationSeekBarChangeListener);
		albumCover.setOnClickListener(coverOnClick);
    }

    /**
     * Sets the typeface of the text views in the main activity to roboto.
     */
    private void setTextViewTypeface() {
        try {
            Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
            trackProgressCurrent.setTypeface(robotoRegular);
            trackDuration.setTypeface(robotoRegular);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Ln.e(e, "setting typeface");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share, menu);
        MenuItem shareItem = menu.findItem(R.id.actionbar_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        updateShareIntent("", "");
        mShareActionProvider.setShareIntent(mShareIntent);
    }

    private void updateShareIntent(String artist, String title) {
        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        final String payload = String.format("Now Playing: %s - %s", artist, title);
        mShareIntent.putExtra(Intent.EXTRA_TEXT, payload);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_share:
                mShareActionProvider.setShareIntent(mShareIntent);
                return true;
            default:
                return false;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
	public void handleRatingChange(float rating) {
        if (trackRating != null) {
            trackRating.setRating(rating);
        }
    }

    private void handlePlayStateChange(PlayState state) {
        switch (state) {
            case PLAYING:
                trackProgressAnimation();
                break;
            case PAUSED:
                stopTrackProgressAnimation();
                break;
            default:
                stopTrackProgressAnimation();
                activateStoppedState();
                break;
        }
    }

    /**
     * If the track progress animation is running the the function stops it.
     */
    private void stopTrackProgressAnimation() {
        if (mProgressUpdateHandler != null) {
            mProgressUpdateHandler.cancel(true);
        }
    }

    /**
     * Starts the progress animation when called. If It was previously running then it restarts it.
     */
    private void trackProgressAnimation() {
        if (!isVisible()) {
            return;
        }
        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
        stopTrackProgressAnimation();

        final Runnable updateProgress = () -> {

            int currentProgress = trackProgressSlider.getProgress() / MILLISECONDS;
            final int currentMinutes = currentProgress / SECONDS;
            final int currentSeconds = currentProgress % SECONDS;

            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(() -> {
                try {
                    if (trackProgressSlider == null) {
                        return;
                    }
                    trackProgressSlider.setProgress(trackProgressSlider.getProgress() + MILLISECONDS);
                    trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
                } catch (Exception ex) {

                    if (BuildConfig.DEBUG) {
                        Log.d("mbrc-log:", "animation timer", ex);
                    }
                }
            });
        };

        mProgressUpdateHandler = progressScheduler.scheduleAtFixedRate(updateProgress, 0,
                TIME_PERIOD, TimeUnit.SECONDS);

    }

    private void activateStoppedState() {
        if (trackProgressCurrent == null || trackProgressSlider == null) {
            return;
        }
        trackProgressSlider.setProgress(0);
        trackProgressCurrent.setText("00:00");
    }

    public void handleTrackInfoChange(final TrackInfoChange change) {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(change.getTitle());
        actionBar.setSubtitle(change.getArtist());
        actionBar.setDisplayShowTitleEnabled(true);

        updateShareIntent(change.getArtist(), change.getTitle());
    }


    @SuppressWarnings("UnusedDeclaration")
	public void handleConnectionStatusChange(final ConnectionStatusChange change) {
        if (change.getStatus() == ConnectionStatusChange.Status.CONNECTION_OFF) {
            stopTrackProgressAnimation();
            activateStoppedState();
        }
    }

    /**
     * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
     * current progress of playback
     */

    public void handlePositionUpdate(int current, int total) {
        if (trackProgressCurrent == null || trackProgressSlider == null || trackDuration == null) {
            return;
        }
        if (total == 0) {
            new Message(EventType.REQUEST_POSITION);
            return;
        }
        int currentSeconds = current / MILLISECONDS;
        int totalSeconds = total / MILLISECONDS;

        final int currentMinutes = currentSeconds / SECONDS;
        final int totalMinutes = totalSeconds / SECONDS;

        currentSeconds %= SECONDS;
        totalSeconds %= SECONDS;
        final int finalTotalSeconds = totalSeconds;
        final int finalCurrentSeconds = currentSeconds;

        trackDuration.setText(String.format("%02d:%02d", totalMinutes, finalTotalSeconds));
        trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, finalCurrentSeconds));

        trackProgressSlider.setMax(total);
        trackProgressSlider.setProgress(current);

        trackProgressAnimation();
    }
}
