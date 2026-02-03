package com.example.videoplayer.presentation.ui

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.videoplayer.presentation.Utils.irishGroverFamily
import com.example.videoplayer.presentation.navigation.TabItem
import com.example.videoplayer.viewModel.MyViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController,viewModel: MyViewModel) {

    val tabs = listOf(
        TabItem("Folder", icon = Icons.Default.Folder, filedIcon = Icons.Outlined.Folder),
        TabItem("All Video", icon = Icons.Default.VideoLibrary, filedIcon = Icons.Outlined.VideoLibrary)

    )

    val pagerState = rememberPagerState(pageCount = {tabs.size})
    val scope = rememberCoroutineScope()
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        containerColor = Color(0xFF282525),
        topBar = {
            val topBarBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4A4A4A), // Lighter gray at the top
                    Color(0xFF3A3A3A), // Medium dark gray
                    Color(0xFF282525)  // Fading into the screen background color
                )
            )
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Video Player",
                        // MODIFICATION: Style updated to match your image
                        color = Color.LightGray,
                        fontSize = 28.sp,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.5f), // Darker shadow for 3D effect
                                offset = Offset(x = 2f, y = 4f),
                                blurRadius = 4f
                            )
                        ),
                        fontFamily = irishGroverFamily
                    )
                },
                // MODIFICATION: Apply the gradient brush as the background
                modifier = Modifier.background(topBarBrush),
                // MODIFICATION: Pass the scroll behaviour
                scrollBehavior = scrollBehaviour,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    // MODIFICATION: Set to Transparent so the gradient brush is visible
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    // Set title color in case it's needed (though we overrode it)
                    titleContentColor = Color.LightGray
                )
            )
        },
        bottomBar = {
            SlidingGlassBottomNavBar(
                pagerState = pagerState,
                tabs = tabs,
                onTabSelected = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )
        }

    ){innerPadding->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState, modifier = Modifier.weight(1f)
            ) {page->
                when(page){
                    0->FolderScreen(navController = navController, viewModel = viewModel)
                    1->AllVideoScreen(navController, viewModel = viewModel)
                }

            }

        }

    }

    

}

@Composable
fun SlidingGlassBottomNavBar(
    pagerState: androidx.compose.foundation.pager.PagerState,
    tabs: List<TabItem>,
    onTabSelected: (Int) -> Unit
) {
    val haptics = LocalView.current
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // 1. ANALYSIS: Color Palette Strategy
    // Matches your 'WelcomeScreen' gradients (Dark Grays/Blacks)
    // We use a Vertical Gradient to simulate a curved metallic surface
    val barBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3A3A3A), // Top Highlight (Light catching the edge)
            Color(0xFF282525), // Your App's Surface Color (Middle)
            Color(0xFF181818)  // Deep Shadow (Bottom)
        )
    )

    // 2. ANALYSIS: The "Glass" Slider Look
    // Instead of pure white, we use a "Lens" gradient.
    // It's mostly transparent but catches light at the top-left.
    val glassSliderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f), // Reflected Light
            Color.White.copy(alpha = 0.02f)  // Clear Glass
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, 200f)
    )

    // 3. ANALYSIS: The "Rim Light" Border
    // A high-end 3D feel requires edges. We fade the border from white to transparent.
    val glassBorderBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.5f), // Sharp edge at top
            Color.Transparent               // Fades out at bottom
        )
    )

    val containerShape = RoundedCornerShape(50)

    BoxWithConstraints(//nav bar
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp) // Generous padding for a floating look
            .height(72.dp)
            // Deep shadow to lift the bar off the Video Player background
            .shadow(elevation = 15.dp, shape = containerShape, spotColor = Color.Black)
            .clip(containerShape)
            .background(barBackgroundBrush)
    ) {
        val maxWidth = this.maxWidth
        val tabWidth = maxWidth / tabs.size

        // PHYSICS: Matches your Login Toggle (LowBouncy = Liquid feel)
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * pagerState.currentPage,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            ),
            label = "indicatorOffset"
        )

        // --- LAYER 1: The Sliding Glass Lens --- small one
        Box(
            modifier = Modifier
                .width(tabWidth)
                .fillMaxHeight()
                .padding(6.dp) // Inset slightly so it floats inside
                .offset(x = indicatorOffset)
                .clip(RoundedCornerShape(50))
                .then(
                    if (isAndroid12OrAbove) {
                        Modifier
                            .blur(8.dp) // Stronger blur for "Thick Glass" look
                            .background(glassSliderBrush)
                            .border(width = 1.dp, brush = glassBorderBrush, shape = RoundedCornerShape(50))
                    } else {
                        // Fallback for older phones
                        Modifier
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(width = 1.dp, color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
                    }
                )
        )

        // --- LAYER 2: The Icons & Text ---
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = pagerState.currentPage == index
                val interactionSource = remember { MutableInteractionSource() }

                // Animate scale: Selected pops out, Unselected recedes
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 0.9f,
                    animationSpec = spring(dampingRatio = 0.6f), // Subtle bounce
                    label = "scale"
                )

                // Animate Color: "Silver" for active, "Dark Gray" for inactive
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFFEEEEEE) else Color(0xFF666666),
                    label = "color"
                )

                Column(
                    modifier = Modifier
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onTabSelected(index)
                            haptics.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.icon else tab.filedIcon, // Use filedIcon or unselectedIcon
                        contentDescription = tab.title,
                        tint = contentColor,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(scale)
                    )

                    // Label only shows when selected (Cleaner look) or barely visible when unselected
                    if(isSelected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            color = contentColor,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.scale(scale)
                        )
                    }
                }
            }
        }
    }
}