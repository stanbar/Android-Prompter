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

import android.app.Activity
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.security.SignedObject
import android.transition.Fade
import android.os.Build
import android.support.v4.view.ViewCompat
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.widget.Button


/**
 * Created by stasbar on 19.10.2017
 */
class Builder(val view: View) {
    private lateinit var dialog: Prompter

    var signed = false
        private set
    var currentValue: String? = null
        private set
    var message: String = view.context.getString(R.string.enter_new_value)
        private set
    var hintMode = false
        private set
    var inputType: Int = InputType.TYPE_CLASS_TEXT
        private set

    private val onValueChangedListeners: ArrayList<OnChangeListener> = ArrayList()


    init {
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        view.setOnClickListener { show() }
        message = view.contentDescription.toString()
        if (view is TextView) {  // Get type from view inputType
            view.isCursorVisible = false
            message = view.hint.toString()
        }

        addOnValueChangeListener {
            if (view is TextView)
                view.text = it
        }
    }

    fun message(message: String) = apply {
        this.message = message
    }

    fun message(@StringRes messageRes: Int) = apply {
        this.message = view.context.getString(messageRes)
    }

    fun signed(isSigned: Boolean) = apply {
        this.signed = signed
    }

    fun currentValue(currentValue: String) = apply {
        this.currentValue = currentValue
    }

    fun addOnValueChangeListener(onValueChanged: (String) -> Unit) = apply {
        onValueChangedListeners.add(object : OnChangeListener {
            override fun onChangeListener(newValue: String) {
                onValueChanged(newValue)
            }
        })
    }

    fun setOnValueChangeListener(onValueChanged: (String) -> Unit) = apply {
        onValueChangedListeners.clear()
        onValueChangedListeners.add(object : OnChangeListener {
            override fun onChangeListener(newValue: String) {
                onValueChanged(newValue)
            }
        })
    }


    private fun show() {
        var text = ""
        if (view is TextView) {
            inputType = view.inputType
            if (currentValue == null)
                text = view.text.toString()
        }

        signed = inputType and InputType.TYPE_NUMBER_FLAG_SIGNED > 0

        val transitionName = "${view.id}_prompter_shared_view"
        dialog = Prompter.newInstance(inputType = inputType, message = message
                , currentValue = text, transitionName = transitionName, onValueChangedListeners = onValueChangedListeners)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.sharedElementEnterTransition = TransitionInflater.from(view.context).inflateTransition(android.R.transition.move)
        }
        ViewCompat.setTransitionName(view, transitionName)

        val fragmentManager = (view.context as AppCompatActivity).supportFragmentManager
        fragmentManager.beginTransaction()
                .add(dialog, "${view.id}_prompter")
                .addSharedElement(view, transitionName)
                .commit()

    }
}