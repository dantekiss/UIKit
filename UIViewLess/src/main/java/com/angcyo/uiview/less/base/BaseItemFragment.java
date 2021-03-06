package com.angcyo.uiview.less.base;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.recycler.RExItemDecoration;
import com.angcyo.uiview.less.recycler.RRecyclerView;
import com.angcyo.uiview.less.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.less.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.less.recycler.item.RItemAdapter;
import com.angcyo.uiview.less.recycler.item.SingleItem;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/12/08
 */
@Deprecated
public abstract class BaseItemFragment extends BaseRecyclerFragment<SingleItem> {
    /**
     * 每次调用{@link #refreshLayout()}, 如果item类型有改变, 左移此条目, 必须是高四位开始
     */
    protected int itemTypeStart = 0x1_0000;
    ArrayList<SingleItem> singleItems;

    public BaseItemFragment() {
        singleItems = new ArrayList<>(getItemInitialCapacity());
    }

    /**
     * 初始化容量
     */
    protected int getItemInitialCapacity() {
        return getViewHolderInitialCapacity();
    }

    @Override
    public void initRefreshRecyclerView(@Nullable SmartRefreshLayout smartRefreshLayout, @Nullable RRecyclerView recyclerView) {
        super.initRefreshRecyclerView(smartRefreshLayout, recyclerView);
        enableRefreshAffect();
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            recyclerView.addItemDecoration(
                    new RExItemDecoration(
                            new RExItemDecoration.SingleItemCallback() {

                                @Override
                                public void getItemOffsets2(@NonNull Rect outRect, int position, int edge) {
                                    SingleItem t = singleItems.get(position);
                                    t.setItemOffsets2(outRect, edge);
                                }

                                @Override
                                public void draw(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                                 @NonNull View itemView, @NonNull Rect offsetRect,
                                                 int itemCount, int position) {
                                    SingleItem t = singleItems.get(position);
                                    t.draw(canvas, paint, itemView, offsetRect, itemCount, position);
                                }
                            }));

            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                ((LinearLayoutManager) recyclerView.getLayoutManager()).setRecycleChildrenOnDetach(true);
            }
            recyclerView.setItemViewCacheSize(1);
        }
    }

    @Override
    protected RBaseAdapter<SingleItem> onCreateAdapter(List<SingleItem> datas) {
        singleItems.clear();
        onCreateItems(singleItems);
        RItemAdapter<SingleItem> adapter = new RItemAdapter<SingleItem>(mAttachContext, singleItems) {

            @Override
            public int getDataItemType(int posInData) {
                if (areItemTypeTheSame()) {
                    return posInData;
                }
                return itemTypeStart + posInData;
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (BaseItemFragment.this.getItemLayoutId() != -1) {
                    return BaseItemFragment.this.getItemLayoutId();
                }
                return super.getItemLayoutId(viewType);
            }
        };
        return adapter;
    }

    protected int getItemLayoutId() {
        return -1;
    }

    /**
     * 强转后返回
     */
    @Nullable
    public RItemAdapter<SingleItem> getItemAdapter() {
        if (baseAdapter instanceof RItemAdapter) {
            return (RItemAdapter<SingleItem>) baseAdapter;
        }
        return null;
    }

    /**
     * 创建Items
     */
    protected abstract void onCreateItems(@NonNull ArrayList<SingleItem> singleItems);

    /**
     * 如果item 类型会变化, 则返回false, 否则item type不会变化, 返回 true
     */
    protected boolean areItemTypeTheSame() {
        return true;
    }

    //<editor-fold desc="Adapter 数据操作的方法">

    /**
     * 更新布局, 重新创建了items, 如果item的数量有变化, 建议使用这个方法
     * 请在post方法中调用
     *
     * @see #updateItemsLayout()
     */
    public void refreshLayout() {

        recyclerView.saveLastPosition();
        initRecyclerView(recyclerView);

        RecyclerView.RecycledViewPool recycledViewPool = recyclerView.getRecycledViewPool();
//        recycledViewPool.clear();
//
//        for (int i = 0; i < recyclerView.getChildCount(); i++) {
//            try {
//                View child = recyclerView.getChildAt(i);
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(params.getViewAdapterPosition());
//                if (holder != null) {
//                    Reflect.setMember(RecyclerView.ViewHolder.class, holder, "mFlags", 8);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        singleItems.clear();
//        onCreateItems(singleItems);
        if (!areItemTypeTheSame()) {
            itemTypeStart = itemTypeStart << 1;

            if (itemTypeStart > 0x1000_0000) {
                itemTypeStart = 0x1_0000;
            }
        }
        for (int i = 0; recyclerView != null && i < singleItems.size(); i++) {
            //只需要缓存一个就行
            recycledViewPool.setMaxRecycledViews(i, 1);
        }
//        if (baseAdapter != null) {
//            baseAdapter.notifyDataSetChanged();
//        }

        recyclerView.resetToLastPosition();
    }

    /**
     * 如果只是要更新item的数据, 建议使用此方法
     *
     * @see #refreshLayout()
     */
    public void updateItemsLayout() {
        if (baseAdapter != null) {
            for (int i = 0; i < singleItems.size(); i++) {
                SingleItem item = singleItems.get(i);
                RBaseViewHolder viewHolder = null;
                if (baseAdapter instanceof RModelAdapter) {
                    viewHolder = ((RModelAdapter<SingleItem>) baseAdapter).getViewHolderFromPosition(i);
                }
                if (viewHolder != null) {
                    item.onBindView(viewHolder, i, item);
                }
            }
        }
    }

    /**
     * 刷新某一个item
     */
    public void notifyItemChanged(SingleItem item) {
        if (baseAdapter != null) {
            int indexOf = singleItems.indexOf(item);
            if (indexOf > -1) {
                notifyItemChanged(indexOf);
            }
        }
    }

    public void notifyItemChanged(int position) {
        if (baseAdapter != null) {
            baseAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 通过Tag, 刷新指定Item
     */
    public void notifyItemChangedByTag(String tag) {
        if (baseAdapter != null) {
            for (int i = 0; i < singleItems.size(); i++) {
                SingleItem item = singleItems.get(i);
                if (TextUtils.equals(item.getTag(), tag)) {
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void notifyItemChanged(int position, int count) {
        if (baseAdapter != null) {
            baseAdapter.notifyItemRangeChanged(position, count);
        }
    }

    //</editor-fold>


    @Override
    protected boolean isFirstNeedLoadData() {
        return false;
    }
}
