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
import android.text.InputType
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager

/**
 * Created by stasbar on 31.10.2017
 */
class AdaptiveBuilder internal constructor(fragmentManager: FragmentManager, val clickView: View) : Builder(fragmentManager) {
    override fun getContext(): Context = clickView.context
    private var destinationView: TextView? = null

    init {
        if (clickView is TextView)
            destinationView = clickView

        clickView.isFocusable = false
        clickView.isFocusableInTouchMode = false
        clickView.setOnClickListener {
            show()
        }
        if (clickView is TextView)   // Get type from clickView inputType
            clickView.isCursorVisible = false


        addOnValueChangeListener {
            destinationView?.text = it
        }
    }

    fun showOn(destinationView: TextView) = apply {
        this.destinationView = destinationView
    }

    override fun figureCurrentValue(): String {
        return if (clickView is TextView) {
            clickView.text.toString()
        } else if (destinationView is TextView) {
            (destinationView as TextView).text.toString()
        } else
            ""
    }

    override fun figureDefaultInputType(): Int {
        return if (clickView is TextView) {
            clickView.inputType
        } else if (destinationView is TextView)
            (destinationView as TextView).inputType
        else InputType.TYPE_CLASS_TEXT

    }
}