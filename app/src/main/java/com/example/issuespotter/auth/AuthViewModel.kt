package com.example.issuespotter.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.issuespotter.clients.SupabaseManager
import com.example.issuespotter.models.Report
import com.example.issuespotter.screens.ReportData // Keep for submitReport if it uses a different structure
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.util.UUID

sealed class AuthState {
    data object Authenticated : AuthState()
    data object NotAuthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    val supabase = SupabaseManager.supabaseClient

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _userDisplayName = MutableStateFlow<String?>(null)
    val userDisplayName: StateFlow<String?> = _userDisplayName.asStateFlow()

    // For all reports, as in HomeScreen
    private val _reports = MutableStateFlow<List<Report>>(emptyList()) // Changed to Report type
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    // For user-specific reports
    private val _userSpecificReports = MutableStateFlow<List<Report>>(emptyList())
    val userSpecificReports: StateFlow<List<Report>> = _userSpecificReports.asStateFlow()

    private val _isLoadingUserReports = MutableStateFlow(false)
    val isLoadingUserReports: StateFlow<Boolean> = _isLoadingUserReports.asStateFlow()

    init {
        viewModelScope.launch {
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                _authState.value = AuthState.Authenticated
                fetchUserDisplayName()
            } else {
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }






    private fun fetchUserDisplayName() {
        viewModelScope.launch {
            try {
                val currentUser = supabase.auth.currentUserOrNull()
                val displayName = currentUser?.userMetadata?.get("display_name")?.jsonPrimitive?.contentOrNull
                _userDisplayName.value = displayName
            } catch (e: Exception) {
                _userDisplayName.value = null
            }
        }
    }

    fun signUp(email: String, pass: String, name: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                    data = buildJsonObject {
                        put("display_name", name)
                    }
                }
                _authState.value = AuthState.Authenticated
                fetchUserDisplayName()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Sign Up Failed: ${e.message}")
                _userDisplayName.value = null
            }
        }
    }

    fun login(email: String, pass: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }
                _authState.value = AuthState.Authenticated
                fetchUserDisplayName()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login Failed: ${e.message}")
                _userDisplayName.value = null
            }
        }
    }

    fun logout() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState.NotAuthenticated
                _userDisplayName.value = null
                _reports.value = emptyList()
                _userSpecificReports.value = emptyList()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Logout Failed: ${e.message}")
            }
        }
    }

    // General function to get all reports (used by HomeScreen)
    fun getAllReports() {
        viewModelScope.launch {
            try {
                val fetchedReports = supabase.from("reports").select().decodeList<Report>()
                _reports.value = fetchedReports
            } catch (e: Exception) {
                _reports.value = emptyList() // Handle error
            }
        }
    }

    // Function to get reports for the current user
    fun fetchUserSpecificReports() {
        viewModelScope.launch {
            val currentUser = supabase.auth.currentUserOrNull()
            if (currentUser == null) {
                _userSpecificReports.value = emptyList()
                // Optionally, post an error or a specific state for not being authenticated
                return@launch
            }
            _isLoadingUserReports.value = true
            try {
                val userId = currentUser.id
                val fetchedReports = supabase.from("reports")
                    .select { filter {
                        eq("user_id", userId)
                    } }
                    .decodeList<Report>()
                _userSpecificReports.value = fetchedReports
            } catch (e: Exception) {
                _userSpecificReports.value = emptyList() // Handle error
                // Optionally, log error or post to an error StateFlow
            } finally {
                _isLoadingUserReports.value = false
            }
        }
    }

    suspend fun uploadImageToSupabase(imageUri: Uri, userId: String, context: Context): String {
        return try {
            val fileBytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
            val fileName = "reports/${userId}/${UUID.randomUUID()}.jpg"

            if (fileBytes != null) {
                supabase.storage.from("reports").upload(path = fileName, data = fileBytes) {
                    upsert = false
                }
                supabase.storage.from("reports").publicUrl(fileName)
            } else {
                throw Exception("Failed to read image file.")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    // Assuming ReportData might be a different structure for submission, if not, change to Report
    suspend fun submitReport(report: ReportData) {
        try {
            supabase.from("reports").insert(report)
        } catch (e: Exception) {
            throw e
        }
    }
}
