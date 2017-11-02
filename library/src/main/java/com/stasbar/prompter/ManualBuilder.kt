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
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

/**
 * Created by stasbar on 31.10.2017
 */
class ManualBuilder internal constructor(activity: AppCompatActivity, private val destinationView: TextView) : Builder(activity) {

    init {
        addOnValueChangeListener {
            destinationView.text = it
        }
    }

    override fun title(@StringRes stringRes: Int) = apply {
        this.title = activity.getString(stringRes)
    }

    override fun title(title: String) = apply {
        this.title = title
    }

    override fun message(message: String) = apply {
        this.message = message
    }

    override fun message(@StringRes stringRes: Int) = apply {
        this.message = activity.getString(stringRes)
    }


    override fun inputType(inputType: Int) = apply {
        this.inputType = inputType
    }

    override fun hintMode() = apply { this.hintMode = true }

    override fun allowEmpty() = apply { this.allowEmpty = true }

    override fun currentValue(currentValue: String) = apply {
        this.currentValue = currentValue
    }

    override fun currentValue(currentValue: Number) = apply {
        this.currentValue = currentValue.toString()
    }

    override fun addOnValueChangeListener(onValueChanged: (String) -> Unit) = apply {
        onValueChangedListeners.add(object : OnChangeListener {
            override fun onChangeListener(newValue: String) {
                onValueChanged(newValue)
            }
        })
    }

    override fun setOnValueChangeListener(onValueChanged: (String) -> Unit) = apply {
        onValueChangedListeners.clear()
        onValueChangedListeners.add(object : OnChangeListener {
            override fun onChangeListener(newValue: String) {
                onValueChanged(newValue)
            }
        })
    }


    override fun validate(failMessage: String, validator: (String) -> Boolean) = apply {
        this.validator = validator
        this.failMessage = failMessage
    }

    override fun validate(validator: (String) -> Boolean) = apply {
        this.validator = validator
    }

    //Expose the show method
    public override fun show() {
        super.show()
    }

    override fun figureCurrentValue(): String {
        return destinationView.text.toString()
    }

    override fun figureDefaultInputType(): Int {
        return destinationView.inputType
    }


}