package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import com.angcyo.uiview.less.base.BaseFragment
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.RDialog

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun BaseFragment.buildBottomDialog(): RDialog.Builder {
    return RDialog.build(activity)
        .setCanceledOnTouchOutside(false)
        .setDialogWidth(-1)
        .setDialogHeight(-2)
        .setDialogBgColor(Color.TRANSPARENT)
        .setDialogGravity(Gravity.BOTTOM)
}

private fun configDialogBuilder(builder: RDialog.Builder, dialogConfig: BaseDialogConfig): RDialog.Builder {
    builder.setCancelable(dialogConfig.dialogCancel)
        .setCanceledOnTouchOutside(dialogConfig.dialogCanceledOnTouchOutside)
        .setOnCancelListener {
            dialogConfig.onDialogCancel(it as Dialog)
            dialogConfig.onDialogCancel.invoke(it)
        }
        .setOnDismissListener {
            dialogConfig.onDialogDismiss(it as Dialog)
            dialogConfig.onDialogDismiss.invoke(it)
        }
        .setContentLayoutId(dialogConfig.dialogLayoutId)
        .setInitListener(object : RDialog.OnInitListener() {
            override fun onInitDialog(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
                dialogConfig.onDialogInit(dialog, dialogViewHolder)

                dialogConfig.dialogInit.invoke(dialog, dialogViewHolder)
            }
        })
    return builder
}

public fun BaseFragment.normalDialog(config: NormalDialogConfig.() -> Unit) {
    val dialogConfig = NormalDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        RDialog.build(activity)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

public fun BaseFragment.normalIosDialog(config: IosDialogConfig.() -> Unit) {
    val dialogConfig = IosDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        RDialog.build(activity)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

public fun BaseFragment.itemsDialog(config: ItemDialogConfig.() -> Unit) {
    val dialogConfig = ItemDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}