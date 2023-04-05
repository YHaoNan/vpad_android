package top.yudoge.vpad.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import top.yudoge.vpad.R;

public class UnscrollableRecyclerView extends RecyclerView {
    private final WindowManager windowManager;
    // 状态栏高度
    private final int statusHeight;
    // drag是否开启
    private boolean isItemDraggable = false;
    // 在一个项目上长按多久触发drag
    private long dragResponseMS = 600;
    // drag时在一个项目范围上多久才算hover
    private long hoverResponseMS = 400;
    // drag时，和另一个项目重合的比例为多少才算在另一个项目上hover
    private float insetPercentage = 0.30f;

    private Handler handler = new Handler();

    private OnDragStateChangedListener onDragStateChangedListener;

    public UnscrollableRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public UnscrollableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnscrollableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        statusHeight = getStatusBarHeight(context);
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    public void enableDraggable() {
        this.isItemDraggable = true;
    }

    public void disableDraggable() {
        this.isItemDraggable = false;
    }

    public void setDragResponseMS(long dragResponseMS) {
        this.dragResponseMS = dragResponseMS;
    }

    public void setHoverResponseMS(long hoverResponseMS) {
        this.hoverResponseMS = hoverResponseMS;
    }

    public void setInsetPercentage(float insetPercentage) {
        this.insetPercentage = insetPercentage;
    }

    public void setOnDragStateChangedListener(OnDragStateChangedListener onDragStateChangedListener) {
        this.onDragStateChangedListener = onDragStateChangedListener;
    }


    private float lastX = 0;
    private float lastY = 0;
    private int lastAction = -1;
    private long lastDownTime = 0;

    // 当前拖拽的item
    private View currentDragItem = null;
    private Rect currentDragItemRect = null;
    private int currentDragItemPosition = -1;
    // 当前拖拽的item的一个复制，它会跟随手指移动
    private ImageView currentDragItemCopy = null;

    private WindowManager.LayoutParams currentDragItemCopyLayoutParam = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isItemDraggable) return super.dispatchTouchEvent(ev);

        lastX = ev.getRawX();
        lastY = ev.getRawY();
        lastAction = ev.getAction();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 如果没点击在任何一个item的位置上，结束事件分派
                if (!findCurrentDragItem(ev)) return super.dispatchTouchEvent(ev);
                lastDownTime = System.currentTimeMillis();
                // 在堆上暂存当前的dragItem，以在dragResponseMS后正确传递给handle方法
                View thisItem = currentDragItem;
                handler.postDelayed(() -> handleLongClickTimeReached(thisItem), dragResponseMS);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMoveAction(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleReleaseDragItem();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void handleReleaseDragItem() {
        if (onDragStateChangedListener != null && hasCurrentDragItem()) {
            int underlyingItemPos;
            if ((underlyingItemPos = findUnderlyingItemIndexByPosition((int) lastX, (int) lastY)) != -1) {
                onDragStateChangedListener.onDragRelease(currentDragItem, currentDragItemPosition, getChildAt(underlyingItemPos), underlyingItemPos);
            } else {
                onDragStateChangedListener.onDragRelease(currentDragItem, currentDragItemPosition, null, -1);
            }
        }

        // 清除当前dragItem相关的状态
        clearCurrentDragItemAboutStates();
    }

    // 当你刚刚进入一个underlyingItem，还没到hoverResponseTime时，我们会猜测你可能是想要在上面hover，
    // 并且设置一个定时器。第二次在该underlyingItem上触发移动事件时，我们已经可以通过该变量知道我们已经
    // 预测过该underlyingItem，并且在上面设置了定时器，无需再次进行设置。
    private int predicUnderlyingItemPos = -1;
    private boolean moveOutEmitted = false;
    private void handleMoveAction(MotionEvent ev) {
        if (!hasCurrentDragItem()) return;
        // 更新位置
        if (currentDragItemCopyLayoutParam != null && currentDragItemCopy != null) {
            currentDragItemCopyLayoutParam.x = (int) lastX - (currentDragItemCopy.getWidth() / 2);
            currentDragItemCopyLayoutParam.y = (int) lastY - (currentDragItemCopy.getHeight() / 2) - statusHeight;
            windowManager.updateViewLayout(currentDragItemCopy, currentDragItemCopyLayoutParam);
        }

        int underlyingItemPos;
        if ((underlyingItemPos = findUnderlyingItemIndexByPosition((int) lastX, (int) lastY)) != -1) {
            if (predicUnderlyingItemPos == underlyingItemPos) return;
            predicUnderlyingItemPos = underlyingItemPos;
            moveOutEmitted = false;
            View underlyingItem = getChildAt(underlyingItemPos);
            long lastEnterTime = System.currentTimeMillis();
            if (onDragStateChangedListener != null)
                onDragStateChangedListener.onMoveInAnItem(currentDragItem, currentDragItemPosition, underlyingItem, underlyingItemPos);
            handler.postDelayed(() -> {
                // 经过hoverResponse后还在这个underlyingItem上，并且距离开始的时间 > hoverResponseMs
                if (findUnderlyingItemIndexByPosition((int)lastX, (int)lastY) == underlyingItemPos
                        && System.currentTimeMillis() - lastEnterTime >= hoverResponseMS
                        && lastActionIsNotUpOrCancel()
                        && currentDragItemPosition != underlyingItemPos) {
                    if (onDragStateChangedListener != null)
                        onDragStateChangedListener.onHoverOnAnItem(currentDragItem, currentDragItemPosition, underlyingItem, underlyingItemPos);
                }
            }, hoverResponseMS);
        } else {
            if (predicUnderlyingItemPos >=0 && predicUnderlyingItemPos < getChildCount() && onDragStateChangedListener != null && !moveOutEmitted) {
                onDragStateChangedListener.onMoveOutAnItem(currentDragItem, currentDragItemPosition, getChildAt(predicUnderlyingItemPos), predicUnderlyingItemPos);
                moveOutEmitted = true;
            }
        }
    }

    /**
     * 清除所有和currentDragItem相关的状态
     */
    private void clearCurrentDragItemAboutStates() {
        if (currentDragItemCopy != null) windowManager.removeView(currentDragItemCopy);
        currentDragItemRect = null;
        currentDragItem = null;
        currentDragItemPosition = -1;
        currentDragItemCopy = null;
        currentDragItemCopyLayoutParam = null;
    }

    /**
     * 根据x,y位置获取内部Item的下标，这个位置相对于整个屏幕
     * @param x x坐标
     * @param y y坐标
     * @return 若找到，返回Item下标，否则返回-1
     */
    private int findUnderlyingItemIndexByPosition(int x, int y) {
        for (int i=0; i<getChildCount(); i++) {
            View child = getChildAt(i);
            Rect rect = new Rect();
            child.getGlobalVisibleRect(rect);
            if (rect.contains(x, y)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * 查找currentDragItem，如果点击位置不在任意一个Item上，清空currentDragItem相关的状态，返回false
     * 否则，设置currentDragItem相关的状态，返回true
     * @param ev
     * @return
     */
    private boolean findCurrentDragItem(MotionEvent ev){
        int childPos;
        if ((childPos = findUnderlyingItemIndexByPosition((int) ev.getRawX(), (int) ev.getRawY())) != -1) {
            currentDragItem = getChildAt(childPos);
            currentDragItemRect = new Rect();
            currentDragItem.getGlobalVisibleRect(currentDragItemRect);
            currentDragItemPosition = childPos;
        }
        return childPos != -1;
    }

    /**
     * 距离一次TOUCH_DOWN发生已经过去了dragResponseMS，现在需要判断这次是否符合长按的要求
     */
    private void handleLongClickTimeReached(View targetLongClickChild) {
        // 只有当按下一秒后没有移动出当前项目，并且也没有松手，才认为是长按事件发生了
        // 并且有可能出现一个前长按延时到达时，正在按下另一个子元素的情况，使用targetLongClickChild加以区别
        if (hasCurrentDragItem()
                && System.currentTimeMillis() - lastDownTime >= dragResponseMS
                && currentDragItem == targetLongClickChild
                && currentDragItemRect.contains((int) lastX, (int) lastY)
                && lastActionIsNotUpOrCancel()) {
            ImageView imageView = createImageViewAndFillPixelsByOtherView(targetLongClickChild);
            int xy[] = new int[2];
            targetLongClickChild.getLocationInWindow(xy);
            addCurrentDragItemCopyToWindow(imageView, xy[0], xy[1] - statusHeight, targetLongClickChild.getWidth(), targetLongClickChild.getHeight());
            // 在当前if中，由于currentDragItem == targetLongClickChild，那么currentDragItemPosition必然是当前drag的position，因为UI只在主线程更新，所以我们可以直接使用它
            onDragStateChangedListener.onDragStart(currentDragItem, currentDragItemPosition);
        }
    }

    private boolean lastActionIsNotUpOrCancel() {
        return lastAction != MotionEvent.ACTION_UP && lastAction != MotionEvent.ACTION_CANCEL;
    }

    /**
     *
     * @param originalView
     */
    private ImageView createImageViewAndFillPixelsByOtherView(View originalView) {
        ImageView imageView = new ImageView(getContext());
//        imageView.setImageResource(R.drawable.bg_akai_mpd_218);
        Bitmap bitmap = Bitmap.createBitmap(originalView.getWidth(), originalView.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        originalView.draw(canvas);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    private void addCurrentDragItemCopyToWindow(ImageView dragItem, int startX, int startY, int width, int height) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = startX;
        layoutParams.y = startY;
        layoutParams.alpha = 0.55f;
        layoutParams.width  = width;
        layoutParams.height = height;
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;

        currentDragItemCopy = dragItem;
        currentDragItemCopyLayoutParam = layoutParams;
        windowManager.addView(currentDragItemCopy, currentDragItemCopyLayoutParam);
    }


    private boolean hasCurrentDragItem() {
        return currentDragItemRect != null && currentDragItem != null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return false;
    }

    public interface OnDragStateChangedListener {
        void onDragStart(@NonNull View dragItem, int dragItemPosition);
        void onMoveInAnItem(@NonNull View dragItem, int dragItemPosition, @NonNull View moveInItem, int moveInItemPosition);
        void onMoveOutAnItem(@NonNull View dragItem, int dragItemPosition, @NonNull View moveOutItem, int moveOutItemPosition);
        void onHoverOnAnItem(@NonNull View dragItem, int dragItemPosition, @NonNull View hoverOnItem, int hoverOnItemPosition);
        void onDragRelease(@NonNull View dragItem, int dragItemPosition, @Nullable View releaseOnItem, int releaseOnItemPosition);
    }
}
