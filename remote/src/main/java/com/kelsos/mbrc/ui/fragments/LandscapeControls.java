package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
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
 */
public class LandscapeControls extends BaseFragment {

    @InjectView(R.id.track_title)
    private TextView trackTitle;
    @InjectView(R.id.track_artist)
    private TextView trackArtist;
    @InjectView(R.id.track_album)
    private TextView trackAlbum;
    @InjectView(R.id.track_year)
    private TextView trackYear;
    @InjectView(R.id.mbrc_info_overflow)
    private ImageButton overflowButton;
    @InjectView(R.id.main_button_play_pause)
    private ImageButton playButton;
    @InjectView(R.id.main_button_previous)
    private ImageButton previousButton;
    @InjectView(R.id.main_button_next)
    private ImageButton nextButton;
    @InjectView(R.id.main_shuffle_button)
    private ImageButton shuffleButton;
    @InjectView(R.id.main_repeat_button)
    private ImageButton repeatButton;


    private View.OnClickListener playButtonListener = new View.OnClickListener() {

        public void onClick(View v) {

        }
    };
    private View.OnClickListener previousButtonListener = new View.OnClickListener() {

        public void onClick(View v) {

        }
    };
    private View.OnClickListener nextButtonListener = new View.OnClickListener() {

        public void onClick(View v) {

        }
    };
    private View.OnLongClickListener stopListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            return true;
        }
    };
    private ImageButton.OnClickListener shuffleListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private ImageButton.OnClickListener repeatListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public LandscapeControls() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment LandscapeControls.
     */
    public static LandscapeControls newInstance() {
        return new LandscapeControls();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PopupMenu menu = new PopupMenu(getActivity(), overflowButton);
        menu.inflate(R.menu.info_popup);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.popup_scrobble:

                        break;
                    case R.id.popup_auto_dj:

                        break;
                    default:
                        return false;

                }
                return false;
            }
        });
        overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });
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

    @Override
    public void onStart() {
        super.onStart();
        playButton.setOnClickListener(playButtonListener);
        playButton.setOnLongClickListener(stopListener);
        previousButton.setOnClickListener(previousButtonListener);
        nextButton.setOnClickListener(nextButtonListener);
        shuffleButton.setOnClickListener(shuffleListener);
        repeatButton.setOnClickListener(repeatListener);
    }

    @Subscribe
    public void handleShuffleChange(ShuffleChange change) {
        if (shuffleButton == null) {
            return;
        }
        shuffleButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_shuffle : R.drawable.ic_media_shuffle_off);
    }

    @Subscribe
    public void updateRepeatButtonState(RepeatChange change) {
        if (repeatButton == null) {
            return;
        }
        repeatButton.setImageResource(change.getIsActive() ? R.drawable.ic_media_repeat : R.drawable.ic_media_repeat_off);
    }

    @Subscribe
    public void handlePlayStateChange(final PlayStateChange change) {
        if (playButton == null) {
            return;
        }
        switch (change.getState()) {
            case PLAYING:
                playButton.setImageResource(R.drawable.ic_media_pause);

                break;
            case PAUSED:
                playButton.setImageResource(R.drawable.ic_media_play);
                break;
            case STOPPED:
                playButton.setImageResource(R.drawable.ic_media_stop);
                break;
            case UNDEFINED:
                playButton.setImageResource(R.drawable.ic_media_play);
                break;
            default:
                break;
        }
    }

}
