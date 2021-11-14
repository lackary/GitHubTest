package com.lacklab.app.githubtest.utils.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText

fun hideKeyboard(context: Context, textInputEditText: TextInputEditText) {
    val inputMethManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
    inputMethManager.hideSoftInputFromWindow(textInputEditText.windowToken, 0)
}