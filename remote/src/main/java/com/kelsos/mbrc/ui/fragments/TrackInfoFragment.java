package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

public class TrackInfoFragment extends BaseFragment {

    @InjectView (R.id.track_title) private TextView trackTitle;
    @InjectView (R.id.track_artist) private TextView trackArtist;
    @InjectView (R.id.track_album) private TextView trackAlbum;
    @InjectView (R.id.track_year) private TextView trackYear;
    @InjectView (R.id.mbrc_info_overflow) private ImageButton overflowButton;
    @Inject private Bus bus;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_main_track_info, container, false);
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
                        bus.post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYER_SCROBBLE, Const.TOGGLE)));
                        break;
                    case R.id.popup_auto_dj:
                        bus.post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYER_AUTO_DJ, Const.TOGGLE)));
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

    @Subscribe public void setTrackInfo(TrackInfoChange trackInfo) {
        trackTitle.setText(trackInfo.getTitle());
        trackArtist.setText(trackInfo.getArtist());
        trackAlbum.setText(trackInfo.getAlbum());
        trackYear.setText(trackInfo.getYear());
    }
}
