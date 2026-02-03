package com.example.videoplayer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.videoplayer.dataStore.prefDataStore
import com.example.videoplayer.domain.RepoImpal
import com.example.videoplayer.presentation.navigation.AppNavigation
import com.example.videoplayer.presentation.navigation.NavigationItem
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import com.example.videoplayer.viewModel.MyViewModel


class MainActivity : ComponentActivity() {
    private var pendingVideoUri by mutableStateOf<Uri?>(null)
    private var pendingVideoTitle by mutableStateOf<String?>(null)
    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        processIntent(intent)

        val repo = RepoImpal()
        val pref = prefDataStore(context = this)
        val viewModel = MyViewModel(repo = repo, prefs = pref, application = this.application)
        enableEdgeToEdge()
        setContent {
            VideoPlayerTheme {
                AppNavigation(
                    startDestination = NavigationItem.HomeScreen,
                    viewModel = viewModel,
                    pendingVideoUri = pendingVideoUri,
                    pendingVideoTitle = pendingVideoTitle
                ){
                    clearPendingVideo()
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent)
    }

    @OptIn(UnstableApi::class)
    private fun processIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            val uri = intent.data
            if (uri != null) {
                pendingVideoUri = uri
                pendingVideoTitle = extractVideoTitle(uri)
                Log.d("IntentHandler", "Received video URI: $uri")
            }
        }
    }

    private fun extractVideoTitle(uri: Uri): String {
        return when {
            uri.scheme == "content" -> {
                // Try to get title from MediaStore
                val projection = arrayOf(MediaStore.Video.Media.DISPLAY_NAME)
                contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                    } else {
                        // Fallback: use last path segment
                        uri.lastPathSegment ?: "External Video"
                    }
                } ?: uri.lastPathSegment ?: "External Video"
            }
            else -> {
                // For file URIs, use filename
                uri.lastPathSegment ?: "External Video"
            }
        }
    }

    private fun clearPendingVideo() {
        pendingVideoUri = null
        pendingVideoTitle = null
    }
}
