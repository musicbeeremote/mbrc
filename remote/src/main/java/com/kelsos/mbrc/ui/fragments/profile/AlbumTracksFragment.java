package com.kelsos.mbrc.ui.fragments.profile;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.*;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumProfileCursorAdapter;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.data.dbdata.Cover;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.kelsos.mbrc.ui.fragments.browse.BrowseMenuItems;
import com.squareup.picasso.Picasso;
import roboguice.inject.InjectView;

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
    private int mLastDampedScroll;
    private FrameLayout mMarginView;
    private ListView mListView;
    private View mListViewBackgroundView;
    private View mHeader;
    private View mContentView;
    private int mLastScrollPosition;
    private boolean isInit;

    @InjectView(R.id.header_tracks) private TextView mTracks;
    @InjectView(R.id.header_album) private TextView mAlbum;
    @InjectView(R.id.header_artist) private TextView mArtist;
    @InjectView(R.id.header_artwork) private ImageView mArtwork;
    private static final int GROUP_ID = 0x83721e;

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
        mContentView = inflater.inflate(R.layout.fragment_album_tracks, container, false);
        if (mContentView != null) {
            View header = inflater.inflate(R.layout.album_profile_header, null, false);
            mHeader = header.findViewById(R.id.album_header);
            mListView = (ListView) mContentView.findViewById(android.R.id.list);
            mMarginView = new FrameLayout(mListView.getContext());
            mMarginView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            mMarginView.addView(header);
            mListView.addHeaderView(mMarginView);
            mListViewBackgroundView = mContentView.findViewById(R.id.listview_background);
            mListView.setOnScrollListener(mOnScrollListener);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(LinearLayout.LayoutParams.MATCH_PARENT, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(LinearLayout.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
            mMarginView.setOnLongClickListener(null);
            mHeader.measure(widthMeasureSpec,heightMeasureSpec);
            setHeaderHeight(mHeader.getMeasuredHeight());
            mContentView.getRootView()
                    .getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int headerHeight = mHeader.getHeight();
                    if (!isInit && headerHeight != 0) {
                        setHeaderHeight(headerHeight);
                        isInit = true;
                    }
                }
            });

        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Uri uri = Uri.withAppendedPath(Album.getContentUri(), String.valueOf(albumId));
        final Cursor cursor = getActivity().getContentResolver().query(uri,
                new String[]{
                        Album._ID,
                        Album.ALBUM_NAME,
                        Album.ARTIST_ID,
                        Artist.ARTIST_NAME,
                        Album.COVER_HASH
                }, null, null, null
        );

        cursor.moveToFirst();
        final Album album = new Album(cursor);
        final Uri artUri = Uri.withAppendedPath(Cover.CONTENT_IMAGE_URI, album.getCoverHash());

        mAlbum.setText(album.getAlbumName());
        mArtist.setText(album.getArtist());

        Picasso.with(mContext)
                .load(artUri)
                .fit()
                .placeholder(R.color.mbrc_transparent_dark)
                .error(R.drawable.ic_image_no_cover)
                .into(mArtwork);
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
        mTracks.setText(getString(R.string.track_count, mAdapter.getCount()));
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

//        int headerHeight = cHeaderHeight - ((ActionBarActivity)getActivity())
//                .getSupportActionBar()
//                .getHeight();
        //float ratio = (float) Math.min(Math.max(position, 0), headerHeight) / headerHeight;
        //int newAlpha = (int) (ratio * 255);

        //parallaxScrolling(position);

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

    private void parallaxScrolling(int position) {
        float damping = 0.25f;
        int dampedScroll = (int) (position * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mHeader.offsetTopAndBottom(offset);

        if (mListViewBackgroundView != null) {
            offset = mLastScrollPosition - position;
            mListViewBackgroundView.offsetTopAndBottom(offset);
        }

        if (isInit) {
            mLastScrollPosition = position;
            mLastDampedScroll = dampedScroll;
        }
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = mi != null ? mi.position : 0;
            //final Artist artist = new Artist((Cursor) mAdapter.getItem(position));

            switch (item.getItemId()) {
                default:
                    break;

            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }
}
