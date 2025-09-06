package com.example.issuespotter.screens

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.issuespotter.auth.AuthViewModel
import com.google.android.gms.location.LocationServices
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class ReportData(
    val title: String,
    val description: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val image_url: String? = null,
    val user_id: String,
    val status: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportNewIssueScreen(navController: NavController, authViewModel: AuthViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                locationError = null
                // Try to get the last known location.
                try {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                latitude = location.latitude
                                longitude = location.longitude
                                locationError = null
                            } else {
                                locationError = "Could not get location. Ensure GPS is enabled."
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Could not get location. Ensure GPS is enabled.")
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            locationError = "Error getting location: ${e.message}"
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Error getting location: ${e.message}")
                            }
                        }
                } catch (e: SecurityException) {
                    locationError = "Location permission not granted."
                     coroutineScope.launch {
                        snackbarHostState.showSnackbar("Location permission not granted.")
                    }
                }
            } else {
                locationError = "Location permission denied."
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Location permission is required to get current location.")
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF06154C))
    ) {
        // Decorative background canvas
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
                standardQuadFromTo(mediumColoredPoint1, mediumColoredPoint2)
                standardQuadFromTo(mediumColoredPoint2, mediumColoredPoint3)
                standardQuadFromTo(mediumColoredPoint3, mediumColoredPoint4)
                standardQuadFromTo(mediumColoredPoint4, mediumColoredPoint5)
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
                standardQuadFromTo(lightPoint1, lightPoint2)
                standardQuadFromTo(lightPoint2, lightPoint3)
                standardQuadFromTo(lightPoint3, lightPoint4)
                standardQuadFromTo(lightPoint4, lightPoint5)
                lineTo(width + 100f, height + 100f)
                lineTo(-100f, height + 100f)
                close()
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPath(path = mediumColoredPath, color = Color(0xFF494E8A))
                drawPath(path = lightColoredPath, color = Color(0xFFCACFFF))
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Report a New Issue", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g., Pothole on Main Street") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Provide details about the issue...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))
                CategoryDropdown(
                    categories = listOf("ROADS", "LIGHTING", "WATER SUPPLY", "CLEANLINESS", "PUBLIC SAFETY", "OBSTRUCTION"),
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Add Image from Device", color = Color.White)
                }
                if (imageUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Image selected: ${imageUri?.lastPathSegment}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                 Button(
                    onClick = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Get Current Location", color = Color.White)
                }

                if (latitude != null && longitude != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Location: Lat: $latitude, Lon: $longitude",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                } else if (locationError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = locationError!!,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }


                Spacer(modifier = Modifier.weight(1f)) 
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (title.isBlank() || description.isBlank() || selectedCategory == null) {
                                snackbarHostState.showSnackbar("Please fill in title, description, and category.")
                                return@launch
                            }
                            if (latitude == null || longitude == null) {
                                snackbarHostState.showSnackbar("Please fetch the current location.")
                                return@launch
                            }
                            val userId = authViewModel.supabase.auth.currentUserOrNull()?.id
                            if (userId == null) {
                                snackbarHostState.showSnackbar("User not authenticated.")
                                return@launch
                            }
                            try {
                                val imageUrl = imageUri?.let {
                                    authViewModel.uploadImageToSupabase(it, userId, context)
                                }
                                val reportData = ReportData(
                                    title = title,
                                    description = description,
                                    category = selectedCategory!!,
                                    latitude = latitude!!,
                                    longitude = longitude!!,
                                    image_url = imageUrl,
                                    user_id = userId,
                                    status = "pending" // Explicitly set for new reports
                                )
                                authViewModel.submitReport(reportData)
                                snackbarHostState.showSnackbar("Report submitted successfully!")
                                navController.popBackStack()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error submitting report: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2))
                ) {
                    Text("Submit Report", fontSize = 16.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.LightGray,
        focusedTrailingIconColor = Color.White,
        unfocusedTrailingIconColor = Color.LightGray
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory ?: "Select Category",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = textFieldColors
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF494E8A))
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category, color = Color.White) },
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
