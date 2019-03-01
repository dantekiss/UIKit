package com.angcyo.uiview.less.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/01/23
 */
open class RSpinnerAdapter<T> : ArrayAdapter<T> {
    var thisContext: Context
    private var thisDataList: List<T>
    var layoutInflater: LayoutInflater
    var thisResource: Int
    var thisDropDownResource: Int

    constructor(context: Context, datas: List<T>) : this(context, -1, datas)

    constructor(context: Context, resource: Int, datas: List<T>) : super(context, resource, datas) {
        this.thisContext = context
        this.thisDataList = datas
        this.thisResource = resource
        this.thisDropDownResource = resource
        this.layoutInflater = LayoutInflater.from(context)
    }

    override fun setDropDownViewResource(resource: Int) {
        super.setDropDownViewResource(resource)
        this.thisDropDownResource = resource
    }

    open fun createView(convertView: View?, parent: ViewGroup, layoutId: Int): View {
        var itemView = if (convertView == null) {
            val inflate = layoutInflater.inflate(thisResource, parent, false)
            inflate.tag = RBaseViewHolder(inflate)
            inflate
        } else {
            convertView
        }
        return itemView
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getViewTypeCount(): Int {
        return super.getViewTypeCount()
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //super.getView(position, convertView, parent)
        val itemView = createView(convertView, parent, thisResource)

        onBindItemView(
            itemView.tag as RBaseViewHolder,
            position,
            if (thisDataList.size > position) thisDataList[position] else null
        )

        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getDropDownView(position, convertView, parent)

        val itemView = createView(convertView, parent, thisDropDownResource)

        onBindDropDownItemView(
            itemView.tag as RBaseViewHolder,
            position,
            if (thisDataList.size > position) thisDataList[position] else null
        )

        return itemView
    }

    /**
     * 重写此方法, 绑定布局
     * */
    open fun onBindItemView(itemViewHolder: RBaseViewHolder, position: Int, itemBean: T? = null) {
        onBindDropDownItemView(itemViewHolder, position, itemBean)
    }

    /**
     * 下拉弹窗item,布局绑定
     * */
    open fun onBindDropDownItemView(itemViewHolder: RBaseViewHolder, position: Int, itemBean: T? = null) {

    }

    /**
     * 重置数据源
     * */
    fun resetData(datas: List<T>) {
        thisDataList = datas
        clear()
        addAll(datas)
    }
}