package com.dragdrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class DraggableLinearLayout extends LinearLayout implements View.OnTouchListener {
    private BitmapDrawable mHoverCell;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;

    private int mDownY = -1;
    private int mDownX = -1;

    private int mLastEventY = -1;

    private final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private boolean mCellIsMobile = false;

    private int mTotalOffset = 0;

    private final int INVALID_ID = -1;
    private int mMobileItemPosition = INVALID_ID;

    private LockableScrollView mScrollView;

    public DraggableLinearLayout(Context context) {
        this(context, null);
    }

    public DraggableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(final View child) {
        super.addView(child);
        child.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mHoverCell = getAndAddHoverView(child);
                mCellIsMobile = true;
                mMobileItemPosition = indexOfChild(child);
                child.setVisibility(INVISIBLE);
                mScrollView = (LockableScrollView) getRootView().findViewById(R.id.scroll);
                mScrollView.setScrollingEnabled(false);

                return true;
            }
        });
        child.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)event.getX();
                mDownY = (int)event.getY();
                mActivePointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER_ID) {
                    break;
                }

                int pointerIndex = event.findPointerIndex(mActivePointerId);

                mLastEventY = (int) event.getY(pointerIndex);
                int deltaY = mLastEventY - mDownY;

                if (mCellIsMobile) {
                    mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left,
                            mHoverCellOriginalBounds.top + deltaY + mTotalOffset);
                    mHoverCell.setBounds(mHoverCellCurrentBounds);
                    invalidate();
                    handleCellSwitch();
                    return false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                touchEventsEnded();
                break;
            case MotionEvent.ACTION_UP:
                touchEventsEnded();
                break;
        }
        return false;
    }

    private void touchEventsEnded () {
        mHoverCell = null;
        mCellIsMobile = false;
        mTotalOffset = 0;
        View mobileView = getChildAt(mMobileItemPosition);
        if(mobileView != null) {
            mobileView.setVisibility(VISIBLE);
        }
        mMobileItemPosition = INVALID_ID;
        mScrollView.setScrollingEnabled(true);
        invalidate();
    }

    public View getViewForID (int itemID) {
        return getChildAt(itemID);
    }

    private void handleCellSwitch() {
        final int deltaY = mLastEventY - mDownY;
        int deltaYTotal = mHoverCellOriginalBounds.top + mTotalOffset + deltaY;

        int belowItemPosition = mMobileItemPosition + 1;
        int aboveItemPosition = mMobileItemPosition - 1;

        View belowView = getViewForID(belowItemPosition);
        View aboveView = getViewForID(aboveItemPosition);

        boolean isBelow = (belowView != null) && (deltaYTotal > belowView.getTop());
        boolean isAbove = (aboveView != null) && (deltaYTotal < aboveView.getTop());

        if (isBelow || isAbove) {
            int switchItemPosition = isBelow ? belowItemPosition : aboveItemPosition;

            View switchView = getChildAt(switchItemPosition);
            removeView(switchView);
            addView(switchView, mMobileItemPosition);

            if(isBelow) {
                mTotalOffset = mTotalOffset + switchView.getHeight();
            } else {
                mTotalOffset = mTotalOffset - switchView.getHeight();
            }

            mMobileItemPosition = switchItemPosition;
        }
    }

    private BitmapDrawable getAndAddHoverView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();

        Bitmap b = getBitmapWithBorder(v);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

        drawable.setBounds(mHoverCellCurrentBounds);

        return drawable;
    }

    private Bitmap getBitmapWithBorder(View v) {
        Bitmap bitmap = getBitmapFromView(v);
        Canvas can = new Canvas(bitmap);

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setColor(Color.GRAY);

        can.drawBitmap(bitmap, 0, 0, null);
        can.drawRect(rect, paint);

        return bitmap;
    }

    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHoverCell != null) {
            mHoverCell.draw(canvas);
        }
    }
}
