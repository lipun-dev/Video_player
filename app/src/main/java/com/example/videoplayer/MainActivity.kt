package com.example.videoplayer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.videoplayer.dataStore.prefDataStore
import com.example.videoplayer.domain.RepoImpal
import com.example.videoplayer.presentation.navigation.AppNavigation
import com.example.videoplayer.presentation.navigation.NavigationItem
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import com.example.videoplayer.viewModel.MyViewModel


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MyViewModel> {
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = RepoImpal()
                val pref = prefDataStore(context = this@MainActivity)
                @Suppress("UNCHECKED_CAST")
                return MyViewModel(repo,  this@MainActivity.application,pref) as T
            }
        }
    }

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        processIntent(intent)
        setContent {
            VideoPlayerTheme {
                AppNavigation(
                    startDestination = NavigationItem.HomeScreen,
                    viewModel = viewModel
                )

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
                val title = extractVideoTitle(uri)
                Log.d("IntentHandler", "Received video URI: $uri")

                // Pass data to ViewModel instead of holding it in Activity
                viewModel.handleExternalIntent(uri, title)
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

}
