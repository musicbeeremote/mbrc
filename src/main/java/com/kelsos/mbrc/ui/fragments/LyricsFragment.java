package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

public class LyricsFragment extends BaseListFragment {
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_lyrics, container, false);
    }

    @Subscribe public void updateLyricsData(LyricsUpdated update) {
        final ArrayList<String> lyricsList = new ArrayList<String>(Arrays.asList(update.getLyrics().split("\r\n")));
        final LyricsAdapter lyricsAdapter = new LyricsAdapter(getActivity(), R.layout.ui_list_lyrics_item, lyricsList);
        setListAdapter(lyricsAdapter);
    }
}
