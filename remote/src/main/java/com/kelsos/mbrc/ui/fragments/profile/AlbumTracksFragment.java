package com.kelsos.mbrc.ui.fragments.profile;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumProfileCursorAdapter;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Cover;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.squareup.picasso.Picasso;
import java.io.File;
import roboguice.fragment.provided.RoboListFragment;
import roboguice.inject.InjectView;

import static android.widget.AbsListView.OnScrollListener;

public class AlbumTracksFragment extends RoboListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final int URL_LOADER = 0x928a;
  private static final String ALBUM_ID = "albumId";
  private long albumId;
  @Inject private AlbumProfileCursorAdapter mAdapter;
  private Context mContext;
  private int mOldHeaderHeight = -1;
  private int mLastDampedScroll;
  private FrameLayout mMarginView;
  private View mListViewBackgroundView;
  private View mHeader;
  private View mContentView;
  private int mLastScrollPosition;
  private boolean isInit;
  @InjectView(R.id.header_tracks) private TextView mTracks;
  @InjectView(R.id.header_album) private TextView mAlbum;
  @InjectView(R.id.header_artist) private TextView mArtist;
  @InjectView(R.id.header_artwork) private ImageView mArtwork;


  private OnScrollListener mOnScrollListener = new OnScrollListener() {
    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
      // empty
    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
        int totalItemCount) {
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

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public AlbumTracksFragment() {
  }

  public static AlbumTracksFragment newInstance(long albumId) {
    AlbumTracksFragment fragment = new AlbumTracksFragment();
    Bundle args = new Bundle();
    args.putLong(ALBUM_ID, albumId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      albumId = getArguments().getLong(ALBUM_ID);
    }
    mContext = getActivity();
    this.setListAdapter(mAdapter);
    getLoaderManager().initLoader(URL_LOADER, null, this);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mContentView = inflater.inflate(R.layout.fragment_album_tracks, container, false);
    if (mContentView != null) {
      View header = inflater.inflate(R.layout.album_profile_header, container, false);
      mHeader = header.findViewById(R.id.album_header);
      ListView listView = (ListView) mContentView.findViewById(android.R.id.list);
      mMarginView = new FrameLayout(listView.getContext());
      mMarginView.setLayoutParams(
          new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
      mMarginView.addView(header);
      listView.addHeaderView(mMarginView);
      mListViewBackgroundView = mContentView.findViewById(R.id.listview_background);
      listView.setOnScrollListener(mOnScrollListener);
      int widthMeasureSpec =
          View.MeasureSpec.makeMeasureSpec(LinearLayout.LayoutParams.MATCH_PARENT,
              View.MeasureSpec.EXACTLY);
      int heightMeasureSpec =
          View.MeasureSpec.makeMeasureSpec(LinearLayout.LayoutParams.WRAP_CONTENT,
              View.MeasureSpec.EXACTLY);
      mMarginView.setOnLongClickListener(null);
      mHeader.measure(widthMeasureSpec, heightMeasureSpec);
      setHeaderHeight(mHeader.getMeasuredHeight());
      mContentView.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
        int headerHeight = mHeader.getHeight();
        if (!isInit && headerHeight != 0) {
          setHeaderHeight(headerHeight);
          isInit = true;
        }
      });
    }
    return mContentView;
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    //final String sortOrder = String.format("%s ASC", TrackHelper.POSITION);
    //final String selection = String.format("%s = ?", TrackHelper.ALBUMID);
    //final String[] selectionArgs = {
    //    String.valueOf(albumId)
    //};
    //return new CursorLoader(getActivity(), TrackHelper.CONTENT_URI,
    //    TrackHelper.getProjection(),
    //    selection, selectionArgs, sortOrder);
    return null;
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    // FIXME: 8/6/15 temporarily disabled
    final Album album = new Album();
    final Cover cover = album.getCover();
    final Artist artist = album.getArtist();
    mAdapter.swapCursor(cursor);
    mTracks.setText(getString(R.string.track_count, mAdapter.getCount()));
    mAlbum.setText(album.getName());

    if (artist != null) {
      mArtist.setText(artist.getName());
    }

    if (cover != null) {
      final File image = new File(RemoteUtils.getStorage(), cover.getHash());

      Picasso.with(getActivity())
          .load(image)
          .placeholder(R.drawable.ic_image_no_cover)
          .fit()
          .centerCrop()
          .into(mArtwork);
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mAdapter.swapCursor(null);
  }

  private void scrollToPosition(int position) {

    int cHeaderHeight = mHeader.getHeight();
    if (cHeaderHeight != mOldHeaderHeight) {
      setHeaderHeight(cHeaderHeight);
    }
  }

  private void setHeaderHeight(int headerHeight) {
    ViewGroup.LayoutParams params = mMarginView.getLayoutParams();
    params.height = headerHeight;
    mMarginView.setLayoutParams(params);

    if (mListViewBackgroundView != null) {
      LinearLayout.LayoutParams params2 =
          (LinearLayout.LayoutParams) mListViewBackgroundView.getLayoutParams();
      params2.topMargin = headerHeight;
      mListViewBackgroundView.setLayoutParams(params2);
    }

    mOldHeaderHeight = headerHeight;
  }
}
