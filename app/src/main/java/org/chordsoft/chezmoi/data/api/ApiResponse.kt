package org.chordsoft.chezmoi.data.api

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)
