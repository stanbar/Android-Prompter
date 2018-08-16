/*
 * Copyright (c) 2017. Stanislaw stasbar Baranski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.stasbar.prompter

import android.content.Context
import android.content.res.TypedArray
import androidx.annotation.AttrRes
import android.graphics.drawable.Drawable
import android.graphics.Color
import androidx.annotation.ColorInt


/**
 * Created by stasbar on 19.10.2017
 */
object ThemeUtils {


    /** Temporary array used internally to resolve attributes.  */
    private val TEMP_ATTR = IntArray(1)

    /**
     * Convenience method for retrieving a themed color value.
     *
     * @param context the [Context] to resolve the theme attribute against
     * @param attr    the attribute corresponding to the color to resolve
     * @return the color value of the resolved attribute
     */
    @ColorInt
    fun resolveColor(context: Context, @AttrRes attr: Int): Int {
        return resolveColor(context, attr, null /* stateSet */)
    }

    /**
     * Convenience method for retrieving a themed color value.
     *
     * @param context  the [Context] to resolve the theme attribute against
     * @param attr     the attribute corresponding to the color to resolve
     * @param stateSet an array of [android.view.View] states
     * @return the color value of the resolved attribute
     */
    @ColorInt
    fun resolveColor(context: Context, @AttrRes attr: Int, @AttrRes stateSet: IntArray?): Int {
        var a: TypedArray
        synchronized(TEMP_ATTR) {
            TEMP_ATTR[0] = attr
            a = context.obtainStyledAttributes(TEMP_ATTR)
            try {
                if (stateSet == null) {
                    return a.getColor(0, Color.RED)
                }
                val colorStateList = a.getColorStateList(0)
                return colorStateList?.getColorForState(stateSet, Color.RED) ?: Color.RED
            } finally {
                a.recycle()
            }
        }

    }

    /**
     * Convenience method for retrieving a themed drawable.
     *
     * @param context the [Context] to resolve the theme attribute against
     * @param attr    the attribute corresponding to the drawable to resolve
     * @return the drawable of the resolved attribute
     */
    fun resolveDrawable(context: Context, @AttrRes attr: Int): Drawable? {
        var a: TypedArray
        synchronized(TEMP_ATTR) {
            TEMP_ATTR[0] = attr
            a = context.obtainStyledAttributes(TEMP_ATTR)
            try {
                return a.getDrawable(0)
            } finally {
                a.recycle()
            }
        }

    }
}