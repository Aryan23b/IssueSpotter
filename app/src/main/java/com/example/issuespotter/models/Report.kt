package com.example.issuespotter.models


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