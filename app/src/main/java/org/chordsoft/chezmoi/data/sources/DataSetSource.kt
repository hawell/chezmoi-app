package org.chordsoft.chezmoi.data.sources

import kotlinx.coroutines.flow.Flow

interface DataSetSource<T> {
    val flow: Flow<T>
}

interface MutableDataSetSource<T>: DataSetSource<T> {
    suspend fun set(newValue: T)
}