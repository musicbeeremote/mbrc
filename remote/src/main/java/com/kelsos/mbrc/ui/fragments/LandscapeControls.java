package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;


/**
 * A simple {@link com.kelsos.mbrc.ui.base.BaseFragment} subclass.
 * Used to to display the track information and controls while on
 * landscape mode.
 * Use the {@link LandscapeControls#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LandscapeControls extends BaseFragment {

    @InjectView (R.id.track_title) private TextView trackTitle;
    @InjectView (R.id.track_artist) private TextView trackArtist;
    @InjectView (R.id.track_album) private TextView trackAlbum;
    @InjectView (R.id.track_year) private TextView trackYear;
    @InjectView (R.id.mbrc_info_overflow) private ImageButton overflowButton;
    @InjectView(R.id.main_button_play_pause) private ImageButton playButton;
    @InjectView(R.id.main_button_previous) private ImageButton previousButton;
    @InjectView(R.id.main_button_next) private ImageButton nextButton;
    @InjectView(R.id.main_shuffle_button) private ImageButton shuffleButton;
    @InjectView(R.id.main_repeat_button) private ImageButton repeatButton;


    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment LandscapeControls.
     */
    public static LandscapeControls newInstance() {
        return new LandscapeControls();
    }

    public LandscapeControls() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landscape_controls, container, false);
    }

    @Subscribe
    public void setTrackInfo(TrackInfoChange trackInfo) {
        trackTitle.setText(trackInfo.getTitle());
        trackArtist.setText(trackInfo.getArtist());
        trackAlbum.setText(trackInfo.getAlbum());
        trackYear.setText(trackInfo.getYear());
    }

}
