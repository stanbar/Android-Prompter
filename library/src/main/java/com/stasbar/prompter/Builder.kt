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

import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.widget.TextView
import android.os.Build
import android.support.v4.view.ViewCompat
import android.transition.TransitionInflater


/**
 * Created by stasbar on 19.10.2017
 */
class Builder(val clickView: View) {
    private lateinit var dialog: Prompter

    private var currentValue: String? = null
    private var message: String? = null
    private var title: String = clickView.context.getString(R.string.enter_new_value)
    private var hintMode = false
    private var inputType: Int = InputType.TYPE_NULL
    private var validator: ((String) -> Boolean) = { true }
    private var failMessage = clickView.context.getString(R.string.invalid_value)
    private var animateOnFail: Boolean = true
    private val onValueChangedListeners: ArrayList<OnChangeListener> = ArrayList()
    private var allowEmpty = false
    private var destinationView : View = clickView
    init {
        clickView.setOnClickListener { show() }

        addOnValueChangeListener {
            if (destinationView is TextView)
                (destinationView as TextView).text = it
        }
        populateWithDefaults()
    }
    fun showOn(destinationView : TextView){
        this.destinationView = destinationView
    }

    private fun populateWithDefaults() {
        clickView.isFocusable = false
        clickView.isFocusableInTouchMode = false
        if (clickView is TextView)   // Get type from clickView inputType
            clickView.isCursorVisible = false

        message = when {
            clickView is TextView && clickView.hint != null -> clickView.hint.toString()
            clickView.contentDescription != null -> clickView.contentDescription.toString()
            else -> null
        }


    }

    fun title(@StringRes stringRes: Int) = apply {
        this.title = clickView.context.getString(stringRes)
    }

    fun title(title: String) = apply {
        this.title = title
    }

    fun message(message: String) = apply {
        this.message = message
    }

    fun message(@StringRes stringRes: Int) = apply {
        this.message = clickView.context.getString(stringRes)
    }


    fun inputType(inputType: Int) = apply {
        this.inputType = inputType
    }

    fun hintMode() = apply { this.hintMode = true }

    fun allowEmpty() = apply { this.allowEmpty = true }

    fun currentValue(currentValue: String) = apply {
        this.currentValue = currentValue
    }

    fun currentValue(currentValue: Number) = apply {
        this.currentValue = currentValue.toString()
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


    fun validate(failMessage: String, validator: (String) -> Boolean) = apply {
        this.validator = validator
        this.failMessage = failMessage
    }

    fun validate(validator: (String) -> Boolean) = apply {
        this.validator = validator
    }


    private fun show() {
        var text = ""
        if (clickView is TextView) {
            if (inputType == InputType.TYPE_NULL)
                inputType = clickView.inputType
            if (currentValue == null)
                text = clickView.text.toString()
        }

        val transitionName = "${clickView.id}_prompter_shared_view"
        dialog = Prompter.newInstance(inputType = inputType
                , title = title
                , message = message
                , currentValue = text
                , transitionName = transitionName
                , hintMode = hintMode
                , onValueChangedListeners = onValueChangedListeners
                , validator = validator
                , animateOnFail = animateOnFail
                , allowEmpty = allowEmpty
                , failMessage = failMessage
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.sharedElementEnterTransition = TransitionInflater.from(clickView.context).inflateTransition(android.R.transition.move)
        }
        ViewCompat.setTransitionName(clickView, transitionName)

        val fragmentManager = (clickView.context as AppCompatActivity).supportFragmentManager
        fragmentManager.beginTransaction()
                .add(dialog, "${clickView.id}_prompter")
                .addSharedElement(clickView, transitionName)
                .commit()

    }


}