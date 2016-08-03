package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.PlayerState.State;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.presenters.MiniControlPresenter;
import com.kelsos.mbrc.ui.activities.nav.MainActivity;
import com.kelsos.mbrc.views.MiniControlView;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;

public class MiniControlFragment extends Fragment implements MiniControlView {

  @BindView(R.id.mc_track_cover) ImageView trackCover;
  @BindView(R.id.mc_track_artist) TextView trackArtist;
  @BindView(R.id.mc_track_title) TextView trackTitle;
  @BindView(R.id.mc_play_pause) ImageButton playPause;

  @Inject RxBus bus;
  @Inject MiniControlPresenter presenter;

  private void post(String action) {
    bus.post(MessageEvent.action(UserAction.create(action)));
  }

  @OnClick(R.id.mini_control)
  void onControlClick() {
    getContext().startActivity(new Intent(getContext(), MainActivity.class));
  }

  @OnClick(R.id.mc_next_track)
  void onNextClick() {
    String playerNext = Protocol.PlayerNext;
    post(playerNext);
  }

  @OnClick(R.id.mc_play_pause)
  void onPlayClick() {
    post(Protocol.PlayerPlayPause);
  }

  @OnClick(R.id.mc_prev_track)
  void onPreviousClick() {
    post(Protocol.PlayerPrevious);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Scope scope = Toothpick.openScopes(getActivity().getApplication(), getActivity(), this);
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_mini_control, container, false);
    ButterKnife.bind(this, view);
    Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
    Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_medium.ttf");
    trackTitle.setTypeface(robotoMedium);
    trackArtist.setTypeface(robotoRegular);
    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    bus.register(this, CoverChangedEvent.class, this::onCoverChange, true);
    bus.register(this, TrackInfoChangeEvent.class, this::onTrackInfoChange, true);
    bus.register(this, PlayStateChange.class, this::onPlayStateChange, true);
    presenter.attach(this);
    presenter.load();
  }

  @Override
  public void updateCover(@Nullable Bitmap cover) {
    if (trackCover == null) {
      return;
    }
    if (cover != null) {
      trackCover.setImageBitmap(cover);
    } else {
      trackCover.setImageResource(R.drawable.ic_image_no_cover);
    }
  }

  @Override
  public void updateTrackInfo(TrackInfo trackInfo) {
    trackArtist.setText(trackInfo.artist);
    trackTitle.setText(trackInfo.title);
  }

  @Override
  public void updateState(@State String state) {
    switch (state) {
      case PlayerState.PLAYING:
        playPause.setImageResource(R.drawable.ic_action_pause);
        break;
      default:
        playPause.setImageResource(R.drawable.ic_action_play);
        break;
    }
  }

  private void onCoverChange(CoverChangedEvent event) {
    updateCover(event.getCover());
  }

  private void onTrackInfoChange(TrackInfoChangeEvent event) {
    updateTrackInfo(event.getTrackInfo());
  }

  private void onPlayStateChange(PlayStateChange event) {
    updateState(event.getState());
  }

  @Override
  public void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }
}
