package com.example.zzh.videodemo_java.itemDecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangzhihao on 2018/6/25 10:51.
 * 第一个item增加top
 */

public class EmptyItemDecoration extends RecyclerView.ItemDecoration {
    private int paddingTop;

    public EmptyItemDecoration(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.top = paddingTop;
        }
    }
}
