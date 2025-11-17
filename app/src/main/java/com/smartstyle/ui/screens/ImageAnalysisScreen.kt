package com.smartstyle.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartstyle.ui.viewmodel.ImageAnalysisViewModel
import com.smartstyle.util.Result
import kotlinx.coroutines.launch

@Composable
fun ImageAnalysisScreen(viewModel: ImageAnalysisViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val labelsState by viewModel.labels.collectAsState()
    val embeddingState by viewModel.embedding.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val bitmapState by viewModel.bitmap.collectAsState()

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // convert URI to bitmap asynchronously in ViewModel
            viewModel.loadBitmapFromUri(context, it)
        }
    }

    // Camera preview (TakePicturePreview returns a Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(TakePicturePreview()) { bmp ->
        bmp?.let { viewModel.setBitmap(it) }
    }

    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) cameraLauncher.launch(null)
            else {
                // permission denied — show snackbar or message handled by ViewModel
                viewModel.setError("Camera permission denied")
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("SmartStyle — Image Analysis", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(12.dp))

        // Image preview
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmapState != null) {
                Image(
                    bitmap = bitmapState!!.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("No image selected", style = MaterialTheme.typography.body2)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                // Launch gallery
                galleryLauncher.launch("image/*")
            }, modifier = Modifier.weight(1f)) {
                Text("Pick from Gallery")
            }

            Button(onClick = {
                // Request camera permission if necessary, then launch camera
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val status = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (status == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                } else {
                    cameraLauncher.launch(null)
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Use Camera")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                // Trigger analysis: ML Kit labels + TFLite embedding (ViewModel handles)
                coroutineScope.launch {
                    viewModel.analyzeCurrentImage()
                }
            }, enabled = bitmapState != null && !loading) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(18.dp)) else Text("Analyze Image")
            }

            OutlinedButton(onClick = { viewModel.clearResults() }) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colors.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Results
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Text("Labels", style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(4.dp))
                when (labelsState) {
                    is Result.Success -> {
                        val labels = (labelsState as Result.Success<List<String>>).data
                        Column {
                            labels.take(10).forEach { lbl ->
                                Text("• $lbl", style = MaterialTheme.typography.body2)
                            }
                        }
                    }
                    is Result.Error -> {
                        Text("Label extraction failed", color = MaterialTheme.colors.error)
                    }
                    Result.Success(null as List<String>?) -> { /* no-op */ }
                    else -> {
                        Text("No labels yet", style = MaterialTheme.typography.caption)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text("Embedding", style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(4.dp))
                when (embeddingState) {
                    is Result.Success -> {
                        val emb = (embeddingState as Result.Success<FloatArray>).data
                        Text("Vector length: ${emb.size}", style = MaterialTheme.typography.body2)
                        Text("Sample (first 8 dims): ${emb.take(8).joinToString(", ") { String.format("%.3f", it) }}",
                            style = MaterialTheme.typography.caption)
                    }
                    is Result.Error -> {
                        Text("Embedding extraction failed", color = MaterialTheme.colors.error)
                    }
                    else -> {
                        Text("No embedding yet", style = MaterialTheme.typography.caption)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text("Similarity (top matches)", style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(8.dp))
                // Placeholder: ViewModel currently returns similarity list if implemented
                val sims = viewModel.similarItems.collectAsState().value
                if (sims.isEmpty()) {
                    Text("No similar items calculated yet", style = MaterialTheme.typography.caption)
                } else {
                    Column {
                        sims.forEach { (id, score) ->
                            Text("• $id — ${"%.3f".format(score)}", style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }
        }
    }
}
