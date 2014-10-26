package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.rest.RemoteApi;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrackInfoFragment extends RoboFragment {

    @InjectView (R.id.track_title)
    private TextView trackTitle;
    @InjectView (R.id.track_artist)
    private TextView trackArtist;
    @InjectView (R.id.track_album)
    private TextView trackAlbum;
    @InjectView (R.id.track_year)
    private TextView trackYear;
    @InjectView (R.id.mbrc_info_overflow)
    private ImageButton overflowButton;
    @Inject
    private RemoteApi api;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_main_track_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PopupMenu menu = new PopupMenu(getActivity(), overflowButton);
        menu.inflate(R.menu.info_popup);
        final PopupMenu.OnMenuItemClickListener listener = menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.popup_scrobble:
                    toggleScrobble();
                    break;
                case R.id.popup_auto_dj:
                    toggleAutoDj();
                    break;
                default:
                    return false;

            }
            return false;
        };
        menu.setOnMenuItemClickListener(listener);
        final View.OnClickListener clickListener = v -> menu.show();
        overflowButton.setOnClickListener(clickListener);

        api.getTrackInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    trackTitle.setText(resp.getTitle());
                    trackArtist.setText(resp.getArtist());
                    trackAlbum.setText(resp.getAlbum());
                    trackYear.setText(resp.getYear());
                });
    }

    private void toggleScrobble(){
//call toggle function
    }

    private void toggleAutoDj(){
//call toggle function
    }
}
