package com.kelsos.mbrc.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.RepeatMode;
import com.kelsos.mbrc.annotations.ShuffleState;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.presenters.interfaces.IMainViewPresenter;
import com.kelsos.mbrc.ui.dialogs.RatingDialogFragment;
import com.kelsos.mbrc.ui.views.MainView;
import com.kelsos.mbrc.utilities.FontUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import roboguice.fragment.RoboFragment;

@Singleton public class MainFragment extends RoboFragment implements MainView {
  // Inject elements of the view
  @Bind(R.id.main_artist_label) TextView artistLabel;
  @Bind(R.id.main_title_label) TextView titleLabel;
  @Bind(R.id.main_label_album) TextView albumLabel;
  @Bind(R.id.main_track_progress_current) TextView trackProgressCurrent;
  @Bind(R.id.main_track_duration_total) TextView trackDuration;
  @Bind(R.id.main_button_play_pause) ImageButton playPauseButton;
  @Bind(R.id.main_volume_seeker) SeekBar volumeBar;
  @Bind(R.id.main_track_progress_seeker) SeekBar progressBar;
  @Bind(R.id.main_mute_button) ImageButton muteButton;
  @Bind(R.id.main_shuffle_button) ImageButton shuffleButton;
  @Bind(R.id.main_repeat_button) ImageButton repeatButton;
  @Bind(R.id.main_album_cover_image_view) ImageView albumCover;

  @Inject private IMainViewPresenter presenter;

  private ShareActionProvider mShareActionProvider;

  private Menu menu;
  private SeekBar.OnSeekBarChangeListener volumeBarChangeListener =
      new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          if (fromUser) {
            presenter.onVolumeChange(progress);
          }
        }

        public void onStopTrackingTouch(SeekBar seekBar) { }

        public void onStartTrackingTouch(SeekBar seekBar) { }
      };

  private SeekBar.OnSeekBarChangeListener progressBarChangeListener =
      new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          presenter.onPositionChange(progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) { }

        public void onStopTrackingTouch(SeekBar seekBar) { }
      };

  private OnExpandToolbarListener listener;

  public MainFragment() {
  }

  @OnClick(R.id.main_button_play_pause) public void playButtonPressed(View v) {
    presenter.onPlayPausePressed();
  }

  @OnClick(R.id.main_button_previous) public void onPreviousButtonPressed(View v) {
    presenter.onPreviousPressed();
  }

  @OnClick(R.id.main_button_next) public void onNextButtonPressed() {
    presenter.onNextPressed();
  }

  @OnLongClick(R.id.main_button_play_pause) public boolean onPlayerStopPressed() {
    presenter.onStopPressed();
    return true;
  }

  @OnClick(R.id.main_mute_button) public void onMuteButtonPressed(View v) {
    presenter.onMutePressed();
  }

  @OnClick(R.id.main_shuffle_button) public void onShuffleButtonClicked(View v) {
    presenter.onShufflePressed();
  }

  @OnClick(R.id.main_repeat_button) public void onRepeatButtonPressed(View v) {
    presenter.onRepeatPressed();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_main, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);

    artistLabel.setSelected(true);
    titleLabel.setSelected(true);
    albumLabel.setSelected(true);

    Typeface robotoRegular = FontUtils.getRobotoRegular(getActivity());
    Typeface robotoMedium = FontUtils.getRobotoMedium(getActivity());

    artistLabel.setTypeface(robotoRegular);
    titleLabel.setTypeface(robotoMedium);
    albumLabel.setTypeface(robotoMedium);
    trackProgressCurrent.setTypeface(robotoMedium);
    trackDuration.setTypeface(robotoMedium);

    progressBar.setOnSeekBarChangeListener(progressBarChangeListener);
    volumeBar.setOnSeekBarChangeListener(volumeBarChangeListener);
    return view;
  }

  @Override public void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override public void onResume() {
    super.onResume();
    listener.expandToolbar();
    presenter.onResume();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_lastfm_scrobble:
        presenter.onScrobbleToggle();
        return true;
      case R.id.menu_rating_dialog:
        final RatingDialogFragment ratingDialog = new RatingDialogFragment();
        ratingDialog.show(getActivity().getSupportFragmentManager(), "RatingDialog");
        return true;
      case R.id.menu_lastfm_love:
        presenter.onLfmLoveToggle();
        return true;
      default:
        return false;
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu, menu);
    this.menu = menu;
    MenuItem shareItem = menu.findItem(R.id.actionbar_share);
    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
    mShareActionProvider.setShareIntent(getShareIntent());
  }

  private Intent getShareIntent() {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    final String payload =
        String.format("Now Playing: %s - %s", artistLabel.getText(), titleLabel.getText());
    shareIntent.putExtra(Intent.EXTRA_TEXT, payload);
    return shareIntent;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    try {
      listener = (OnExpandToolbarListener) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString() + "must Implement OnExpandToolbarListener");
    }
  }

  @Override public void updateCover(@Nullable Bitmap bitmap) {
    if (bitmap != null) {
      albumCover.setImageBitmap(bitmap);
    } else {
      albumCover.setImageResource(R.drawable.ic_image_no_cover);
    }
  }

  @Override public void updateShuffle(@ShuffleState String state) {
    int color = getResources().getColor(
        !ShuffleState.OFF.equals(state) ? R.color.accent : R.color.button_dark);
    shuffleButton.setColorFilter(color);

    shuffleButton.setImageResource(
        ShuffleState.AUTODJ.equals(state) ? R.drawable.ic_headset_grey600_24dp
            : R.drawable.ic_shuffle_grey600_24dp);
  }

  @Override public void updateRepeat(@RepeatMode String mode) {
    int color = getResources().getColor(mode.equals(RepeatMode.ALL) ? R.color.accent : R.color.button_dark);
    repeatButton.setColorFilter(color);
  }

  @Override public void updateScrobbling(boolean enabled) {
    final MenuItem scrobbleMenuItem = menu.findItem(R.id.menu_lastfm_scrobble);
    if (scrobbleMenuItem == null) {
      return;
    }
    scrobbleMenuItem.setChecked(enabled);
  }

  @Override public void updateLoved(LfmStatus status) {
    final MenuItem favoriteMenuItem = menu.findItem(R.id.menu_lastfm_love);
    if (favoriteMenuItem == null) {
      return;
    }
    switch (status) {
      case LOVED:
        favoriteMenuItem.setIcon(R.drawable.ic_action_favorite);
        break;
      default:
        favoriteMenuItem.setIcon(R.drawable.ic_action_favorite_outline);
        break;
    }
  }

  @Override public void updateVolume(int volume) {
    volumeBar.setProgress(volume);
  }

  @Override public void updatePlaystate(PlayState playstate) {
    switch (playstate) {
      case PLAYING:
        playPauseButton.setImageResource(R.drawable.ic_pause_circle_fill);
        break;
      case PAUSED:
        playPauseButton.setImageResource(R.drawable.ic_play_circle_fill);
        break;
      case STOPPED:
        playPauseButton.setImageResource(R.drawable.ic_play_circle_fill);
        break;
      default:
        playPauseButton.setImageResource(R.drawable.ic_play_circle_fill);
        break;
    }
  }

  @Override public void updateMute(boolean enabled) {
    muteButton.setImageResource(
        enabled ? R.drawable.ic_volume_off_grey600_24dp : R.drawable.ic_volume_up_grey600_24dp);
  }

  @Override public void updatePosition(TrackPosition position) {
    trackProgressCurrent.setText(String.format("%02d:%02d",
        position.getCurrentMinutes(),
        position.getCurrentSeconds()));

    trackDuration.setText(String.format("%02d:%02d",
        position.getTotalMinutes(),
        position.getTotalSeconds()));

    progressBar.setProgress(position.getCurrent());
    progressBar.setMax(position.getTotal());
  }

  @Override public int getCurrentProgress() {
    return progressBar.getProgress();
  }

  @Override public void setStoppedState() {
    progressBar.setProgress(0);
    trackProgressCurrent.setText("00:00");
  }

  @Override public void updateTrackInfo(TrackInfo info) {
    if (info == null) {
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

  public interface OnExpandToolbarListener {
    void expandToolbar();
  }
}
