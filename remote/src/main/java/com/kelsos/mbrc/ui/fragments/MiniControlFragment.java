package com.kelsos.mbrc.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.presenters.MiniControlPresenter;
import com.kelsos.mbrc.ui.views.MiniControlView;
import com.kelsos.mbrc.utilities.FontUtils;
import roboguice.fragment.RoboFragment;

public class MiniControlFragment extends RoboFragment implements MiniControlView {

  @Bind(R.id.mc_track_cover) ImageView trackCover;
  @Bind(R.id.mc_track_artist) TextView trackArtist;
  @Bind(R.id.mc_track_title) TextView trackTitle;
  @Bind(R.id.mc_play_pause) ImageButton playPause;

  @Inject private MiniControlPresenter presenter;

  @NonNull public static Fragment newInstance() {
    return new MiniControlFragment();
  }

  @OnClick(R.id.mc_next_track) public void onNextClicked() {
    presenter.onNextPressed();
  }

  @OnClick(R.id.mc_play_pause) public void onPlayPauseClicked() {
    presenter.onPlayPause();
  }

  @OnClick(R.id.mc_prev_track) public void onPreviousClicked() {
    presenter.onPreviousPressed();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_mini_control, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);
    trackTitle.setTypeface(FontUtils.getRobotoMedium(getActivity()));
    trackArtist.setTypeface(FontUtils.getRobotoRegular(getActivity()));
    presenter.load();
    return view;
  }

  @Override public void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
  }

  public void updateCover(@Nullable Bitmap cover) {
    if (cover != null) {
      trackCover.setImageBitmap(cover);
    } else {
      trackCover.setImageResource(R.drawable.ic_image_no_cover);
    }
  }

  public void updateTrack(String artist, String title) {
    trackArtist.setText(artist);
    trackTitle.setText(title);
  }

  public void updatePlayerState(@PlayerState.State String playState) {
    switch (playState) {
      case PlayerState.PLAYING:
        playPause.setImageResource(R.drawable.ic_pause_black_36dp);
        break;
      default:
        playPause.setImageResource(R.drawable.ic_play_arrow_black_36dp);
        break;
    }
  }
}
