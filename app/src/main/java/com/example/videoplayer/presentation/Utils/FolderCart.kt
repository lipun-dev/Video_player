package com.example.videoplayer.presentation.Utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.videoplayer.R

@Composable
fun FolderCart(
    folderName: String,
    videoCount: Int,
    onClick: () -> Unit
) {

    val name = folderName.split("/").lastOrNull().toString()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(80.dp) // Give it a fixed height to match the design

            // 1. This shadow creates the "dark" part of the inset.
            // It's a black shadow at the bottom-right.
            .shadow(
                elevation = 8.dp,
                shape = WavyFolderShape(), // Use our custom shape!
                spotColor = Color.Black.copy(alpha = 0.5f), // Dark shadow
                ambientColor = Color.Black.copy(alpha = 0.5f)
            )

            // 2. This border creates the "light" part of the inset.
            // It's a 1dp white highlight at the top-left.
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                ),
                shape = WavyFolderShape()
            )

            // 3. We clip the whole Box to our custom shape.
            .clip(WavyFolderShape())

            // 4. The background is a gradient to give it a 3D surface.
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF383838), // Lighter top
                        Color(0xFF2C2C2C)  // Darker bottom
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 5. The Icon from your design
            Icon(
                // You must import your custom icon into res/drawable
                painter = painterResource(id = R.drawable.folder_icon),
                contentDescription = "Folder",
                modifier = Modifier.size(60.dp),
                // The icon is light, as seen in your design
                tint = Color(0xFFDADADA)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 6. The Text (main content)
            Text(
                text = name,
                color = Color(0xFFBDBDBD), // Light text
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = irishGroverFamily,
                modifier = Modifier.weight(1f) // Give text weight to push count
            )

            // (Optional) Add the video count if you want
             Text(
                text = "$videoCount",
                color = Color(0xFF828282),
                fontSize = 14.sp
             )
        }
    }


}

class WavyFolderShape(
    private val cornerRadius: Float = 60f, // 30.dp.toPx() (adjust as needed)
    private val waveAmplitude: Float = 20f   // The "dip" in the middle
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            // Ensure we don't curve more than possible
            val radius = cornerRadius.coerceAtMost(size.height / 2)
            val amplitude = waveAmplitude.coerceAtMost(size.height / 2)

            val midX = size.width / 2

            // Start at top-left
            moveTo(0f, radius)
            // Top-left corner
            quadraticBezierTo(0f, 0f, radius, 0f)

            // --- Top wavy edge ---
            // Line to start of the wave
            lineTo(midX - (radius * 1.5f), 0f)
            // Quadratic curve for the dip
            quadraticBezierTo(
                midX, amplitude, // Control point (the dip)
                midX + (radius * 1.5f), 0f // End point
            )

            // Line to top-right
            lineTo(size.width - radius, 0f)
            // Top-right corner
            quadraticBezierTo(size.width, 0f, size.width, radius)

            // --- Right edge ---
            lineTo(size.width, size.height - radius)
            // Bottom-right corner
            quadraticBezierTo(size.width, size.height, size.width - radius, size.height)

            // --- Bottom wavy edge ---
            // Line to start of the wave
            lineTo(midX + (radius * 1.5f), size.height)
            // Quadratic curve for the dip
            quadraticBezierTo(
                midX, size.height - amplitude, // Control point (the rise)
                midX - (radius * 1.5f), size.height // End point
            )

            // Line to bottom-left
            lineTo(radius, size.height)
            // Bottom-left corner
            quadraticBezierTo(0f, size.height, 0f, size.height - radius)

            // Close the path
            close()
        }
        return Outline.Generic(path)
    }
}

val irishGroverFamily = FontFamily(
    Font(R.font.irishgrover_regular, FontWeight.Normal)
)