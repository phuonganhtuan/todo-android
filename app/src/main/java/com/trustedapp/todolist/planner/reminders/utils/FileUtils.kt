package com.trustedapp.todolist.planner.reminders.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.models.entity.AttachmentEntity
import java.io.File
import java.io.FileOutputStream


object FileUtils {

    fun openAttachment(context: Context, attachmentEntity: AttachmentEntity) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    FileProvider.getUriForFile(
                        context,
                        context.packageName + ".provider",
                        File(attachmentEntity.path)
                    )
                ).apply {
                    flags = FLAG_GRANT_READ_URI_PERMISSION
                }
            )
        } catch (ane: ActivityNotFoundException) {
            val message = context.getString(R.string.could_not_find_app_handle_attachment)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } catch (exception: Exception) {
            val message = context.getString(R.string.could_not_open_this_attachment)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun copyFileToInternalStorage(
        context: Context,
        path: String,
        newDirName: String = "todolist"
    ): String {
        val returnUri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            File(path)
        )
        val returnCursor = context.contentResolver.query(
            returnUri, arrayOf(
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
            ), null, null, null
        )
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val name = returnCursor?.getString(nameIndex!!)
        val output: File = if (newDirName != "") {
            val dir = File(context.filesDir.toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            File(context.filesDir.toString() + "/" + newDirName + "/" + name)
        } else {
            File(context.filesDir.toString() + "/" + name)
        }
        returnCursor?.close()
        try {
            val inputStream = context.contentResolver.openInputStream(returnUri)
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also { read = it!! } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }
        return output.path
    }

    fun getAudioFileLength(context: Context, uri: Uri, stringFormat: Boolean): String {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(getRealPathFromURI(context, uri))
        val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millSecond = duration!!.toInt()
        if (millSecond < 0) return 0.toString() // if some error then we say duration is zero
        if (!stringFormat) return millSecond.toString()
        val hours: Int
        val minutes: Int
        var seconds = millSecond / 1000
        hours = seconds / 3600
        minutes = seconds / 60 % 60
        seconds %= 60
        val stringBuilder = StringBuilder()
        if (hours in 1..9) stringBuilder.append("0").append(hours)
            .append(":") else if (hours > 0) stringBuilder.append(hours).append(":")
        if (minutes < 10) stringBuilder.append("0").append(minutes)
            .append(":") else stringBuilder.append(minutes).append(":")
        if (seconds < 10) stringBuilder.append("0").append(seconds) else stringBuilder.append(
            seconds
        )
        return stringBuilder.toString()
    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
        val result: String?
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex("_id")
            result = cursor.getString(1)
            cursor.close()
        }
        return result
    }
}


