package com.kelsos.mbrc.ui.activities.nav;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.LfmRatingChanged;
import com.kelsos.mbrc.events.ui.OnMainFragmentOptionsInflated;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ScrobbleChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.UpdatePosition;
import com.kelsos.mbrc.events.ui.VolumeChange;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import roboguice.RoboGuice;
import timber.log.Timber;

@Singleton
public class MainActivity extends BaseActivity {
  private static final String PAUSED = "Paused";
  private static final String STOPPED = "Stopped";
  private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
  // Injects
  @Inject
  protected Bus bus;
  // Inject elements of the view
  @BindView(R.id.main_artist_label)
  TextView artistLabel;
  @BindView(R.id.main_title_label)
  TextView titleLabel;
  @BindView(R.id.main_label_album)
  TextView albumLabel;
  @BindView(R.id.main_track_progress_current)
  TextView trackProgressCurrent;
  @BindView(R.id.main_track_duration_total)
  TextView trackDuration;
  @BindView(R.id.main_button_play_pause)
  ImageButton playPauseButton;
  @BindView(R.id.main_volume_seeker)
  SeekBar volumeBar;
  @BindView(R.id.main_track_progress_seeker)
  SeekBar progressBar;
  @BindView(R.id.main_mute_button)
  ImageButton muteButton;
  @BindView(R.id.main_shuffle_button)
  ImageButton shuffleButton;
  @BindView(R.id.main_repeat_button)
  ImageButton repeatButton;
  @BindView(R.id.main_album_cover_image_view)
  ImageView albumCover;
  private ShareActionProvider mShareActionProvider;
  private boolean userChangingVolume;
  private int previousVol;
  private ScheduledFuture mProgressUpdateHandler;
  private Menu menu;
  private SeekBar.OnSeekBarChangeListener volumeBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      if (fromUser) {
        final UserAction action = new UserAction(Protocol.PlayerVolume, String.valueOf(seekBar.getProgress()));
        postAction(action);
      }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
      userChangingVolume = false;
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
      userChangingVolume = true;
    }
  };

  private SeekBar.OnSeekBarChangeListener progressBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      if (fromUser && progress != previousVol) {
        final UserAction action = new UserAction(Protocol.NowPlayingPosition, String.valueOf(progress));
        postAction(action);
        previousVol = progress;
      }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
  };

  @OnClick(R.id.main_button_play_pause)
  void playButtonPressed() {
    final UserAction action = new UserAction(Protocol.PlayerPlayPause, true);
    postAction(action);
  }

  @OnClick(R.id.main_button_previous)
  void onPreviousButtonPressed() {
    final UserAction action = new UserAction(Protocol.PlayerPrevious, true);
    postAction(action);
  }

  @OnClick(R.id.main_button_next)
  void onNextButtonPressed() {
    final UserAction action = new UserAction(Protocol.PlayerNext, true);
    postAction(action);
  }

  @OnLongClick(R.id.main_button_play_pause)
  boolean onPlayerStopPressed() {
    final UserAction action = new UserAction(Protocol.PlayerStop, true);
    postAction(action);
    return true;
  }

  @OnClick(R.id.main_mute_button)
  void onMuteButtonPressed() {
    final UserAction action = new UserAction(Protocol.PlayerMute, Const.TOGGLE);
    postAction(action);
  }

  @OnClick(R.id.main_shuffle_button)
  void onShuffleButtonClicked() {
    final UserAction action = new UserAction(Protocol.PlayerShuffle, Const.TOGGLE);
    postAction(action);
  }

  @OnClick(R.id.main_repeat_button)
  void onRepeatButtonPressed() {
    final UserAction action = new UserAction(Protocol.PlayerRepeat, Const.TOGGLE);
    postAction(action);
  }

  /**
   * Posts a user action wrapped in a MessageEvent. The bus will
   * pass the MessageEvent through the Socket to the plugin.
   *
   * @param action Any kind of UserAction available in the {@link Protocol}
   */
  private void postAction(UserAction action) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    RoboGuice.getInjector(this).injectMembers(this);
    ButterKnife.bind(this);
    super.setup();
    progressBar.setOnSeekBarChangeListener(progressBarChangeListener);
    volumeBar.setOnSeekBarChangeListener(volumeBarChangeListener);
    userChangingVolume = false;
  }

  @Override
  public void onStart() {
    super.onStart();
    setTextViewTypeface();
    bus.register(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    final UserAction action = new UserAction(Protocol.NowPlayingPosition, true);
    bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_lastfm_scrobble:
        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerScrobble, Const.TOGGLE)));
        return true;
      case R.id.menu_rating_dialog:
        final RatingDialogFragment ratingDialog = new RatingDialogFragment();
        ratingDialog.show(getSupportFragmentManager(), "RatingDialog");
        return true;
      case R.id.menu_lastfm_love:
        bus.post(new MessageEvent(ProtocolEventType.UserAction,
            new UserAction(Protocol.NowPlayingLfmRating, Const.TOGGLE)));
        return true;
      default:
        return false;
    }
  }

  /**
   * Sets the typeface of the text views in the main activity to roboto.
   */
  private void setTextViewTypeface() {
    try {
      /* Marquee Hack */
      artistLabel.setSelected(true);
      titleLabel.setSelected(true);
      albumLabel.setSelected(true);

      Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
      Typeface robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/roboto_medium.ttf");
      Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
      artistLabel.setTypeface(robotoRegular);
      titleLabel.setTypeface(robotoLight);
      albumLabel.setTypeface(robotoMedium);
      trackProgressCurrent.setTypeface(robotoMedium);
      trackDuration.setTypeface(robotoMedium);
    } catch (Exception ignore) {
      Timber.d(ignore, "Failed");
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    this.menu = menu;
    MenuItem shareItem = menu.findItem(R.id.actionbar_share);
    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
    mShareActionProvider.setShareIntent(getShareIntent());
    bus.post(new OnMainFragmentOptionsInflated());
    return super.onCreateOptionsMenu(menu);
  }

  private Intent getShareIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    final String payload = String.format("Now Playing: %s - %s", artistLabel.getText(), titleLabel.getText());
    shareIntent.putExtra(Intent.EXTRA_TEXT, payload);
    return shareIntent;
  }

  @Subscribe
  public void handleCoverEvent(final CoverAvailable cevent) {
    if (albumCover == null) {
      return;
    }
    if (cevent.isAvailable()) {
      albumCover.setImageBitmap(cevent.getCover());
    } else {
      albumCover.setImageResource(R.drawable.ic_image_no_cover);
    }
  }

  @Subscribe
  public void handleShuffleChange(ShuffleChange change) {
    if (shuffleButton == null) {
      return;
    }

    final boolean shuffle = !ShuffleChange.OFF.equals(change.getShuffleState());
    final boolean autoDj = ShuffleChange.AUTODJ.equals(change.getShuffleState());

    int color = ContextCompat.getColor(this, shuffle ? R.color.accent : R.color.button_dark);
    shuffleButton.setColorFilter(color);

    shuffleButton.setImageResource(autoDj ? R.drawable.ic_headset_black_24dp : R.drawable.ic_shuffle_black_24dp);
  }

  @Subscribe
  public void updateRepeatButtonState(RepeatChange change) {
    if (repeatButton == null) {
      return;
    }

    final String mode = change.getMode();
    @ColorRes int colorId = R.color.accent;
    @DrawableRes int resId = R.drawable.ic_repeat_black_24dp;

    //noinspection StatementWithEmptyBody
    if (Repeat.ALL.equalsIgnoreCase(mode)) {
      // Do nothing already set above
    } else if (Repeat.ONE.equalsIgnoreCase(mode)) {
      resId = R.drawable.ic_repeat_one_black_24dp;
    } else {
      colorId = R.color.button_dark;
    }

    int color = ContextCompat.getColor(this, colorId);
    repeatButton.setImageResource(resId);
    repeatButton.setColorFilter(color);
  }

  @Subscribe
  public void updateVolumeData(VolumeChange change) {
    if (volumeBar == null) {
      return;
    }
    if (!userChangingVolume) {
      volumeBar.setProgress(change.getVolume());
    }
    if (muteButton == null) {
      return;
    }

    int color = ContextCompat.getColor(this, R.color.button_dark);
    muteButton.setColorFilter(color);
    muteButton.setImageResource(change.isMute()
        ? R.drawable.ic_volume_off_black_24dp
        : R.drawable.ic_volume_up_black_24dp);
  }

  @Subscribe
  public void handlePlayStateChange(final PlayStateChange change) {
    if (playPauseButton == null) {
      return;
    }
    int accentColor = ContextCompat.getColor(this, R.color.accent);
    @DrawableRes int resId;
    String tag;

    switch (change.getState()) {
      case Playing:
        resId = R.drawable.ic_pause_circle_filled_black_24dp;
        tag = "Playing";
        /* Start the animation if the track is playing*/
        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingPosition, true)));
        trackProgressAnimation();
        break;
      case Paused:
        resId = R.drawable.ic_play_circle_filled_black_24dp;
        tag = PAUSED;
        /* Stop the animation if the track is paused*/
        stopTrackProgressAnimation();
        break;
      case Stopped:
        resId = R.drawable.ic_play_circle_filled_black_24dp;
        tag = STOPPED;
        /* Stop the animation if the track is paused*/
        stopTrackProgressAnimation();
        activateStoppedState();
        break;
      default:
        resId = R.drawable.ic_play_circle_filled_black_24dp;
        tag = STOPPED;
        break;
    }

    playPauseButton.setColorFilter(accentColor);
    playPauseButton.setImageResource(resId);
    playPauseButton.setTag(tag);
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
    /* If the scheduled tasks is not null then cancel it and clear it along with the
    timer to create them anew */
    final int timePeriod = 1;
    stopTrackProgressAnimation();
    Object tag = playPauseButton.getTag();
    if (STOPPED.equals(tag) || PAUSED.equals(tag)) {
      return;
    }

    final Runnable updateProgress = () -> {

      int currentProgress = progressBar.getProgress() / 1000;
      final int currentMinutes = currentProgress / 60;
      final int currentSeconds = currentProgress % 60;

      runOnUiThread(() -> {
        try {
          if (progressBar == null) {
            return;
          }
          progressBar.setProgress(progressBar.getProgress() + 1000);
          trackProgressCurrent.setText(getString(R.string.playback_progress, currentMinutes, currentSeconds));
        } catch (Exception ex) {
          Timber.d(ex, "animation timer");
        }
      });
    };

    mProgressUpdateHandler = progressScheduler.scheduleAtFixedRate(updateProgress, 0, timePeriod, TimeUnit.SECONDS);
  }

  private void activateStoppedState() {
    if (trackProgressCurrent == null || progressBar == null) {
      return;
    }
    progressBar.setProgress(0);
    trackProgressCurrent.setText(getString(R.string.playback_progress, 0, 0));
  }

  @Subscribe
  public void handleTrackInfoChange(final TrackInfoChange change) {
    if (artistLabel == null) {
      return;
    }
    artistLabel.setText(change.getArtist());
    titleLabel.setText(change.getTitle());
    albumLabel.setText(TextUtils.isEmpty(change.getYear())
        ? change.getAlbum()
        : String.format("%s [%s]", change.getAlbum(), change.getYear()));

    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(getShareIntent());
    }
  }

  @Subscribe
  public void handleConnectionStatusChange(final ConnectionStatusChange change) {
    if (change.getStatus() == ConnectionStatus.CONNECTION_OFF) {
      stopTrackProgressAnimation();
      activateStoppedState();
    }
  }

  /**
   * Responsible for updating the displays and seekbar responsible for the display of the track
   * duration and the
   * current progress of playback
   */
  @Subscribe
  public void handlePositionUpdate(UpdatePosition position) {
    final int total = position.getTotal();
    final int current = position.getCurrent();
    if (trackProgressCurrent == null || progressBar == null || trackDuration == null) {
      return;
    }
    if (total == 0) {
      bus.post(new MessageEvent(UserInputEventType.RequestPosition));
      return;
    }
    int currentSeconds = current / 1000;
    int totalSeconds = total / 1000;

    final int currentMinutes = currentSeconds / 60;
    final int totalMinutes = totalSeconds / 60;

    currentSeconds %= 60;
    totalSeconds %= 60;
    final int finalTotalSeconds = totalSeconds;
    final int finalCurrentSeconds = currentSeconds;

    trackDuration.setText(getString(R.string.playback_progress, totalMinutes, finalTotalSeconds));
    trackProgressCurrent.setText(getString(R.string.playback_progress, currentMinutes, finalCurrentSeconds));

    progressBar.setMax(total);
    progressBar.setProgress(current);

    trackProgressAnimation();
  }

  @Subscribe
  public void handleScrobbleChange(ScrobbleChange event) {
    if (menu == null) {
      return;
    }
    final MenuItem scrobbleMenuItem = menu.findItem(R.id.menu_lastfm_scrobble);
    if (scrobbleMenuItem == null) {
      return;
    }
    scrobbleMenuItem.setChecked(event.isActive());
  }

  @Subscribe
  public void handleLfmLoveChange(LfmRatingChanged event) {
    if (menu == null) {
      return;
    }
    final MenuItem favoriteMenuItem = menu.findItem(R.id.menu_lastfm_love);
    if (favoriteMenuItem == null) {
      return;
    }
    switch (event.getStatus()) {
      case LOVED:
        favoriteMenuItem.setIcon(R.drawable.ic_favorite_black_24dp);
        break;
      default:
        favoriteMenuItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
        break;
    }
  }

  @Override
  protected int active() {
    return R.id.nav_home;
  }
}
