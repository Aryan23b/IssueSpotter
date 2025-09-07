package com.example.issuespotter.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button // Keep this for Sign Out
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton // Added for My Reports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.issuespotter.auth.AuthViewModel

fun Path.standardQuadFromTo(from: Offset, to: Offset) {
    quadraticBezierTo(
        from.x,
        from.y,
        (from.x + to.x) / 2f,
        (from.y + to.y) / 2f
    )
}

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val userName by authViewModel.userDisplayName.collectAsState()
    val userReports by authViewModel.userSpecificReports.collectAsState()


    Box(modifier = Modifier.fillMaxSize()
        .background(Color(0xFF06154C))
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName ?: "User",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp)) // Added space before new button


            UserRatingSection(reportCount = userReports.size)


//             Button to navigate to UserReportsScreen
//            TextButton(onClick = { navController.navigate("userReports") }) {
//                Text("My Reports", color = Color.White, fontSize = 18.sp)
//            }

            Spacer(modifier=Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate("userReports") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = "My Reports"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("My Reports", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.padding(),
                onClick = { 
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            ) {
                Text("Sign Out")
            }
        }
    }
}

/* // Preview will need a mock AuthViewModel or to be updated
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    // val sampleUser = user(name = "Alex Doe") // This is no longer valid
    // ProfileScreen(user = sampleUser) // This is no longer valid
}
*/


@Composable
private fun UserRatingSection(reportCount: Int) {
    val rating = when {
        reportCount > 25 -> 5
        reportCount > 20 -> 4
        reportCount > 15 -> 3
        reportCount > 5  -> 2
        reportCount >= 1 -> 1
        else             -> 0 // This will result in 5 empty stars
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contributor Rating",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            // Loop to display 5 stars, filling them based on the calculated rating
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = if (i <= rating) "Filled Star" else "Empty Star",
                    tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Based on $reportCount reported issues.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}