package com.kelsos.mbrc.ui.fragments.profile;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumProfileCursorAdapter;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.ui.base.BaseListFragment;

import static android.widget.AbsListView.OnScrollListener;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * interface.
 */
public class AlbumTracksFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ALBUM_ID = "albumId";
    public static final int URL_LOADER = 0x928a;

    private long albumId;
    private AlbumProfileCursorAdapter mAdapter;
    private Context mContext;

    private int mOldHeaderHeight = -1;
    private LinearLayout mMarginView;
    private ListView mListView;
    private View mListViewBackgroundView;
    private View mHeader;

    public static AlbumTracksFragment newInstance(long albumId) {
        AlbumTracksFragment fragment = new AlbumTracksFragment();
        Bundle args = new Bundle();
        args.putLong(ALBUM_ID, albumId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlbumTracksFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumId = getArguments().getLong(ALBUM_ID);
        }
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_album_tracks, container, false);
        if (view != null) {
            mHeader = view.findViewById(R.id.album_header);
            mMarginView = (LinearLayout) view.findViewById(R.id.album_profile);
            mListView = (ListView) view.findViewById(android.R.id.list);
            mListViewBackgroundView = view.findViewById(R.id.listview_background);
            mListView.setOnScrollListener(mOnScrollListener);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(LinearLayout.LayoutParams.MATCH_PARENT, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(LinearLayout.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
            mHeader.measure(widthMeasureSpec,heightMeasureSpec);
            setHeaderHeight(mHeader.getMeasuredHeight());
        }
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

    }
    @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        baseUri = Uri.withAppendedPath(Track.CONTENT_ALBUM_URI, Uri.encode(String.valueOf(albumId)));
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Track._ID, Track.TITLE, Track.TRACK_NO}, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new AlbumProfileCursorAdapter(mContext,cursor,0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // empty
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            final View top = view.getChildAt(0);
            if (top == null) {
                scrollToPosition(0);
            } else if (top != mMarginView) {
                scrollToPosition(mHeader.getHeight());
            } else {
                scrollToPosition(-top.getTop());
            }
        }
    };

    private void scrollToPosition(int position) {

        int cHeaderHeight = mHeader.getHeight();
        if (cHeaderHeight != mOldHeaderHeight) {
            setHeaderHeight(cHeaderHeight);
        }

        int headerHeight = cHeaderHeight - ((ActionBarActivity)getActivity())
                .getSupportActionBar()
                .getHeight();
        float ratio = (float) Math.min(Math.max(position, 0), headerHeight) / headerHeight;
        int newAlpha = (int) (ratio * 255);

    }

    private void setHeaderHeight(int headerHeight) {
        ViewGroup.LayoutParams params = mMarginView.getLayoutParams();
        params.height = headerHeight;
        mMarginView.setLayoutParams(params);

        if (mListViewBackgroundView != null) {
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mListViewBackgroundView.getLayoutParams();
            params2.topMargin = headerHeight;
            mListViewBackgroundView.setLayoutParams(params2);
        }

        mOldHeaderHeight = headerHeight;

    }

}
