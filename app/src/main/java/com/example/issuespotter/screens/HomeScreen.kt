package com.example.issuespotter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.issuespotter.auth.AuthViewModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val image_url: String? = null,
    val user_id: String,
    val status: String = "Open",
    val created_at: String? = null,
    val address: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Fetch reports when screen is launched
    LaunchedEffect(Unit) {
        loadReports(authViewModel, { reports = it }, { error = it }, { isLoading = false })
    }

    Card(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Civic Track")
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* Handle menu click */ }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu Icon"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    loadReports(
                                        authViewModel,
                                        { reports = it },
                                        { error = it },
                                        { isLoading = false }
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = "Refresh Reports",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else if (reports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reports yet. Be the first to report an issue!",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        tonalElevation = 2.dp
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(reports) { report ->
                                ReportListItem(report = report)
                            }
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate("reportNewIssue") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7E57C2)
                    )
                ) {
                    Text("Report an Issue", fontSize = 16.sp)
                }
            }
        }
    }
}

private suspend fun loadReports(
    authViewModel: AuthViewModel,
    onSuccess: (List<Report>) -> Unit,
    onError: (String) -> Unit,
    onComplete: () -> Unit
) {
    try {
        val currentUser = authViewModel.supabase.auth.currentUserOrNull()
        if (currentUser == null) {
            onError("User not authenticated")
            return
        }

        // Fetch reports from Supabase
        val reports = authViewModel.supabase.postgrest["reports"]
            .select()
            .decodeList<Report>()

        onSuccess(reports)
    } catch (e: Exception) {
        onError("Failed to load reports: ${e.message}")
    } finally {
        onComplete()
    }
}

@Composable
fun ReportListItem(report: Report) {
    val statusColor = when (report.status.lowercase()) {
        "open" -> Color(0xFF4CAF50) // Green
        "in progress" -> Color(0xFFFFC107) // Amber
        "resolved" -> Color(0xFF5E35B1) // Purple
        else -> Color(0xFF9E9E9E) // Gray for unknown status
    }


    val displayAddress = report.description.take(50) + if (report.description.length > 50) "..." else ""

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7E57C2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayAddress,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Category: ${report.category}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Status indicator on the right
            Surface(
                shape = RoundedCornerShape(50),
                color = statusColor
            ) {
                Text(
                    text = report.status,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}