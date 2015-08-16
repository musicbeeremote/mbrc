package com.kelsos.mbrc.ui.fragments.profile;

import android.content.Context;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumProfileAdapter;
import roboguice.fragment.RoboFragment;

import static android.widget.AbsListView.OnScrollListener;

public class AlbumTracksFragment extends RoboFragment{

  private static final String ALBUM_ID = "albumId";
  private long albumId;
  @Inject private AlbumProfileAdapter mAdapter;
  private Context mContext;
  private int mOldHeaderHeight = -1;
  private int mLastDampedScroll;
  private FrameLayout mMarginView;
  private View mListViewBackgroundView;
  private View mHeader;
  private View mContentView;
  private int mLastScrollPosition;
  private boolean isInit;
  @Bind(R.id.header_tracks) TextView mTracks;
  @Bind(R.id.header_album) TextView mAlbum;
  @Bind(R.id.header_artist) TextView mArtist;
  @Bind(R.id.header_artwork) ImageView mArtwork;


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

    ButterKnife.bind(this, mContentView);

    return mContentView;
  }

  /*
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
   */


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
