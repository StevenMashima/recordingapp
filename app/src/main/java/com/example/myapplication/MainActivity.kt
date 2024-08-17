package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.cloudinary.android.MediaManager
import com.example.myapplication.ui.component.Background
import com.example.myapplication.ui.component.ButtonState
import com.example.myapplication.ui.component.RecordButton
import com.example.myapplication.ui.component.TopBar
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config: HashMap<String, String> = HashMap()

        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
        config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET
        config["upload_preset"] = "ml_default"

        MediaManager.init(this, config)

        enableEdgeToEdge()
        setContent {
            var screenWidth = remember { mutableStateOf(1) }
            var screenHeight = remember { mutableStateOf(1) }

            var videoPath = ""

            val buttonState = remember { mutableStateOf(ButtonState.START_RECORD) }

            val videoBitmap = remember { mutableStateOf<Bitmap?>(null) }

            var info = remember { mutableStateOf("") }

            fun setInfo(text: String) {
                info.value = text
            }

            fun setButtonState(state: ButtonState) {
                buttonState.value = state
            }

            fun setVideoBitmap(bitmap: Bitmap?) {
                videoBitmap.value = bitmap
            }

            val videoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == RESULT_OK) {
                    val videoUri: Uri? = it.data?.data
                    videoPath = getRealPathFromUri(videoUri).orEmpty()
                    videoBitmap.value =
                        ThumbnailUtils.createVideoThumbnail(
                            videoPath,
                            MediaStore.Video.Thumbnails.MICRO_KIND
                        )
                    buttonState.value = ButtonState.START_UPLOAD
                }
            }

            fun openVideoCamera() {
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                videoLauncher.launch(intent)
                info.value = ""
            }

            val cameraPermissionState = rememberMultiplePermissionsState(
                listOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAMERA,
                )
            ) {
                if (it[android.Manifest.permission.RECORD_AUDIO] == true &&
                    it[android.Manifest.permission.CAMERA] == true) {
                    openVideoCamera()
                } else {
                    info.value = "Camera permissions not granted"
                }
            }

            fun requestPermissions() {
                if (cameraPermissionState.allPermissionsGranted) {
                    openVideoCamera()
                } else {
                    cameraPermissionState.launchMultiplePermissionRequest()
                }
            }

            MyApplicationTheme {
                Background(screenWidth, screenHeight)
                Scaffold(
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {},
                    bottomBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 16.dp, 16.dp, 42.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (videoBitmap.value != null) {
                                Image(
                                    modifier = Modifier
                                        .size(500.dp, 200.dp)
                                        .padding(16.dp),
                                    bitmap = videoBitmap.value!!.asImageBitmap(),
                                    contentDescription = "thumbnail"
                                )
                            }
                            RecordButton(
                                modifier = Modifier
                                    .animateContentSize(),
                                state = buttonState.value,
                                videoPath = videoPath,
                                ::requestPermissions,
                                ::setInfo,
                                ::setButtonState,
                                ::setVideoBitmap
                            )

                            if (info.value.isNotEmpty()) {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = info.value,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(16.dp, 0.dp)) {
                        TopBar(
                            topPadding = innerPadding.calculateTopPadding(),
                            name = "Record!",
                            welcomeText = "Let's "
                        )
                    }
                }
            }
        }
    }

    private fun getRealPathFromUri(uri: Uri?): String? {
        if (uri == null) return ""
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        var filePath: String? = null
        val cursor =
            contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    }
}