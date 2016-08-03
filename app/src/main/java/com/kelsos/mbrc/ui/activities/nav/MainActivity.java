package com.kelsos.mbrc.ui.activities.nav;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.PlayerState.State;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Repeat.Mode;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.LfmRatingChanged;
import com.kelsos.mbrc.events.ui.OnMainFragmentOptionsInflated;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ScrobbleChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.events.ui.ShuffleChange.ShuffleState;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.events.ui.UpdatePosition;
import com.kelsos.mbrc.events.ui.VolumeChange;
import com.kelsos.mbrc.helper.VolumeChangeHelper;
import com.kelsos.mbrc.presenters.MainViewPresenter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment;
import com.kelsos.mbrc.views.MainView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

@Singleton
public class MainActivity extends BaseActivity implements MainView {
  private static final String PAUSED = "Paused";
  private static final String STOPPED = "Stopped";
  private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
  // Injects
  @Inject RxBus bus;
  @Inject MainViewPresenter presenter;
  // Inject elements of the view
  @BindView(R.id.main_artist_label) TextView artistLabel;
  @BindView(R.id.main_title_label) TextView titleLabel;
  @BindView(R.id.main_label_album) TextView albumLabel;
  @BindView(R.id.main_track_progress_current) TextView trackProgressCurrent;
  @BindView(R.id.main_track_duration_total) TextView trackDuration;
  @BindView(R.id.main_button_play_pause) ImageButton playPauseButton;
  @BindView(R.id.main_volume_seeker) SeekBar volumeBar;
  @BindView(R.id.main_track_progress_seeker) SeekBar progressBar;
  @BindView(R.id.main_mute_button) ImageButton muteButton;
  @BindView(R.id.main_shuffle_button) ImageButton shuffleButton;
  @BindView(R.id.main_repeat_button) ImageButton repeatButton;
  @BindView(R.id.main_album_cover_image_view) ImageView albumCover;
  private ShareActionProvider mShareActionProvider;
  private int previousVol;
  private ScheduledFuture mProgressUpdateHandler;
  private Menu menu;

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
  private VolumeChangeHelper volumeChangeListener;
  private Scope scope;

  private void register() {
    this.bus.register(this, CoverChangedEvent.class, this::handleCoverEvent, true);
    this.bus.register(this, ShuffleChange.class, this::handleShuffleChange, true);
    this.bus.register(this, RepeatChange.class, this::updateRepeatButtonState, true);
    this.bus.register(this, VolumeChange.class, this::updateVolumeData, true);
    this.bus.register(this, PlayStateChange.class, this::handlePlayStateChange, true);
    this.bus.register(this, TrackInfoChangeEvent.class, this::handleTrackInfoChange, true);
    this.bus.register(this, ConnectionStatusChangeEvent.class, this::handleConnectionStatusChange, true);
    this.bus.register(this, UpdatePosition.class, this::handlePositionUpdate, true);
    this.bus.register(this, ScrobbleChange.class, this::handleScrobbleChange, true);
    this.bus.register(this, LfmRatingChanged.class, this::handleLfmLoveChange, true);
  }

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

  private void changeVolume(int volume) {
    postAction(UserAction.create(Protocol.PlayerVolume, volume));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    scope.installModules(new SmoothieActivityModule(this));
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    super.setup();
    volumeChangeListener = new VolumeChangeHelper(this::changeVolume);
    volumeBar.setOnSeekBarChangeListener(volumeChangeListener);
    progressBar.setOnSeekBarChangeListener(progressBarChangeListener);
  }

  @Override
  public void onStart() {
    super.onStart();
    setTextViewTypeface();
  }

  @Override
  public void onResume() {
    super.onResume();
    register();
    presenter.attach(this);
    presenter.requestNowPlayingPosition();
    presenter.load();
  }

  @Override
  protected void onPause() {
    super.onPause();
    presenter.detach();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_lastfm_scrobble:
        presenter.toggleScrobbling();
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

  private void handleCoverEvent(final CoverChangedEvent cevent) {
    updateCover(cevent.getCover());
  }

  @Override
  public void updateCover(@Nullable Bitmap cover) {
    if (albumCover == null) {
      return;
    }
    if (cover != null) {
      albumCover.setImageBitmap(cover);
    } else {
      albumCover.setImageResource(R.drawable.ic_image_no_cover);
    }
  }

  private void handleShuffleChange(ShuffleChange change) {
    updateShuffleState(change.getShuffleState());
  }

  @Override
  public void updateShuffleState(@ShuffleState String shuffleState) {
    if (shuffleButton == null) {
      return;
    }

    final boolean shuffle = !ShuffleChange.OFF.equals(shuffleState);
    final boolean autoDj = ShuffleChange.AUTODJ.equals(shuffleState);

    int color = ContextCompat.getColor(this, shuffle ? R.color.accent : R.color.button_dark);
    shuffleButton.setColorFilter(color);

    shuffleButton.setImageResource(autoDj ? R.drawable.ic_headset_black_24dp : R.drawable.ic_shuffle_black_24dp);
  }

  private void updateRepeatButtonState(RepeatChange change) {
    updateRepeat(change.getMode());
  }

  @Override
  public void updateRepeat(@Mode String mode) {
    if (repeatButton == null) {
      return;
    }

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

  private void updateVolumeData(VolumeChange change) {
    updateVolume(change.getVolume(), change.isMute());
  }

  @Override
  public void updateVolume(int volume, boolean mute) {
    if (volumeBar == null) {
      return;
    }

    if (!volumeChangeListener.isUserChangingVolume()) {
      volumeBar.setProgress(volume);
    }

    if (muteButton == null) {
      return;
    }

    int color = ContextCompat.getColor(this, R.color.button_dark);
    muteButton.setColorFilter(color);
    muteButton.setImageResource(mute ? R.drawable.ic_volume_off_black_24dp : R.drawable.ic_volume_up_black_24dp);
  }

  private void handlePlayStateChange(final PlayStateChange change) {
    updatePlayState(change.getState());
  }

  @Override
  public void updatePlayState(@State String state) {
    if (playPauseButton == null) {
      return;
    }
    int accentColor = ContextCompat.getColor(this, R.color.accent);
    @DrawableRes int resId;
    String tag;

    if (PlayerState.PLAYING.equals(state)) {
      resId = R.drawable.ic_pause_circle_filled_black_24dp;
      tag = "Playing";
        /* Start the animation if the track is playing*/
      presenter.requestNowPlayingPosition();
      trackProgressAnimation();
    } else if (PlayerState.PAUSED.equals(state)) {
      resId = R.drawable.ic_play_circle_filled_black_24dp;
      tag = PAUSED;
        /* Stop the animation if the track is paused*/
      stopTrackProgressAnimation();
    } else if (PlayerState.STOPPED.equals(state)) {
      resId = R.drawable.ic_play_circle_filled_black_24dp;
      tag = STOPPED;
        /* Stop the animation if the track is paused*/
      stopTrackProgressAnimation();
      activateStoppedState();
    } else {
      resId = R.drawable.ic_play_circle_filled_black_24dp;
      tag = STOPPED;
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

  private void handleTrackInfoChange(final TrackInfoChangeEvent change) {
    updateTrackInfo(change.getTrackInfo());
  }

  @Override
  public void updateTrackInfo(TrackInfo info) {
    if (artistLabel == null) {
      return;
    }
    artistLabel.setText(info.getArtist());
    titleLabel.setText(info.getTitle());
    albumLabel.setText(TextUtils.isEmpty(info.getYear())
        ? info.getAlbum()
        : String.format("%s [%s]", info.getAlbum(), info.getYear()));

    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(getShareIntent());
    }
  }

  private void handleConnectionStatusChange(final ConnectionStatusChangeEvent change) {
    updateConnection(change.getStatus());
  }

  @Override
  public void updateConnection(int status) {
    if (status == Connection.OFF) {
      stopTrackProgressAnimation();
      activateStoppedState();
    }
  }

  /**
   * Responsible for updating the displays and seekbar responsible for the display of the track
   * duration and the
   * current progress of playback
   */

  private void handlePositionUpdate(UpdatePosition position) {
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

  private void handleScrobbleChange(ScrobbleChange event) {
    updateScrobbleStatus(event.isActive());
  }

  @Override
  public void updateScrobbleStatus(boolean active) {
    if (menu == null) {
      return;
    }
    final MenuItem scrobbleMenuItem = menu.findItem(R.id.menu_lastfm_scrobble);
    if (scrobbleMenuItem == null) {
      return;
    }

    scrobbleMenuItem.setChecked(active);
  }

  private void handleLfmLoveChange(LfmRatingChanged event) {
    updateLfmStatus(event.getStatus());
  }

  @Override
  public void updateLfmStatus(LfmStatus status) {
    if (menu == null) {
      return;
    }
    final MenuItem favoriteMenuItem = menu.findItem(R.id.menu_lastfm_love);
    if (favoriteMenuItem == null) {
      return;
    }

    switch (status) {
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

  @Override
  protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }
}
