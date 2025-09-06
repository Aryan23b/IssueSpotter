package com.example.issuespotter.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Added for item clicks
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AccountCircle
// Import theme toggle icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
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
    val status: String = "pending",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val upvote_count: Int = 0,
    val created_at: String? = null,
    val user_has_upvoted: Boolean = false,
    val upvoted_by: List<String> = emptyList()
)

data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isDarkTheme by authViewModel.isDarkTheme.collectAsState()

    var selectedFilterCategory by remember { mutableStateOf<String?>(null) }
    val allIssueCategories = listOf("ROADS", "LIGHTING", "WATER SUPPLY", "CLEANLINESS", "PUBLIC SAFETY", "OBSTRUCTION")
    val filterDropdownCategories = remember { listOf("ALL") + allIssueCategories }

    val filteredReports by remember(reports, selectedFilterCategory) {
        derivedStateOf {
            if (selectedFilterCategory == null) {
                reports
            } else {
                reports.filter { it.category.equals(selectedFilterCategory, ignoreCase = true) }
            }
        }
    }

    val navItems = listOf(
        NavigationItem("Home", "home", Icons.AutoMirrored.Filled.Article, Icons.Outlined.Article),
        NavigationItem("Profile", "profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle),
        NavigationItem("Rating", "rating", Icons.Filled.Star, Icons.Outlined.Star)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        loadReports(authViewModel, { reports = it }, { error = it }, { isLoading = false })
    }

    val currentBackgroundColor = if (isDarkTheme) Color(0xFF06154C) else Color(0xFFF0F0F0)
    val mediumPathColor = if (isDarkTheme) Color(0xFF494E8A) else Color(0xFFD0D0FF)
    val lightPathColor = if (isDarkTheme) Color(0xFFCACFFF) else Color(0xFFE0E0FF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val semiTransparentTextColor = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f)
    val iconButtonColor = if (isDarkTheme) Color.White else Color.Black
    val navDrawerContainerColor = if (isDarkTheme) Color(0xFF06154C) else Color.White
    val navItemUnselectedColor = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else Color.Gray
    val navItemSelectedColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
    val navItemSelectedContainerColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentBackgroundColor)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val mediumColoredPath = Path().apply {
                val mediumColoredPoint1 = Offset(0f, height * 0.8f)
                val mediumColoredPoint2 = Offset(width * 0.25f, height * 0.9f)
                val mediumColoredPoint3 = Offset(width * 0.5f, height * 0.75f)
                val mediumColoredPoint4 = Offset(width * 0.75f, height * 0.95f)
                val mediumColoredPoint5 = Offset(width * 1.4f, height * 0.6f)
                moveTo(mediumColoredPoint1.x, mediumColoredPoint1.y)
                lineTo(width + 100f, height + 100f)
                lineTo(-100f, height + 100f)
                close()
            }
            val lightColoredPath = Path().apply {
                val lightPoint1 = Offset(0f, height * 0.85f)
                val lightPoint2 = Offset(width * 0.2f, height * 0.95f)
                val lightPoint3 = Offset(width * 0.5f, height * 0.85f)
                val lightPoint4 = Offset(width * 0.8f, height * 1.05f)
                val lightPoint5 = Offset(width * 1.4f, height * 0.7f)
                moveTo(lightPoint1.x, lightPoint1.y)
                lineTo(width + 100f, height + 100f)
                lineTo(-100f, height + 100f)
                close()
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPath(path = mediumColoredPath, color = mediumPathColor)
                drawPath(path = lightColoredPath, color = lightPathColor)
            }
        }

        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = navDrawerContainerColor
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    navItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(text = item.title, color = if(item.route == currentRoute) navItemSelectedColor else navItemUnselectedColor ) },
                            selected = item.route == currentRoute,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (item.route != currentRoute) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (item.route == currentRoute) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = if(item.route == currentRoute) navItemSelectedColor else navItemUnselectedColor
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedTextColor = navItemUnselectedColor,
                                unselectedIconColor = navItemUnselectedColor,
                                selectedTextColor = navItemSelectedColor,
                                selectedIconColor = navItemSelectedColor,
                                selectedContainerColor = navItemSelectedContainerColor
                            )
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = navItemUnselectedColor.copy(alpha = 0.2f))
                    NavigationDrawerItem(
                        label = { Text("Sign Out", color = navItemUnselectedColor) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = navItemUnselectedColor) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = navItemUnselectedColor,
                            unselectedIconColor = navItemUnselectedColor
                        )
                    )
                }
            },
            drawerState = drawerState,
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("ISSUE SPOTTER", fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif, color = iconButtonColor) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menu Icon",
                                    tint = iconButtonColor
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { authViewModel.toggleTheme() }) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                    contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                                    modifier = Modifier.size(28.dp),
                                    tint = iconButtonColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    CategoryFilterDropdown(
                        categories = filterDropdownCategories,
                        selectedCategory = selectedFilterCategory,
                        onCategorySelected = { category ->
                            selectedFilterCategory = if (category == "ALL") null else category
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        isDarkTheme = isDarkTheme
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = textColor)
                            }
                        } else if (error != null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                            }
                        } else if (reports.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No reports yet. Be the first to report an issue!",
                                    color = semiTransparentTextColor
                                )
                            }
                        } else if (filteredReports.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No reports match the current filter.",
                                    color = semiTransparentTextColor
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredReports) { report ->
                                    ReportListItem(
                                        report = report,
                                        isDarkTheme = isDarkTheme,
                                        onItemClick = { reportId -> // Added click handler
                                            navController.navigate("issueDetail/$reportId")
                                        }
                                    )
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
                            containerColor = if (isDarkTheme) Color(0xFF7E57C2) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Report an Issue", fontSize = 16.sp, color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onPrimary)
                    }
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
            onComplete()
            return
        }
        val fetchedReports = authViewModel.supabase.postgrest["reports"].select().decodeList<Report>()
        onSuccess(fetchedReports)
    } catch (e: Exception) {
        onError("Failed to load reports: ${e.message}")
    } finally {
        onComplete()
    }
}

@Composable
fun ReportListItem(
    report: Report,
    isDarkTheme: Boolean,
    onItemClick: (reportId: String) -> Unit
) {
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF7E57C2).copy(alpha = 0.9f) else Color(0xFFB39DDB).copy(alpha = 0.9f)
    val titleColor = if (isDarkTheme) Color.White else Color.Black
    val descriptionColor = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.7f)
    val categoryColor = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.5f)
    val statusBadgeTextColor = Color.White

    val statusColor = when (report.status.lowercase()) {
        "open" -> Color(0xFF4CAF50)
        "pending" -> Color(0xFF4CAF50)
        "in progress" -> Color(0xFFFFC107)
        "resolved" -> if (isDarkTheme) Color(0xFF5E35B1) else Color(0xFF7E57C2)
        else -> Color(0xFF9E9E9E)
    }
    val displayAddress = report.description.take(50) + if (report.description.length > 50) "..." else ""
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.clickable { onItemClick(report.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.title, color = titleColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = displayAddress, color = descriptionColor, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Category: ${report.category}", color = categoryColor, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(shape = RoundedCornerShape(50), color = statusColor) {
                Text(
                    text = report.status.uppercase(),
                    color = statusBadgeTextColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val currentTextColor = if (isDarkTheme) Color.White else Color.Black
    val currentLabelColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
    val currentBorderColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val unfocusedBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.5f)
    val currentIconColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
    val containerBgColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
    val focusedContainerBgColor = if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)
    val dropdownMenuBgColor = if (isDarkTheme) Color(0xFF494E8A) else Color.White

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = currentTextColor,
        unfocusedTextColor = currentTextColor,
        cursorColor = currentTextColor,
        focusedBorderColor = currentBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        focusedLabelColor = currentLabelColor,
        unfocusedLabelColor = currentLabelColor,
        focusedTrailingIconColor = currentIconColor,
        unfocusedTrailingIconColor = currentIconColor,
        focusedContainerColor = focusedContainerBgColor,
        unfocusedContainerColor = containerBgColor
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCategory ?: "ALL CATEGORIES",
            onValueChange = {},
            readOnly = true,
            label = { Text("Filter by Category", color = currentLabelColor) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(dropdownMenuBgColor)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category, color = currentTextColor) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
