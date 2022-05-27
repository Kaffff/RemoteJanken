package com.example.remotejanken


import android.graphics.*
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.File
import java.util.concurrent.Executor

@Composable
fun CameraView(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val outputDir = context.filesDir.absolutePath
    val outputPath = File(outputDir, "my.jpg")
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifeCycleOwner = LocalLifecycleOwner.current
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(Size(300, 300))
            .build()
    }
    val cameraExecutor: Executor = remember {
        ContextCompat.getMainExecutor(context)
    }

    LaunchedEffect(Unit) {
        outputPath.delete()
    }


    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        BoxWithConstraints {
            val w = with(LocalDensity.current) {
                constraints.maxWidth.toDp()
            }
            val h = with(LocalDensity.current) {
                constraints.maxHeight.toDp()
            }
            var min = w
            if (h < w) min = h
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifeCycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    }, cameraExecutor)
                    previewView
                },
                modifier = Modifier
                    .height(min)
                    .width(min)
            )
        }
        Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.BottomCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        takePhoto(
                            outputPath,
                            imageCapture,
                            cameraExecutor,
                            navController,
                            viewModel
                        )
                    },
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_photo_camera_24),
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    )
                }
                Text(text = "じゃんけんの手を撮影してください", fontSize = 20.sp)
            }
        }
    }
}


private fun takePhoto(
    filePath: File,
    imageCapture: ImageCapture,
    cameraExecutor: Executor,
    navController: NavController,
    viewModel: MainViewModel
) {
    val outputOptions = ImageCapture.OutputFileOptions.Builder(filePath).build()
    imageCapture.takePicture(outputOptions, cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("takePhoto", "onError: ${exception.message}")
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("takePhoto", "onImageSaved success")
                Log.d("takePhoto", "${outputFileResults.savedUri?.path}")
                viewModel.sendImage(File(outputFileResults.savedUri?.path))
                navController.navigate("janken")
            }
        }
    )
}


