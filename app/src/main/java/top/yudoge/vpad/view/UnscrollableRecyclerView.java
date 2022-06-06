package top.yudoge.vpad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class UnscrollableRecyclerView extends RecyclerView {
    public UnscrollableRecyclerView(@NonNull Context context) {
        super(context);
    }

    public UnscrollableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UnscrollableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return false;
    }
}
