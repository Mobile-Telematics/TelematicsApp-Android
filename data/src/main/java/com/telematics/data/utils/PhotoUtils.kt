package com.telematics.data.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.*

class PhotoUtils {

    companion object {

        const val SELECT_PICTURE_REQUEST_CODE = 9000
        const val TAKE_PHOTO_REQUEST_CODE = 9001
        const val CROP_PHOTO_REQUEST_CODE = 9002

        const val IMAGE_TEMP_FILE_NAME = "cameraTemp.png"
        const val IMAGE_TEMP = "photos"

        private var sCallback: Callback? = null

        private const val TAG = "PhotoUtils"
        private const val TEMP_NAME = "qwer.png"

        private var lastFileName = ""

        fun openGallery(fragment: Fragment, fileName: String) {
            val dir = File(getPathToTempFiles(fragment.activity))
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val intent = Intent(Intent.ACTION_PICK)
            lastFileName = fileName
            intent.type = "image/*"
            fragment.startActivityForResult(intent, SELECT_PICTURE_REQUEST_CODE)
        }

        fun openCamera(fragment: Fragment, fileName: String) {
            val dir = File(getPathToTempFiles(fragment.context))
            if (!dir.exists()) {
                dir.mkdirs()
            }
            lastFileName = fileName
            val imageFilePath = dir.toString() + File.separator + IMAGE_TEMP_FILE_NAME
            val originalFile = File(imageFilePath)
            val imageFileUri = FileProvider.getUriForFile(
                fragment.requireContext(), fragment.requireContext()
                    .applicationContext.packageName + ".provider", originalFile
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri)
            fragment.startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST_CODE)
        }

        private fun getPathToTempFiles(context: Context?): String {
            return getStoragePath(context) + File.separator + IMAGE_TEMP + File.separator
        }

        private fun getStoragePath(context: Context?): String? {
            if (context == null) return null
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {
                val externalDir = context.getExternalFilesDir(null)
                if (externalDir != null) return externalDir.toString() + File.separator
            }
            val filesDir = context.filesDir
            return if (filesDir != null) filesDir.absolutePath + File.separator else null
        }

        fun onActivityResult(fragment: Fragment, requestCode: Int, resultCode: Int, data: Intent?) {
            val context: Context? = fragment.activity
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImage = data?.data
                    if (selectedImage!!.scheme == "file") {
                        showCrop(
                            fragment,
                            selectedImage.encodedPath,
                            getPathToTempFiles(context) + lastFileName
                        )
                        return
                    }
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = fragment.activity
                        ?.contentResolver
                        ?.query(selectedImage, filePathColumn, null, null, null)
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            var filePath = cursor.getString(columnIndex)
                            if (filePath == null) filePath =
                                getImagePathFromInputStreamUri(fragment.context, selectedImage)
                            showCrop(
                                fragment,
                                filePath,
                                getPathToTempFiles(context) + lastFileName
                            )
                        }
                        cursor.close()
                    }
                }
            } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    val path: String =
                        getPathToTempFiles(context) + IMAGE_TEMP_FILE_NAME
                    showCrop(fragment, path, getPathToTempFiles(context) + lastFileName)
                }
            } else if (requestCode == CROP_PHOTO_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_CANCELED) {
                    clear(context, lastFileName)
                }
                clear(context, IMAGE_TEMP_FILE_NAME)
            }
        }

        private fun getImagePathFromInputStreamUri(c: Context?, uri: Uri?): String? {
            var inputStream: InputStream? = null
            var filePath: String? = null
            if (uri!!.authority != null) {
                try {
                    inputStream = c!!.contentResolver.openInputStream(uri) // context needed
                    val photoFile = createTemporalFileFrom(c, inputStream)
                    filePath = photoFile!!.path
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        inputStream!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return filePath
        }

        @Throws(IOException::class)
        private fun createTemporalFileFrom(c: Context?, inputStream: InputStream?): File? {
            var targetFile: File? = null
            if (inputStream != null) {
                var read: Int
                val buffer = ByteArray(8 * 1024)
                targetFile = createTemporalFile(c)
                val outputStream: OutputStream = FileOutputStream(targetFile)
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return targetFile
        }

        private fun createTemporalFile(c: Context?): File {
            return File(c?.externalCacheDir, "tempFile.png") // context needed
        }

        private fun showCrop(fragment: Fragment, path: String?, pathTo: String) {
            val from = File(path)
            val to = File(pathTo)
            try {
                copy(from, to)
            } catch (e: IOException) {
                Log.d(TAG, "copy file error", e)
            }
            if (sCallback != null) {
                sCallback!!.openCropScreen(from.absolutePath, to.absolutePath)
                sCallback = null
            } else {
                fragment.onActivityResult(CROP_PHOTO_REQUEST_CODE, Activity.RESULT_OK, null)
            }
        }

        private fun clear(context: Context?, avatarFileName: String?) {
            val file = File(getPathToTempFiles(context), avatarFileName)
            file.delete()
        }

        fun saveToFile(bmp: Bitmap, filename: File, format: CompressFormat?) {
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(filename)
                bmp.compress(format, 90, out) // bmp is your Bitmap instance
            } catch (e: Exception) {
                Log.d(TAG, "saveToFile error", e)
            } finally {
                try {
                    if (out != null) {
                        out.close()
                        Log.d(
                            TAG,
                            "Saved PhotoUtils Camera Result file size MB = " + filename.length()
                                .toDouble() / 1024 / 1024 + ""
                        )
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "saveToFile error", e)
                }
            }
        }

        @Throws(IOException::class)
        fun copy(src: File?, dst: File?) {
            val inStream = FileInputStream(src)
            val outStream = FileOutputStream(dst)
            val inChannel = inStream.channel
            val outChannel = outStream.channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
            inStream.close()
            outStream.close()
        }

        fun setCallback(callback: Callback?) {
            sCallback = callback
        }
    }

    interface Callback {
        fun openCropScreen(fileFrom: String?, fileTo: String?)
    }
}