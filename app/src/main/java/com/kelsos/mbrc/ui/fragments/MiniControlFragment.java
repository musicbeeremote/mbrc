package com.kelsos.mbrc.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.ui.activities.nav.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roboguice.RoboGuice;

public class MiniControlFragment extends Fragment {

  @Inject
  private RxBus bus;
  @BindView(R.id.mc_track_cover)
  ImageView trackCover;
  @BindView(R.id.mc_track_artist)
  TextView trackArtist;
  @BindView(R.id.mc_track_title)
  TextView trackTitle;
  @BindView(R.id.mc_play_pause)
  ImageButton playPause;

  @OnClick(R.id.mini_control)
  void onControlClick() {
    getContext().startActivity(new Intent(getContext(), MainActivity.class));
  }

  @OnClick(R.id.mc_next_track)
  void onNextClick() {
    bus.post(
        new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerNext, true)));
  }

  @OnClick(R.id.mc_play_pause)
  void onPlayClick() {
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.PlayerPlayPause, true)));
  }

  @OnClick(R.id.mc_prev_track)
  void onPreviousClick() {
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.PlayerPrevious, true)));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_mini_control, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    bus.register(this, CoverChangedEvent.class, this::handleCoverChange);
    bus.register(this, TrackInfoChangeEvent.class, this::handleTrackInfoChange);
    bus.register(this, PlayStateChange.class, this::handlePlayStateChange);
    Typeface robotoRegular =
        Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
    Typeface robotoMedium =
        Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_medium.ttf");
    trackTitle.setTypeface(robotoMedium);
    trackArtist.setTypeface(robotoRegular);
  }

  @Override
  public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  private void handleCoverChange(CoverChangedEvent event) {
    if (trackCover == null) {
      return;
    }
    if (event.isAvailable()) {
      trackCover.setImageBitmap(event.getCover());
    } else {
      trackCover.setImageResource(R.drawable.ic_image_no_cover);
    }
  }

  private void handleTrackInfoChange(TrackInfoChangeEvent event) {
    trackArtist.setText(event.getTrackInfo().artist);
    trackTitle.setText(event.getTrackInfo().title);
  }

  private void handlePlayStateChange(PlayStateChange event) {
    switch (event.getState()) {
      case PlayerState.PLAYING:
        playPause.setImageResource(R.drawable.ic_action_pause);
        break;
      default:
        playPause.setImageResource(R.drawable.ic_action_play);
        break;
    }
  }
}
