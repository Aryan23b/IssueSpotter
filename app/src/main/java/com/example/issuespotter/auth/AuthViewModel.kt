package com.example.issuespotter.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.issuespotter.clients.SupabaseManager
import com.example.issuespotter.screens.ReportData
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val _reports = MutableStateFlow<List<ReportData>>(emptyList())
    val reports: StateFlow<List<ReportData>> = _reports.asStateFlow()

    init {
        viewModelScope.launch {
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }

    fun signUp(email: String, pass: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                }
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Sign Up Failed: ${e.message}")
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
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login Failed: ${e.message}")
            }
        }
    }

    fun logout() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState.NotAuthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Logout Failed: ${e.message}")
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


    suspend fun submitReport(report: ReportData) {
        try {
            supabase.from("reports").insert(report)
        } catch (e: Exception) {
            throw e
        }
    }


    fun getReports() {
        viewModelScope.launch {
            try {
                val fetchedReports = supabase.from("reports")
                    .select()
                    .decodeList<ReportData>()
                _reports.value = fetchedReports
            } catch (e: Exception) {
                // Handle error
                _reports.value = emptyList()
                // Log the error or show a Toast
            }
        }
    }
}

