package com.example.eventconnect.data.network


data class NGODocumentResponse(
    val id: Int,
    val document_type: String,
    val file_url: String,
    val uploaded_at: String
)
