package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.clickIt
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class ItemDialogConfig : BaseDialogConfig() {

    init {
        positiveButtonText = null
        negativeButtonText = null
    }

    /**
     * 需要填充的item数据集合
     * */
    var items = mutableListOf<Any>()

    override var dialogLayoutId: Int = R.layout.dialog_items_layout

    var dialogItemLayoutId = R.layout.dialog_item_text_layout

    /**
     * 返回 true, 不会自动调用 dismiss
     * */
    var onItemClick: (dialog: Dialog, index: Int, item: Any) -> Boolean = { _, _, _ ->
        false
    }

    /**
     * 创建item布局
     * */
    var createDialogItemView: (dialog: Dialog, parent: ViewGroup, inflater: LayoutInflater, index: Int, item: Any) -> View =
        { dialog, parent, inflater, index, item ->
            val view = inflater.inflate(dialogItemLayoutId, parent, false)

            if (item is CharSequence) {
                view.findViewById<TextView>(R.id.item_text_view).text = item
            }

            view.clickIt {
                if (onItemClick.invoke(dialog, index, item)) {

                } else {
                    dialog.dismiss()
                }
            }

            view
        }

    /**
     * 是否显示底部的取消布局
     * */
    var showBottomCancelLayout = true

    var initBottomCancelLayout: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit =
        { dialog, dialogViewHolder ->

            if (showBottomCancelLayout) {
                dialogViewHolder.view(R.id.cancel_layout)?.apply {
                    findViewById<TextView>(R.id.item_text_view).text = "取消"
                    clickIt {
                        dialog.cancel()
                    }
                }
            } else {
                dialogViewHolder.gone(R.id.cancel_layout_line)
                dialogViewHolder.gone(R.id.cancel_layout)
            }

        }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)

        dialogViewHolder.group(R.id.item_wrap_layout).apply {
            val layoutInflater = LayoutInflater.from(context)

            for (i in 0 until items.size) {
                addView(createDialogItemView.invoke(dialog, this, layoutInflater, i, items[i]))
            }
        }

        initBottomCancelLayout.invoke(dialog, dialogViewHolder)
    }
}