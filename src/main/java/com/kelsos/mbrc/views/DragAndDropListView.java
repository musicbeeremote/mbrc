package com.kelsos.mbrc.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.events.Drag;
import com.kelsos.mbrc.events.DragDropEvent;
import com.kelsos.mbrc.events.UserActionEvent;
import com.squareup.otto.Bus;

public class DragAndDropListView extends ListView {

    @Inject
    Bus bus;

    private boolean mDragMode;
    private int mStartPosition;
    private int mEndPosition;
    private int mDragPointOffset;

    private ImageView mDragView;
    private GestureDetector mGestureDetector;

    public DragAndDropListView(Context context) {
        super(context);
    }

    public DragAndDropListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        final int action = motionEvent.getAction();
        final int x = (int) motionEvent.getX();
        final int y = (int) motionEvent.getY();

        if(action == MotionEvent.ACTION_DOWN && x < this.getWidth()/4) {
            mDragMode = true;
        }

        if(!mDragMode){
            return super.onTouchEvent(motionEvent);
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartPosition = pointToPosition(x,y);
                if (mStartPosition != INVALID_POSITION) {
                    int mItemPosition = mStartPosition - getFirstVisiblePosition();
                    mDragPointOffset = y - getChildAt(mItemPosition).getTop();
                    mDragPointOffset -= ((int)motionEvent.getRawY()) - y;
                    startDrag(mItemPosition,y);
                    drag(0,y);// replace 0 with x if desired
                }
                break;
            case MotionEvent.ACTION_MOVE:
                drag(0,y);// replace 0 with x if desired
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                mDragMode = false;
                mEndPosition = pointToPosition(x,y);
                stopDrag(mStartPosition - getFirstVisiblePosition());
                if(mStartPosition != INVALID_POSITION && mEndPosition !=INVALID_POSITION)
                {
                    bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_MOVE_NOWPLAYING_TRACK, mStartPosition + "#" +mEndPosition));
                }
                break;
        }
        return true;
    }

    private void drag(int x, int y){
        if(mDragView != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
            layoutParams.x = x;
            layoutParams.y = y - mDragPointOffset;
            WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.updateViewLayout(mDragView,layoutParams);

//            if (mDragListener != null)
//                mDragListener.onDrag(x, y, null);// change null to "this" when ready to use

        }
    }

    private void startDrag(int index, int y){
        stopDrag(index);

        View item = getChildAt(index);
        if(item == null) return;
        item.setDrawingCacheEnabled(true);

        bus.post(new DragDropEvent(Drag.DRAG_START, item));

        Bitmap bmp = Bitmap.createBitmap(item.getDrawingCache());

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPointOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setImageBitmap(bmp);

        WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }


    private void stopDrag(int index) {
        if (mDragView != null) {

            bus.post(new DragDropEvent(Drag.DRAG_STOP, getChildAt(index)));

            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
    }


}
