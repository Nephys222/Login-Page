package com.nilearning.palace.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

const val TOKEN = "token"
const val PHONE_NUMBER = "phone"
const val USERNAME = "username"
const val REGION = "region"
const val AUTH_PREF_FILE = "auth"
const val PROFILE_IMAGE_URI = "profile_image"

const val MILLIS_IN_FUTURE = (1000 * 60 * 2).toLong()

const val REQUEST_PHOTO_LIBRARY = 5
const val REQUEST_IMAGE_CAPTURE = 6

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Any.TAG(): String = this::class.java.name