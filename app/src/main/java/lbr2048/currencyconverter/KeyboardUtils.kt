package lbr2048.currencyconverter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// https://stackoverflow.com/questions/1109022/close-hide-android-soft-keyboard

fun hideKeyboardFrom(context: Context, view: View) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showKeyboardFrom(context: Context, view: View) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}