package com.nilearning.palace

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import coil.load
import com.nilearning.palace.databinding.ActivityProfileBinding
import com.nilearning.palace.util.*
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var regionName = "ashgabat"
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.entryNumber.setText("+${PreferenceHelper.getString(PHONE_NUMBER, "")}")
        binding.userName.text = if (PreferenceHelper.getString(USERNAME, "") == "") resources.getString(R.string.user) else PreferenceHelper.getString(USERNAME, "")
        binding.entryUser.setText(PreferenceHelper.getString(USERNAME, ""))
        binding.regionName.setText(PreferenceHelper.getString(REGION, ""))

        binding.profileImage.load(Uri.fromFile(File(PreferenceHelper.getString(PROFILE_IMAGE_URI, ""))))

        val regions = resources.getStringArray(R.array.regions)
        val options = resources.getStringArray(R.array.options)
        val optionsValues = resources.getStringArray(R.array.optionsValues)

        val bottomSheet = BottomSheet().apply {
            setSimpleItems(regions.toList()) { index ->
                binding.regionName.setText(regions[index])
                regionName = regions[index]
                PreferenceHelper.putString(REGION, regionName)
            }
        }

        val bottomSheetOptions = BottomSheetOptions().apply {
            setSimpleItems(options.toList()) { index ->
                when (optionsValues[index]) {
                    "camera" -> dispatchTakePictureIntent()
                    "gallery" -> PreferenceHelper.openImagePicker(requireActivity())
                }
            }
        }

        binding.profileImageLayout.setOnClickListener {
            bottomSheetOptions.show(supportFragmentManager, null)
        }

        binding.regionName.setOnClickListener {
            bottomSheet.show(supportFragmentManager, null)
        }

        binding.saveButton.setOnClickListener {

            if (binding.entryUser.text.toString() != "") {
                PreferenceHelper.putString(USERNAME, binding.entryUser.text.toString())
                PreferenceHelper.putString(REGION, binding.regionName.text.toString())

                val intent = Intent(this, com.nilearning.palace.MainActivity::class.java)
                intent.putExtra("frag", R.id.navigation_profile)
                startActivity(intent)
            } else {
                this.hideKeyboard(binding.root)
                Snackbar.make(binding.root, R.string.fill_name, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.logoutButton.setOnClickListener {
            PreferenceHelper.setToken("")
            PreferenceHelper.putString(USERNAME, "")
            PreferenceHelper.putString(REGION, "")
            PreferenceHelper.putString(PHONE_NUMBER, "")
            PreferenceHelper.putString(PROFILE_IMAGE_URI, "")

            val intent = Intent(this, com.nilearning.palace.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            intent.putExtra("frag", R.id.navigation_profile)
            startActivity(intent)

            finishAffinity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PHOTO_LIBRARY && resultCode == Activity.RESULT_OK) {
            binding.profileImage.setImageBitmap(PreferenceHelper.getImageFromGallery(this, data?.data!!))
            PreferenceHelper.saveImage(this, PreferenceHelper.getImageFromGallery(this, data.data!!)!!, "profile_image")
            PreferenceHelper.putString(PROFILE_IMAGE_URI, "${this.filesDir}/profile_image")
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            binding.profileImage.setImageBitmap(getCapturedImage())
            PreferenceHelper.saveImage(this, getCapturedImage(), "profile_image")
            PreferenceHelper.putString(PROFILE_IMAGE_URI, "${this.filesDir}/profile_image")
        }
    }

    /**
     * dispatchTakePictureIntent():
     *     Start the Camera app to take a photo.
     */
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.nilearning.palace.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    /**
     * createImageFile():
     *     Generates a temporary image file for the Camera app to write to.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    /**
     * getCapturedImage():
     *      Decodes and crops the captured image from camera.
     */
    private fun getCapturedImage(): Bitmap {

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }
    }

    /**
     * rotateImage():
     *     Decodes and crops the captured image from camera.
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}