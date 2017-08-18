/*
 * Copyright (C) 2017 Łukasz Milewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.khrone.libraries.fancymenu

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.ActionMenuView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

class FancyMenuAdapter(context: Context, items: List<MenuItem>,
                       private val layoutId: Int) : ArrayAdapter<MenuItem>(context, layoutId, items)
{

    var listener: ActionMenuView.OnMenuItemClickListener? = null

    private val menuItemEnabledColor = ContextCompat.getColor(context, R.color.menu_icon_enabled)

    private val menuItemDisabledColor = ContextCompat.getColor(context, R.color.menu_icon_disabled)

    var minWidth: Int = 0

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)
        val menuItem = getItem(position)

        view.isEnabled = menuItem.isEnabled
        view.setOnClickListener { listener?.onMenuItemClick(menuItem) }

        val titleView = view.findViewById<TextView>(R.id.menu_item_title)
        titleView.text = menuItem.title

        val imageView = view.findViewById<ImageView>(R.id.menu_item_image)
        imageView.visibility = View.GONE

        if (menuItem.icon != null)
        {
            val icon = DrawableCompat.wrap(menuItem.icon).mutate()
            DrawableCompat.setTint(icon, if (menuItem.isEnabled) menuItemEnabledColor else menuItemDisabledColor)

            imageView.visibility = View.VISIBLE
            imageView.setImageDrawable(icon)
        }

        return view
    }

    fun calculateWidth(): Int
    {
        val measureParent = FrameLayout(context)

        var maxWidth = 0
        var itemView: View? = null
        var itemType = 0

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        for (index in 0 until count)
        {
            if (itemType != getItemViewType(index))
            {
                itemType = getItemViewType(index)
                itemView = null
            }

            itemView = getView(index, itemView, measureParent)
            itemView.measure(widthMeasureSpec, heightMeasureSpec)

            maxWidth = Math.max(maxWidth, itemView.measuredWidth)
        }

        return maxWidth
    }

}
