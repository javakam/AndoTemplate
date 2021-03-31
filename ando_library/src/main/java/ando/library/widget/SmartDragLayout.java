package ando.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;

/**
 * Description: 智能的拖拽布局，优先滚动整体，整体滚到头，则滚动内部能滚动的View
 * Create by dance, at 2018/12/23
 */
public class SmartDragLayout extends LinearLayout implements NestedScrollingParent {
    public enum LayoutStatus {
        /**
         *
         */
        OPEN, OPENING, CLOSE, CLOSING
    }

    //    private View childView;
    private OverScroller scroller;
    private VelocityTracker tracker;
    private boolean enableDrag = true;    //是否启用手势拖拽
    private boolean dismissOnTouchOutside = true;
    private boolean isUserClose = false;
    private boolean isThreeDrag = false;  //是否开启三段拖拽
    private boolean isScrollUp;

    private final int animationDuration = 350;
    private int maxY;
    private int minY;
    private int lastHeight;
    private float touchX, touchY;
    private LayoutStatus status = LayoutStatus.CLOSE;

    public SmartDragLayout(Context context) {
        this(context, null);
    }

    public SmartDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        if (enableDrag) {
            scroller = new OverScroller(context);
        }
    }

    @Override
    public void onViewAdded(View c) {
        super.onViewAdded(c);
//        childView = c;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //maxY = childView.getMeasuredHeight();
        maxY = getMeasuredHeight();
        minY = 0;
//        int l = getMeasuredWidth() / 2 - childView.getMeasuredWidth() / 2;
//        //childView.layout(0, 0, l + childView.getMeasuredWidth(), getMeasuredHeight() + maxY);
//        childView.layout(l, getMeasuredHeight(), l + childView.getMeasuredWidth(), getMeasuredHeight() + maxY);
        if (status == LayoutStatus.OPEN) {
            scrollTo(getScrollX(), getScrollY() - (lastHeight - maxY));
        }
        lastHeight = maxY;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        isUserClose = true;
        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enableDrag && scroller.computeScrollOffset()) {
            touchX = 0;
            touchY = 0;
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (enableDrag) {
                    if (tracker != null) {
                        tracker.clear();
                    }
                    tracker = VelocityTracker.obtain();
                }
                touchX = event.getX();
                touchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (enableDrag && tracker != null) {
                    tracker.addMovement(event);
                    tracker.computeCurrentVelocity(1000);
                    int dy = (int) (event.getY() - touchY);
                    scrollTo(getScrollX(), getScrollY() - dy);
                    touchY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // click in child rect
                Rect rect = new Rect();
                //childView.getGlobalVisibleRect(rect);
                getGlobalVisibleRect(rect);
                if (!isInRect(event.getRawX(), event.getRawY(), rect) && dismissOnTouchOutside) {
                    float distance = (float) Math.sqrt(Math.pow(event.getX() - touchX, 2) + Math.pow(event.getY() - touchY, 2));
                    if (distance < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        performClick();
                    }
                }
                if (enableDrag && tracker != null) {
                    float yVelocity = tracker.getYVelocity();
                    if (yVelocity > 1500 && !isThreeDrag) {
                        close();
                    } else {
                        finishScroll();
                    }
                    //tracker.recycle();
                    tracker = null;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void finishScroll() {
        if (enableDrag) {
            int threshold = isScrollUp ? (maxY - minY) / 3 : (maxY - minY) * 2 / 3;
            int dy = (getScrollY() > threshold ? maxY : minY) - getScrollY();
            if (isThreeDrag) {
                int per = maxY / 3;
                if (getScrollY() > per * 2.5f) {
                    dy = maxY - getScrollY();
                } else if (getScrollY() <= per * 2.5f && getScrollY() > per * 1.5f) {
                    dy = per * 2 - getScrollY();
                } else if (getScrollY() > per) {
                    dy = per - getScrollY();
                } else {
                    dy = minY - getScrollY();
                }
            }
            scroller.startScroll(getScrollX(), getScrollY(), 0, dy, animationDuration);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y > maxY) {
            y = maxY;
        }
        if (y < minY) {
            y = minY;
        }
        float fraction = (y - minY) * 1f / (maxY - minY);
        isScrollUp = y > getScrollY();
        if (listener != null) {
            if (isUserClose && fraction == 0.0F && status != LayoutStatus.CLOSE) {
                status = LayoutStatus.CLOSE;
                listener.onClose();
            } else if (fraction == 1.0F && status != LayoutStatus.OPEN) {
                status = LayoutStatus.OPEN;
                listener.onOpen();
            }
            //Log.i("123", "fraction = " + fraction + "  y = " + y + "  getScrollY = " + getScrollY());
            listener.onDrag(y, fraction, isScrollUp);
        }
        super.scrollTo(x, y);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isScrollUp = false;
        isUserClose = false;
        setTranslationY(0);
    }

    public void open() {
        post(() -> {
            int dy = maxY - getScrollY();
            smoothScroll(enableDrag && isThreeDrag ? dy / 3 : dy, true);
            status = LayoutStatus.OPENING;
        });
    }

    public void close() {
        isUserClose = true;
        post(() -> {
            scroller.abortAnimation();
            smoothScroll(minY - getScrollY(), false);
            status = LayoutStatus.CLOSING;
        });
    }

    public void smoothScroll(final int dy, final boolean isOpen) {
        post(() -> {
            scroller.startScroll(getScrollX(), getScrollY(), 0, dy, (int) (isOpen ? animationDuration : animationDuration * 0.8f));
            ViewCompat.postInvalidateOnAnimation(SmartDragLayout.this);
        });
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && enableDrag;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        //必须要取消，否则会导致滑动初次延迟
        scroller.abortAnimation();
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        finishScroll();
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        scrollTo(getScrollX(), getScrollY() + dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        if (dy > 0) {
            //scroll up
            int newY = getScrollY() + dy;
            if (newY < maxY) {
                consumed[1] = dy; // dy不一定能消费完
            }
            scrollTo(getScrollX(), newY);
        }
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        boolean isDragging = getScrollY() > minY && getScrollY() < maxY;
        if (isDragging && velocityY < -1500 && !isThreeDrag) {
            close();
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    private boolean isInRect(float x, float y, Rect rect) {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    public void isThreeDrag(boolean isThreeDrag) {
        this.isThreeDrag = isThreeDrag;
    }

    public void enableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    public void dismissOnTouchOutside(boolean dismissOnTouchOutside) {
        this.dismissOnTouchOutside = dismissOnTouchOutside;
    }

    private OnCloseListener listener;

    public void setOnCloseListener(OnCloseListener listener) {
        this.listener = listener;
    }

    public interface OnCloseListener {
        void onClose();

        void onDrag(int y, float percent, boolean isScrollUp);

        void onOpen();
    }
}