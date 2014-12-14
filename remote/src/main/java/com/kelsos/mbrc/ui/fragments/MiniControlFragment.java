package com.kelsos.mbrc.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.model.PlayerState;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.actions.ButtonPressedEvent;
import com.kelsos.mbrc.events.actions.ButtonPressedEvent.Button;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.util.Logger;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MiniControlFragment extends RoboFragment {

    @InjectView (R.id.mc_track_cover)
    private ImageView trackCover;

    @InjectView (R.id.mc_track_artist)
    private TextView trackArtist;

    @InjectView (R.id.mc_track_title)
    private TextView trackTitle;

    @InjectView (R.id.mc_next_track)
    private ImageButton playNext;

    @InjectView (R.id.mc_play_pause)
    private ImageButton playPause;

    @InjectView (R.id.mc_prev_track)
    private ImageButton playPrevious;

    @Inject
    private PlayerState playerState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_mini_control, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        playNext.setOnClickListener(view -> Events.ButtonPressedNotification
                .onNext(new ButtonPressedEvent(Button.NEXT)));

        playPause.setOnClickListener(view -> Events.ButtonPressedNotification
                .onNext(new ButtonPressedEvent(Button.PLAYPAUSE)));

        playPrevious.setOnClickListener(view -> Events.ButtonPressedNotification
                .onNext(new ButtonPressedEvent(Button.PREVIOUS)));

        Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
        trackTitle.setTypeface(robotoLight);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidObservable.bindFragment(this, Events.CoverAvailableNotification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateAlbumCover, Logger::LogThrowable);

        AndroidObservable.bindFragment(this, Events.TrackInfoChangeNotification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleTrackInfoChange, Logger::LogThrowable);

        AndroidObservable.bindFragment(this, playerState.observePlaystate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handlePlayStateChange, Logger::LogThrowable);
    }

    private void updateAlbumCover(final CoverAvailable coverAvailable) {
        trackCover.setImageBitmap(coverAvailable.getCover());
    }

    public void handleTrackInfoChange(TrackInfoChange event) {
        trackArtist.setText(event.getArtist());
        trackTitle.setText(event.getTitle());
    }

    public void handlePlayStateChange(PlayState playState) {
        switch (playState) {
            case PLAYING:
                playPause.setImageResource(R.drawable.ic_action_pause);
                break;
            case PAUSED:
                playPause.setImageResource(R.drawable.ic_action_play);
                break;
            case STOPPED:
                playPause.setImageResource(R.drawable.ic_action_play);
                break;
            case UNDEFINED:
                break;
            default:
                playPause.setImageResource(R.drawable.ic_media_stop);
                break;
        }
    }

}
