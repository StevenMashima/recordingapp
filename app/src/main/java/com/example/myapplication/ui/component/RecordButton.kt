package com.example.myapplication.ui.component

import android.graphics.Bitmap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

@Composable
fun RecordButton(
    modifier: Modifier,
    state: ButtonState,
    videoPath: String,
    onOpenCamera: () -> Unit,
    setInfo: (String) -> Unit,
    setButtonState: (ButtonState) -> Unit,
    setVideoBitmap: (Bitmap?) -> Unit
)  {

    fun getRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    fun uploadVideo() {
        val options: HashMap<String, Any> = HashMap()
        options["resource_type"] = "video"
        options["upload_preset"] = "ml_default"
        options["public_id"] = getRandomString(10)
        MediaManager.get().upload(videoPath).options(options)
            .callback(object: UploadCallback {
                override fun onStart(requestId: String?) {
                    setButtonState(ButtonState.LOADING)
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    setButtonState(ButtonState.LOADING)
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    setButtonState(ButtonState.START_RECORD)
                    setVideoBitmap(null)
                    setInfo("Video uploaded successfully")
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    setButtonState(ButtonState.START_UPLOAD)
                    setInfo("Uploading went wrong err: ${error?.description.orEmpty()}")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {

                }

            })
            .dispatch()
    }

    fun generateText(state: ButtonState): String {
        return when(state) {
            ButtonState.START_RECORD -> "Record a Video"
            ButtonState.START_UPLOAD -> "Upload Now"
            ButtonState.LOADING -> "Uploading... Please Wait"
        }
    }

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x0DFFFFFF)
        ),
        onClick = {
            when(state) {
                ButtonState.START_RECORD -> onOpenCamera()
                ButtonState.START_UPLOAD -> uploadVideo()
                ButtonState.LOADING -> {}
            }
        }
    ) {
        Text(
            text = generateText(state),
            style = MaterialTheme.typography.bodyMedium
        )
    }


}