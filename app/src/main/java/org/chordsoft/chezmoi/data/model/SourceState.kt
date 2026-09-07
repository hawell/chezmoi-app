package org.chordsoft.chezmoi.data.model

sealed class SourceState<out T> {
    data object Empty: SourceState<Nothing>()
    data object Loading: SourceState<Nothing>()
    data class Error(val message: String): SourceState<Nothing>()
    data class Success<T>(val data: T): SourceState<T>()
}

fun SourceState<*>.typeName(): String = when (this) {
    SourceState.Empty -> "Empty"
    SourceState.Loading -> "Loading"
    is SourceState.Error -> "Error"
    is SourceState.Success -> "Success"
}