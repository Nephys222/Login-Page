package com.nilearning.palace.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.preference.PreferenceManager
import coil.ImageLoader
import coil.load
import java.io.FileOutputStream
import java.io.InputStream


object PreferenceHelper {
    /**
     * for normal preferences
     */
    private lateinit var settings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    /**
     * For sensitive data (like token)
     */
    private lateinit var authSettings: SharedPreferences
    private lateinit var authEditor: SharedPreferences.Editor


    /**
     * set the context that is being used to access the shared preferences
     */
    fun initialize(context: Context) {
        settings = getDefaultSharedPreferences(context)
        editor = settings.edit()

        authSettings = getAuthenticationPreferences(context)
        authEditor = authSettings.edit()
    }

    fun putString(key: String?, value: String) {
        editor.putString(key, value).commit()
    }

    fun getString(key: String?, defValue: String?): String {
        return settings.getString(key, defValue)!!
    }

    fun getToken(): String {
        return authSettings.getString(TOKEN, "")!!
    }

    fun setToken(newValue: String) {
        authEditor.putString(TOKEN, newValue).apply()
    }

//    fun getUsername(): String {
//        return authSettings.getString(PreferenceKeys.USERNAME, "")!!
//    }
//
//    fun setUsername(newValue: String) {
//        authEditor.putString(PreferenceKeys.USERNAME, newValue).apply()
//    }

    private fun getDefaultSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun getAuthenticationPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(AUTH_PREF_FILE, Context.MODE_PRIVATE)
    }

    fun openImagePicker(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        activity.startActivityForResult(intent, REQUEST_PHOTO_LIBRARY)
    }

    fun loadImage(context: Context, url: String?, target: ImageView) {
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(true)
            .build()

        target.load(url, imageLoader)
    }

    fun saveImage(context: Context, b: Bitmap, imageName: String?) {
        val foStream: FileOutputStream
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream)
            foStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getImageFromGallery(context: Context, imageUri: Uri): Bitmap? {
        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(imageUri)
            val options = BitmapFactory.Options()
            return BitmapFactory.decodeStream(inputStream, null, options)

        } finally {
            inputStream?.close()
        }
    }
}
