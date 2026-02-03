package com.example.videoplayer.presentation.Utils

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    topAppBarText: String,
    navController: NavController,


    ) {
    val topBarBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4A4A4A),
            Color(0xFF3A3A3A),
            Color(0xFF282525)
        )
    )

    // MODIFICATION 3: Change TopAppBar to CenterAlignedTopAppBar to match source
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = topAppBarText,
                // MODIFICATION 4: Apply exact text styling (Color, Font, Shadow)
                color = Color.LightGray,
                fontSize = 28.sp,
                fontFamily = irishGroverFamily, // Ensure this variable is accessible here
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(x = 2f, y = 4f),
                        blurRadius = 4f
                    )
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    // MODIFICATION 5: Ensure icon is visible against dark background
                    tint = Color.LightGray
                )
            }
        },
        // MODIFICATION 6: Apply gradient background
        modifier = Modifier.background(topBarBrush),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            // MODIFICATION 7: Make container transparent so gradient shows
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = Color.LightGray
        )
    )

}