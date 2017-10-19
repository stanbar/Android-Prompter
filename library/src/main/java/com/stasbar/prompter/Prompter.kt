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

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.NumberPicker
import android.widget.TextView

/**
 * Created by stasbar on 19.10.2017
 */
class Prompter : DialogFragment() {

    private lateinit var onValueChangedListeners: List<OnChangeListener>
    private var editText: AppCompatEditText? = null
    private var previousValue: String = ""
    private var inputType: Int = InputType.TYPE_CLASS_TEXT

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // As long as the label box exists, save its state.
        if (editText != null) {
            outState.putString(KEY_LABEL, editText!!.text.toString())
        }
    }


    @SuppressLint("RestrictedApi")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inputType = arguments.getInt(INPUT_TYPE)

        var text = ""
        if (savedInstanceState != null)
            text = savedInstanceState.getString(KEY_LABEL)
        else if (arguments.containsKey(CURRENT_VALUE)) {
            text = arguments.getString(CURRENT_VALUE)
            previousValue = arguments.getString(CURRENT_VALUE)
        }


        val dialog = AlertDialog.Builder(context)
                .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
                .setMessage(arguments.getString(MESSAGE_STRING))
                .create()
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.set)) { _, _ -> commit(); dismiss() }

        val colorControlActivated = ThemeUtils.resolveColor(context, R.attr.colorControlActivated);
        val colorControlNormal = ThemeUtils.resolveColor(context, R.attr.colorControlNormal);

        editText = AppCompatEditText(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            ViewCompat.setTransitionName(editText!!, arguments.getString(TRANSITION_NAME))

        editText!!.supportBackgroundTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_activated), intArrayOf()),
                intArrayOf(colorControlActivated, colorControlNormal))
        editText!!.setOnEditorActionListener(imeDoneListener)
        editText!!.addTextChangedListener(onTextChanged {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)?.isActivated = !TextUtils.isEmpty(it)
        })
        editText!!.setSingleLine()
        editText!!.inputType = inputType
        editText!!.setText(text)
        editText!!.selectAll()
        val padding = resources.getDimensionPixelSize(R.dimen.label_edittext_padding)

        dialog.setView(editText, padding, 0, padding, 0)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }

    /**
     * Bring back the new value via callback to the caller in declared value type
     * If new value is empty then don't do anything
     */
    @Suppress("UNCHECKED_CAST")
    private fun commit() {
        val input: String = editText!!.text.toString()
        onValueChangedListeners.forEach {
            it.onChangeListener(input)
        }

    }

    /**
     * Listener that invokes update method every time text changes
     * @param update method to be invoked whenever text changes
     */
    private fun onTextChanged(update: (CharSequence?) -> Unit) = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            update(text)
        }
    }

    /**
     * Handles completing the new value edit from the IME keyboard.
     */
    private val imeDoneListener: TextView.OnEditorActionListener = TextView.OnEditorActionListener { _: TextView, actionId: Int, _: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            commit()
            dismissAllowingStateLoss()
            true
        } else
            false

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Stop callbacks from the IME since there is no view to process them.
        editText?.setOnEditorActionListener(null)
    }


    companion object {
        private val KEY_LABEL = "label"
        private val MESSAGE_STRING = "message_string"
        private val INPUT_TYPE = "input_type"
        private val CURRENT_VALUE = "current_value"
        private val TRANSITION_NAME = "transition_name"
        @JvmStatic
        fun with(view: View): Builder {
            return Builder(view)
        }

        internal fun newInstance(inputType: Int, message: String, currentValue: String, transitionName: String = "", onValueChangedListeners: List<OnChangeListener>): Prompter {
            val args = Bundle()
            args.putInt(INPUT_TYPE, inputType)
            args.putString(MESSAGE_STRING, message)
            args.putString(CURRENT_VALUE, currentValue)
            args.putString(TRANSITION_NAME, transitionName)
            val fragment = Prompter()
            fragment.arguments = args
            fragment.onValueChangedListeners = onValueChangedListeners
            return fragment
        }

    }
}
