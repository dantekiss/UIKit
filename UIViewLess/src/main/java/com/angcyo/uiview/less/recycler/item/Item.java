package com.angcyo.uiview.less.recycler.item;

import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;

/**
 * 用来实现{@link com.angcyo.uiview.less.base.BaseItemFragment}界面中, 每个Item的布局
 * Created by angcyo on 2017-03-12.
 */

public interface Item {
    String getTag();//唯一标识item, 用来notify changed

    void onBindView(@NonNull RBaseViewHolder holder, int posInData, Item itemDataBean);

    @Deprecated
    void setItemOffsets(@NonNull Rect rect);

    void setItemOffsets2(@NonNull Rect rect, int edge);

    void draw(@NonNull Canvas canvas, @NonNull TextPaint paint, @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position);

    /**
     * Item对应的布局id, -1表示默认
     */
    int getItemLayoutId();

    View createItemView(@NonNull ViewGroup parent, int viewType);
}
