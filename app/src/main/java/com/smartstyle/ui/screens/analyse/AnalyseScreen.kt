package com.smartstyle.ui.screens.analyse

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnalyseScreen(viewModel: AnalyseViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.setImage(it) } }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) pendingCameraUri?.let { viewModel.setImage(it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createCameraUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val uri = createCameraUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outfit Analyser", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is AnalyseUiState.Idle -> IdleContent(
                    onCamera = ::launchCamera,
                    onGallery = { galleryLauncher.launch("image/*") }
                )

                is AnalyseUiState.ImageReady -> {
                    OutfitImage(state.bitmap)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = ::launchCamera) {
                            Icon(Icons.Default.AddAPhoto, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Retake")
                        }
                        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                            Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Gallery")
                        }
                    }
                    Button(
                        onClick = viewModel::analyzeOutfit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Style, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Analyse Outfit")
                    }
                }

                is AnalyseUiState.Analysing -> {
                    OutfitImage(state.bitmap)
                    Spacer(Modifier.height(8.dp))
                    CircularProgressIndicator()
                    Text(
                        "Analysing your outfit…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is AnalyseUiState.Success -> {
                    OutfitImage(state.bitmap)
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AssessmentCard(state.analysis.assessment)
                            if (state.analysis.tags.isNotEmpty()) {
                                TagsCard(state.analysis.tags)
                            }
                            if (state.analysis.suggestions.isNotEmpty()) {
                                SuggestionsCard(state.analysis.suggestions)
                            }
                            if (!state.saved) {
                                Button(
                                    onClick = viewModel::saveAnalysis,
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Save to History") }
                            } else {
                                FilledTonalButton(
                                    onClick = viewModel::reset,
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Analyse Another Outfit") }
                            }
                        }
                    }
                }

                is AnalyseUiState.Error -> {
                    ErrorCard(state.message)
                    Button(onClick = viewModel::reset) { Text("Try Again") }
                }
            }
        }
    }
}

@Composable
private fun IdleContent(onCamera: () -> Unit, onGallery: () -> Unit) {
    Spacer(Modifier.height(32.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Style,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Add a photo to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = onCamera, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.AddAPhoto, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Camera")
        }
        OutlinedButton(onClick = onGallery, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Gallery")
        }
    }
}

@Composable
private fun OutfitImage(bitmap: android.graphics.Bitmap) {
    AsyncImage(
        model = bitmap,
        contentDescription = "Selected outfit",
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun AssessmentCard(assessment: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Style Assessment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(assessment, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsCard(tags: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Outfit Tags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(tag, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionsCard(suggestions: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Style Tips", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            suggestions.forEach { suggestion ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("•", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(suggestion, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

private fun createCameraUri(context: Context): Uri {
    val file = File.createTempFile("outfit_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
