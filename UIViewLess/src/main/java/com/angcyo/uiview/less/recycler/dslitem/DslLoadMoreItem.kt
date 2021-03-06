package com.angcyo.uiview.less.recycler.dslitem

import com.angcyo.lib.L
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseUI
import com.angcyo.uiview.less.kotlin.setWidthHeight
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem

/**
 * [RecyclerView.Adapter] 加载更多实现
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslLoadMoreItem : BaseDslStateItem() {

    init {
        BaseUI.uiDslAdapterStatus.initStateLayoutMap(this, itemStateLayoutMap)
        onItemViewDetachedToWindow = {
            if (itemEnableLoadMore) {
                //加载失败时, 下次是否还需要加载更多?
                if (itemState == ADAPTER_LOAD_ERROR) {
                    itemState =
                        ADAPTER_LOAD_RETRY
                }
            }
        }
        thisAreContentsTheSame = { _, _ ->
            false
        }
    }

    companion object {
        /**正常状态, 等待加载更多*/
        const val ADAPTER_LOAD_NORMAL = 0
        /**加载更多中*/
        const val ADAPTER_LOAD_LOADING = 1
        /**无更多*/
        const val ADAPTER_LOAD_NO_MORE = 2
        /**加载失败*/
        const val ADAPTER_LOAD_ERROR = 10
        /**加载失败, 自动重试中*/
        const val ADAPTER_LOAD_RETRY = 11
    }

    /**是否激活加载更多, 默认关闭*/
    var itemEnableLoadMore = false
        set(value) {
            field = value
            itemState = ADAPTER_LOAD_NORMAL
        }

    /**加载更多回调*/
    var onLoadMore: (RBaseViewHolder) -> Unit = {
        L.i("[DslLoadMoreItem] 触发加载更多")
    }

    override fun onItemBind(
        itemHolder: RBaseViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        itemHolder.itemView.setWidthHeight(-1, -2)
        super.onItemBind(itemHolder, itemPosition, adapterItem)
    }

    override fun _onBindStateLayout(itemHolder: RBaseViewHolder, state: Int) {
        super._onBindStateLayout(itemHolder, state)

        if (itemEnableLoadMore) {
            if (itemState == ADAPTER_LOAD_NORMAL || itemState == ADAPTER_LOAD_LOADING) {
                _notifyLoadMore(itemHolder)
            } else if (itemState == ADAPTER_LOAD_ERROR) {
                itemHolder.clickItem {
                    if (itemState == ADAPTER_LOAD_ERROR || itemState == ADAPTER_LOAD_RETRY) {
                        //失败的情况下, 点击触发重新加载
                        _notifyLoadMore(itemHolder)
                        updateAdapterItem()
                    }
                }

                if (itemData != null) {
                    itemHolder.tv(R.id.base_error_tip_view)?.text = when {
                        itemData is String -> itemData as String
                        itemData is Throwable -> (itemData as Throwable).message
                        itemData != null -> itemData.toString()
                        else -> "加载失败, 点击重试"
                    }
                }
            } else {
                itemHolder.itemView.isClickable = false
            }
        } else {
            itemHolder.itemView.isClickable = false
        }
    }

    open fun _notifyLoadMore(itemHolder: RBaseViewHolder) {
        itemState = ADAPTER_LOAD_LOADING
        if (!_isLoadMore) {
            _isLoadMore = true
            itemHolder.post { onLoadMore(itemHolder) }
        }
    }

    //是否已经在加载更多
    var _isLoadMore = false

    override fun _onItemStateChange(old: Int, value: Int) {
        if (old != value && value != ADAPTER_LOAD_LOADING) {
            _isLoadMore = false
        }
        super._onItemStateChange(old, value)
    }
}