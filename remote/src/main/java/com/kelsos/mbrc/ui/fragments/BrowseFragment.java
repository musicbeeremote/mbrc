package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.BrowsePagerAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.HashMap;
import java.util.Map;

public class BrowseFragment extends BaseFragment {
    public static final int LIBRARY_SYNC = 1;
    public static final int GROUP_ID = 15;
    private BrowsePagerAdapter mAdapter;

    public BrowseFragment() { }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_fragment_search, container, false);
        ViewPager mPager;
        if (view != null) {
            mPager = (ViewPager) view.findViewById(R.id.search_pager);
            mPager.setAdapter(mAdapter);
            TitlePageIndicator titleIndicator = (TitlePageIndicator) view.findViewById(R.id.search_categories);
            titleIndicator.setViewPager(mPager);
        }

        return view;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case LIBRARY_SYNC:
                Map<String, String> req = new HashMap<>();
                req.put("type", "full");
                getBus().post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LIBRARY_SYNC, req)));
                break;
            default:
                return false;
        }
        return false;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new BrowsePagerAdapter(getActivity());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(GROUP_ID, LIBRARY_SYNC, 0, "Sync Library");
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }
}
