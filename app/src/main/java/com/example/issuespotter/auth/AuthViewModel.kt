package com.example.issuespotter.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.issuespotter.clients.SupabaseManager
import com.example.issuespotter.screens.Report
import com.example.issuespotter.screens.ReportData
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID

sealed class AuthState {
    data object Authenticated : AuthState()
    data object NotAuthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed interface ReportDetailState {
    data object Idle : ReportDetailState
    data object Loading : ReportDetailState
    data class Success(val report: Report) : ReportDetailState
    data class Error(val message: String) : ReportDetailState
}

class AuthViewModel : ViewModel() {

    val supabase = SupabaseManager.supabaseClient


    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()


    private val _userDisplayName = MutableStateFlow<String?>(null)
    val userDisplayName: StateFlow<String?> = _userDisplayName.asStateFlow()

private val _reports = MutableStateFlow<List<Report>>(emptyList()) // Changed to Report type
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _reportDetailState = MutableStateFlow<ReportDetailState>(ReportDetailState.Idle)
    val reportDetailState: StateFlow<ReportDetailState> = _reportDetailState.asStateFlow()

    // Track user upvoted reports
    private val _userUpvotedReports = MutableStateFlow<Set<String>>(emptySet())
    val userUpvotedReports: StateFlow<Set<String>> = _userUpvotedReports.asStateFlow()

    // Track upvoting state (reportId to loading state)
    private val _isUpvoting = MutableStateFlow<Pair<String, Boolean>?>(null)
    val isUpvoting: StateFlow<Pair<String, Boolean>?> = _isUpvoting.asStateFlow()

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

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun signUp(email: String, pass: String,name : String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                    this.data = buildJsonObject {
                        put("display_name", name)
                    }
                }
                _authState.value = AuthState.Authenticated
                loadUserUpvotedReports()
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
                loadUserUpvotedReports()
                fetchUserDisplayName()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login Failed: ${e.message}")
                _userDisplayName.value = null
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState.NotAuthenticated
                _userUpvotedReports.value = emptySet()
                _userDisplayName.value = null
                _reports.value = emptyList()
                _userSpecificReports.value = emptyList()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Logout Failed: ${e.message}")
            }
        }
    }

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
        return try{
        val fileBytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
            ?: throw Exception("Failed to read image file.")
        val fileName = "reports/${userId}/${UUID.randomUUID()}.jpg"


        if (fileBytes != null) {
            supabase.storage.from("reports").upload(path = fileName, data = fileBytes) {
                upsert = false
            }
            supabase.storage.from("reports").publicUrl(fileName)
        }else{
            throw Exception("Failed to read image file.")
        }
    } catch (e: Exception) {
        throw e
        }
    }

    suspend fun submitReport(report: ReportData) {
        try {
            supabase.from("reports").insert(report)
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error submitting report: ${e.message}", e)
            throw e
        }
    }

    fun fetchReportById(reportId: String) {
        viewModelScope.launch {
            _reportDetailState.value = ReportDetailState.Loading
            try {
                val result = supabase.from("reports")
                    .select {
                        filter {
                            eq("id", reportId)
                        }
                    }
                    .decodeSingle<Report>()

                _reportDetailState.value = ReportDetailState.Success(result)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to fetch report by ID $reportId: ${e.message}", e)
                _reportDetailState.value = ReportDetailState.Error("Failed to fetch report: ${e.message}")
            }
        }
    }

    fun upvoteReport(reportId: String) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                _reportDetailState.value = ReportDetailState.Error("Please login to upvote.")
                return@launch
            }

            if (hasUserUpvoted(reportId)) {
                _reportDetailState.value = ReportDetailState.Error("You've already upvoted this report.")
                return@launch
            }

            _isUpvoting.value = Pair(reportId, true)

            try {

                supabase.from("report_upvotes").insert(
                    mapOf("report_id" to reportId, "user_id" to userId)
                )

                val currentReport = supabase.from("reports")
                    .select {
                        filter {
                            eq("id", reportId)
                        }
                    }
                    .decodeSingle<Report>()

                val currentCount = currentReport.upvote_count ?: 0
                val newCount = currentCount + 1

                supabase.from("reports").update(
                    mapOf("upvote_count" to newCount)
                ) {
                    filter {
                        eq("id", reportId)
                    }
                }


                _userUpvotedReports.value = _userUpvotedReports.value + reportId
                _isUpvoting.value = Pair(reportId, false)


                val currentState = _reportDetailState.value
                if (currentState is ReportDetailState.Success) {
                    val updatedReport = currentState.report.copy(
                        upvote_count = newCount
                    )
                    _reportDetailState.value = ReportDetailState.Success(updatedReport)
                }

            } catch (e: RestException) {
                if (e.message?.contains("23505") == true || e.message?.contains("duplicate") == true) {
                    _userUpvotedReports.value = _userUpvotedReports.value + reportId
                    _isUpvoting.value = Pair(reportId, false)
                    _reportDetailState.value = ReportDetailState.Error("You've already upvoted this report.")
                } else {
                    Log.e("AuthViewModel", "RestException during upvote: ${e.message}", e)
                    _isUpvoting.value = Pair(reportId, false)
                    _reportDetailState.value = ReportDetailState.Error("Upvote failed: Database error.")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error during upvote: ${e.message}", e)
                _isUpvoting.value = Pair(reportId, false)
                _reportDetailState.value = ReportDetailState.Error("Upvote failed: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun hasUserUpvoted(reportId: String): Boolean {
        return _userUpvotedReports.value.contains(reportId)
    }

    fun loadUserUpvotedReports() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch

            try {
                val upvotedReports = supabase.from("report_upvotes")
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<Map<String, Any>>()

                val reportIds = upvotedReports.mapNotNull { it["report_id"]?.toString() }.toSet()
                _userUpvotedReports.value = reportIds

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error loading user upvoted reports: ${e.message}", e)

            }
        }
    }


    fun clearReportDetailState() {
        _reportDetailState.value = ReportDetailState.Idle
    }

}