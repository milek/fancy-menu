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
import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.support.v7.view.menu.MenuItemImpl
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.ListPopupWindow
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import android.widget.PopupWindow
import java.lang.reflect.Field

fun Activity.prepareFancyMenu(toolbarId: Int, menuItem: MenuItem? = null, layoutId: Int = R.layout.fancy_menu_item)
{
    if (menuItem != null)
    {
        val actionMenuItemView: View = findViewById(menuItem.itemId) ?: return
        actionMenuItemView.setOnClickListener { showSubMenu(actionMenuItemView, menuItem, layoutId) }
        actionMenuItemView.setOnLongClickListener { true }
        actionMenuItemView.setOnTouchListener(null)

        return
    }

    val toolbar = findViewById<Toolbar>(toolbarId)
    for (index in 0 until toolbar.childCount)
    {
        val child: View = toolbar.getChildAt(index)
        if (child is ActionMenuView)
        {
            for (actionIndex in 0 until child.childCount)
            {
                val actionChild = child.getChildAt(actionIndex)
                if (actionChild::class.java.simpleName.contains("OverflowMenuButton"))
                {
                    actionChild.setOnClickListener { showOverflowMenu(actionChild, toolbarId, layoutId) }
                    actionChild.setOnLongClickListener { true }
                    actionChild.setOnTouchListener(null)
                }
            }
        }
    }
}

// -=-=-=- Private Activity extensions that helps with FancyMenu

private fun Activity.gatherItemsFromOverflow(toolbarId: Int): List<MenuItem>
{
    val items: MutableList<MenuItem> = mutableListOf()
    val toolbar = findViewById<Toolbar>(toolbarId)

    for (index in 0 until toolbar.menu.size())
    {
        val menuItem = toolbar.menu.getItem(index)
        if (!menuItem.isVisible)
        {
            continue
        }

        try
        {
            val IS_ACTION = 0x00000020

            val menuItemFlags: Field = MenuItemImpl::class.java.getDeclaredField("mFlags") ?: continue
            menuItemFlags.isAccessible = true

            if ((menuItemFlags.getInt(menuItem) and IS_ACTION) == IS_ACTION)
            {
                continue
            }
        }
        catch (_: Exception)
        {
        }

        items.add(menuItem)
    }

    return items
}

private fun Activity.showOverflowMenu(view: View, toolbarId: Int, layoutId: Int)
{
    val adapter = FancyMenuAdapter(this, gatherItemsFromOverflow(toolbarId), layoutId)
    adapter.minWidth = 168F.toPx()

    showFancyMenu(view, adapter)
}

private fun Activity.showFancyMenu(view: View, adapter: FancyMenuAdapter)
{
    val popupWindow = ListPopupWindow(this, null, android.R.attr.popupMenuStyle)
    popupWindow.setAdapter(adapter)
    popupWindow.isModal = true
    popupWindow.anchorView = view
    popupWindow.setDropDownGravity(Gravity.END or Gravity.TOP)
    popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
    popupWindow.width = Math.max(adapter.minWidth, adapter.calculateWidth())
    popupWindow.verticalOffset = -1 * view.height
    popupWindow.horizontalOffset = -1 * 4F.toPx()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
        popupWindow.animationStyle = 0
    }

    adapter.listener = ActionMenuView.OnMenuItemClickListener { item -> item.invoke(popupWindow) }
    popupWindow.show()
}

private fun Activity.showSubMenu(view: View, menuItem: MenuItem, layoutId: Int)
{
    val submenu: SubMenu = menuItem.subMenu ?: return
    val items: MutableList<MenuItem> = mutableListOf()

    (0 until submenu.size()).mapTo(items) { submenu.getItem(it) }

    showFancyMenu(view, FancyMenuAdapter(this, items, layoutId))
}

// -=-=-=- MenuItem extension

@SuppressLint("RestrictedApi")
private fun MenuItem.invoke(popupWindow: ListPopupWindow): Boolean
{
    (this as MenuItemImpl).invoke()
    popupWindow.dismiss()

    return true
}

// -=-=-=- Utilities

private fun Float.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()
