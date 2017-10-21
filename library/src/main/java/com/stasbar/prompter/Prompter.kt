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

import android.animation.ObjectAnimator
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
    private var validator: ((String) -> Boolean) = { true }
    private var animateOnFail = true
    private lateinit var failMessage: String
    private var message: String? = null
    private lateinit var title: String
    private var allowEmpty = false
    private var hintMode = false
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // As long as the label box exists, save its state.
        if (editText != null) {
            outState.putString(KEY_LABEL, editText!!.text.toString())
        }
    }

    private fun dispatchArguments() {
        inputType = arguments.getInt(INPUT_TYPE)
        animateOnFail = arguments.getBoolean(ANIMATE_ON_FAIL)
        failMessage = arguments.getString(FAIL_MESSAGE)
        previousValue = arguments.getString(CURRENT_VALUE)
        title = arguments.getString(TITLE_STRING)
        message = arguments.getString(MESSAGE_STRING, null)
        allowEmpty = arguments.getBoolean(ALLOW_EMPTY)
        hintMode = arguments.getBoolean(HINT_MODE)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dispatchArguments()

        val text = if (savedInstanceState != null)
            savedInstanceState.getString(KEY_LABEL)
        else
            previousValue

        val dialog = AlertDialog.Builder(context)
                .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
                .setPositiveButton(getString(R.string.set)) { _, _ -> validateAnd { commit(); dismiss(); } }
                .setTitle(title)
                .create()
        message?.let { dialog.setMessage(message) }
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
            //TODO doesn't work
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)?.isActivated = validate()
        })
        editText!!.setSingleLine()
        editText!!.inputType = inputType
        if (hintMode)
            editText!!.hint = text
        else {
            editText!!.setText(text)
            editText!!.selectAll()
        }

        val padding = resources.getDimensionPixelSize(R.dimen.label_edittext_padding)
        dialog.setView(editText, padding, 0, padding, 0)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }


    /**
     * Bring back the new value via callback to the caller in declared value type
     * If new value is empty then don't do anything
     */
    private fun commit() {
        val input: String = editText!!.text.toString()
        onValueChangedListeners.forEach { it.onChangeListener(input) }

    }


    /**
     * Validate input
     * @return true if input pass validation and false if it fails or any Exception occur
     */
    private fun validate(): Boolean {
        return try {
            (allowEmpty && editText!!.text.toString().isBlank())
                    || validator(editText!!.text.toString())
        } catch (e: Exception) {
            ValidationException(e).printStackTrace()
            false
        }
    }

    /**
     * Validate input and play animation on EditText when it fails
     */
    private fun validateAnd(onSuccess: () -> Unit): Boolean {
        val valid = try {
            validate()
        } catch (e: Exception) {
            false
        }
        if (valid) {
            onSuccess()
        } else if (animateOnFail) {
            ObjectAnimator.ofFloat(editText!!, "translationX"
                    , 0F, 25F, -25F, 25F, -25F, 15F, -15F, 6F, -6F, 0F).start()
        }
        editText!!.error = failMessage

        return valid

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
            validateAnd {
                commit()
                dismissAllowingStateLoss()
            }
        } else
            false

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Stop callbacks from the IME since there is no clickView to process them.
        editText?.setOnEditorActionListener(null)
    }


    companion object {
        private val KEY_LABEL = "label"
        private val TITLE_STRING = "title_string"
        private val MESSAGE_STRING = "message_string"
        private val INPUT_TYPE = "input_type"
        private val CURRENT_VALUE = "current_value"
        private val TRANSITION_NAME = "transition_name"
        private val ANIMATE_ON_FAIL = "animate_on_fail"
        private val FAIL_MESSAGE = "fail_message"
        private val HINT_MODE = "hint_mode"
        private val ALLOW_EMPTY = "allow_empty"

        @JvmStatic
        fun with(view: View): Builder {
            return Builder(view)
        }

        internal fun newInstance(inputType: Int
                                 , title: String
                                 , message: String?
                                 , currentValue: String
                                 , transitionName: String = ""
                                 , hintMode: Boolean = false
                                 , onValueChangedListeners: List<OnChangeListener>
                                 , animateOnFail: Boolean = true
                                 , failMessage: String
                                 , allowEmpty: Boolean = false
                                 , validator: (String) -> Boolean)
                : Prompter {
            val args = Bundle()
            args.putInt(INPUT_TYPE, inputType)
            args.putString(TITLE_STRING, title)
            args.putString(MESSAGE_STRING, message)
            args.putString(CURRENT_VALUE, currentValue)
            args.putString(TRANSITION_NAME, transitionName)
            args.putBoolean(HINT_MODE, hintMode)
            args.putBoolean(ANIMATE_ON_FAIL, animateOnFail)
            args.putString(FAIL_MESSAGE, failMessage)
            args.putBoolean(ALLOW_EMPTY, allowEmpty)
            val fragment = Prompter()
            fragment.arguments = args
            fragment.onValueChangedListeners = onValueChangedListeners
            fragment.validator = validator
            return fragment
        }

    }
}
