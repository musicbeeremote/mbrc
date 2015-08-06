package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.utilities.DrawableUtils;
import com.kelsos.mbrc.utilities.ViewUtils;
import java.util.ArrayList;
import roboguice.util.Ln;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.TrackHolder>
    implements DraggableItemAdapter<NowPlayingAdapter.TrackHolder>,
    SwipeableItemAdapter<NowPlayingAdapter.TrackHolder> {
  private ArrayList<MusicTrack> data;
  private int playingTrackIndex;
  private LayoutInflater inflater;
  private OnUserActionListener listener;

  public NowPlayingAdapter() {
    data = new ArrayList<>();
    setHasStableIds(true);
  }

  public void setData(ArrayList<MusicTrack> data) {
    this.data = data;
    notifyDataSetChanged();
  }

  public int getPlayingTrackIndex() {
    return this.playingTrackIndex;
  }

  public void setPlayingTrackIndex(int index) {
    this.playingTrackIndex = index;
    notifyDataSetChanged();
  }

  @Override public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (inflater == null) {
      final Context context = parent.getContext();
      inflater = LayoutInflater.from(context);
    }
    final View view = inflater.inflate(R.layout.ui_list_track_item, parent, false);
    return new TrackHolder(view);
  }

  @Override public void onBindViewHolder(TrackHolder holder, int position) {
    MusicTrack track = data.get(position);
    holder.title.setText(track.getTitle());
    holder.artist.setText(track.getArtist());

    if (position == playingTrackIndex) {
      holder.trackPlaying.setImageResource(R.drawable.ic_media_now_playing);
    } else {
      holder.trackPlaying.setImageResource(android.R.color.transparent);
    }

    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClicked(position);
      }
    });

    // set background resource (target view ID: container)
    final int dragState = holder.getDragStateFlags();
    final int swipeState = holder.getSwipeStateFlags();

    if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0) ||
        ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_UPDATED) != 0)) {
      int bgResId;

      if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
        bgResId = R.drawable.bg_item_dragging_active_state;

        // need to clear drawable state here to get correct appearance of the dragging item.
        DrawableUtils.clearState(holder.container.getForeground());
      } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
        bgResId = R.drawable.bg_item_dragging_state;
      } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_ACTIVE) != 0) {
        bgResId = R.drawable.bg_item_swiping_active_state;
      } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_SWIPING) != 0) {
        bgResId = R.drawable.bg_item_swiping_state;
      } else {
        bgResId = R.drawable.bg_item_normal_state;
      }

      holder.container.setBackgroundResource(bgResId);
    }

    // set swiping properties
    holder.setSwipeItemSlideAmount(RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_RIGHT);
  }

  @Override public int getItemCount() {
    return data.size();
  }

  @Override public long getItemId(int position) {
    return data.get(position).getPosition();
  }

  @Override public boolean onCheckCanStartDrag(TrackHolder holder, int position, int x, int y) {
    final View containerView = holder.itemView;
    final View dragHandleView = holder.dragHandle;

    final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
    final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

    final boolean test = ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    Ln.v("Hit was %s", test);
    return test;
  }

  @Override
  public ItemDraggableRange onGetItemDraggableRange(TrackHolder trackHolder, int position) {
    return null;
  }

  @Override public void onMoveItem(int from, int to) {
    final MusicTrack track = data.get(from);
    data.remove(track);
    data.add(to, track);

    notifyItemMoved(from, to);

    if (listener != null) {
      listener.onTrackMoved(from, to);
    }
  }

  @Override public int onGetSwipeReactionType(TrackHolder holder, int position, int x, int y) {
    if (onCheckCanStartDrag(holder, position, x, y)) {
      Ln.d("Drag");
      return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
    }

    return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
  }

  @Override public void onSetSwipeBackground(TrackHolder holder, int position, int type) {
    int bgRes = 0;
    switch (type) {
      case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
        bgRes = R.drawable.bg_swipe_item_neutral;
        break;
      case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
        bgRes = R.drawable.bg_swipe_item_left;
        break;
      case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
        bgRes = R.drawable.bg_swipe_item_right;
        break;
    }

    holder.itemView.setBackgroundResource(bgRes);
  }

  @Override public int onSwipeItem(TrackHolder trackHolder, int position, int result) {
    Ln.d("onSwipeItem(result = %d)", result);
    switch (result) {
      // swipe right
      case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
        Ln.v("Right Swipe");
        return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
      case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
      case RecyclerViewSwipeManager.RESULT_CANCELED:
      default:
        Ln.v("default");
        return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
    }
  }

  @Override
  public void onPerformAfterSwipeReaction(TrackHolder trackHolder, int position, int result,
      int reaction) {
    Ln.d("onPerformAfterSwipeReaction(result = %d, reaction = %d)", result, reaction);
    if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
      data.remove(position);
      notifyItemRemoved(position);

      if (listener != null) {
        listener.onTrackRemoved(position);
      }
    }
  }

  public void setOnUserActionListener(OnUserActionListener listener) {
    this.listener = listener;
  }

  /**
   * This method is used to restore the tracks to their original positions in case the move
   * failed to complete.
   *
   * @param from The original position of the {@link MusicTrack}.
   * @param to The position the element was original moved to.
   */
  public void restorePositions(int from, int to) {
    final MusicTrack track = data.get(to);
    data.remove(track);
    data.add(from, track);
  }

  public void insert(MusicTrack track, int index) {
    data.add(index, track);
  }

  public void setPlayingTrack(MusicTrack track) {
    setPlayingTrackIndex(data.indexOf(track));
    notifyDataSetChanged();
  }

  public interface OnUserActionListener {
    void onTrackRemoved(int position);

    void onTrackMoved(int from, int to);

    void onItemClicked(int position);
  }

  static class TrackHolder extends AbstractDraggableSwipeableItemViewHolder {
    @Bind(R.id.drag_handle) View dragHandle;
    @Bind(R.id.track_title) TextView title;
    @Bind(R.id.track_artist) TextView artist;
    @Bind(R.id.track_indicator_view) ImageView trackPlaying;
    @Bind(R.id.container) FrameLayout container;

    public TrackHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Override public View getSwipeableContainerView() {
      return itemView;
    }
  }
}
