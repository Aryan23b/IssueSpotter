package com.example.issuespotter.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.issuespotter.R
import com.example.issuespotter.auth.AuthViewModel
import com.example.issuespotter.auth.ReportDetailState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDetailScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    reportId: String?
) {
    val isDarkTheme by authViewModel.isDarkTheme.collectAsState()
    val reportDetailState by authViewModel.reportDetailState.collectAsState()

    val topAppBarColor = if (isDarkTheme) Color(0xFF06154C) else MaterialTheme.colorScheme.primary
    val contentColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onPrimary
    val screenBackgroundColor = if (isDarkTheme) Color(0xFF06154C) else MaterialTheme.colorScheme.background
    val textColorOnBackground = if (isDarkTheme) Color.White else Color.Black
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF1E2A5E) else Color(0xFFE8EAF6)
    val buttonBackgroundColor = if (isDarkTheme) Color(0xFF7E57C2) else MaterialTheme.colorScheme.secondary
    val buttonContentColor = Color.White

    val userUpvotedReports by authViewModel.userUpvotedReports.collectAsState()
    val isUpvoting by authViewModel.isUpvoting.collectAsState()


    LaunchedEffect(reportId) {
        if (reportId != null) {
            authViewModel.fetchReportById(reportId)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            authViewModel.clearReportDetailState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ISSUE DETAILS",  fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = contentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topAppBarColor)
            )
        },
        containerColor = screenBackgroundColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (reportId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: Report ID not found.", color = textColorOnBackground, fontSize = 18.sp)
                }
                return@Scaffold
            }

            when (val state = reportDetailState) {
                is ReportDetailState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = textColorOnBackground)
                    }
                }
                is ReportDetailState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is ReportDetailState.Success -> {
                    val report = state.report
                    val hasUpvoted = userUpvotedReports.contains(reportId)
                    val isCurrentlyUpvoting = isUpvoting?.first == reportId && isUpvoting?.second == true

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Title
                        Text(
                            text = report.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColorOnBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Description
                        DetailCard(
                            title = "Description",
                            content = report.description,
                            isDarkTheme = isDarkTheme,
                            cardBackgroundColor = cardBackgroundColor,
                            textColor = textColorOnBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category and Status in Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Category
                            DetailCard(
                                title = "Category",
                                content = report.category,
                                isDarkTheme = isDarkTheme,
                                cardBackgroundColor = cardBackgroundColor,
                                textColor = textColorOnBackground,
                                modifier = Modifier.weight(1f)
                            )

                            // Status
                            DetailCard(
                                title = "Status",
                                content = report.status,
                                isDarkTheme = isDarkTheme,
                                cardBackgroundColor = when (report.status.lowercase()) {
                                    "open" -> if (isDarkTheme) Color(0xFF4CAF50).copy(alpha = 0.3f) else Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    "in progress" -> if (isDarkTheme) Color(0xFFFFC107).copy(alpha = 0.3f) else Color(0xFFFFC107).copy(alpha = 0.2f)
                                    "resolved" -> if (isDarkTheme) Color(0xFF5E35B1).copy(alpha = 0.3f) else Color(0xFF5E35B1).copy(alpha = 0.2f)
                                    else -> cardBackgroundColor
                                },
                                textColor = textColorOnBackground,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reported Image (if available)
                        if (!report.image_url.isNullOrBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                            ) {
                                Column {
                                    Text(
                                        text = "Reported Image",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = textColorOnBackground.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(report.image_url)
                                            .placeholder(R.drawable.placeholder_dark)
                                            .error(R.drawable.placeholder_dark)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Reported Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Google Maps Location (if available)
                        if (report.latitude != null && report.longitude != null) {
                            val location = LatLng(report.latitude, report.longitude)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
                                onClick = {
                                    // TODO: Open full screen map or navigation
                                }
                            ) {
                                Column {
                                    Text(
                                        text = "Location",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = textColorOnBackground.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                    GoogleMap(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp),
                                        cameraPositionState = rememberCameraPositionState {
                                            position = CameraPosition.fromLatLngZoom(location, 15f)
                                        }
                                    ) {
                                        Marker(
                                            state = MarkerState(position = location),
                                            title = "Issue Location"
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Upvote Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Community Support",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorOnBackground,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Upvote Count with Icon
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (hasUpvoted) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                            contentDescription = "Upvotes",
                                            tint = if (hasUpvoted) Color(0xFF4CAF50) else textColorOnBackground,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "${report.upvote_count ?: 0} Upvotes",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = textColorOnBackground
                                        )
                                    }

                                    // Upvote Button
                                    Button(
                                        onClick = {
                                            if (!hasUpvoted && !isCurrentlyUpvoting) {
                                                authViewModel.upvoteReport(reportId)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (hasUpvoted) Color.Gray else buttonBackgroundColor,
                                            contentColor = buttonContentColor
                                        ),
                                        modifier = Modifier.height(48.dp),
                                        enabled = !hasUpvoted && !isCurrentlyUpvoting
                                    ) {
                                        if (isCurrentlyUpvoting) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = if (hasUpvoted) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                                contentDescription = "Upvote",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (hasUpvoted) "Upvoted" else "Upvote",
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                if (hasUpvoted) {
                                    Text(
                                        text = "✓ You've upvoted this issue",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                ReportDetailState.Idle -> {

                }
            }
        }
    }
}

@Composable
fun DetailCard(
    title: String,
    content: String,
    isDarkTheme: Boolean,
    cardBackgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, isDarkTheme: Boolean) {
    val textColorOnBackground = if (isDarkTheme) Color.White else Color.Black
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = textColorOnBackground.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = textColorOnBackground
        )
    }
}

