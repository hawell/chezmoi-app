package org.chordsoft.chezmoi.data.api

import org.chordsoft.chezmoi.data.model.Address
import org.chordsoft.chezmoi.data.model.Cluster
import org.chordsoft.chezmoi.data.model.HlmData
import org.chordsoft.chezmoi.data.model.Rpls
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("hlm")
    suspend fun getHlmTele(
        @Query("south")
        south: Double,
        @Query("west")
        west: Double,
        @Query("north")
        north: Double,
        @Query("east")
        east: Double,
        @Query("zoom")
        zoom: Float
    ): ApiResponse<List<HlmData.HlmTele>>

    @GET("hlm")
    suspend fun getHlmWide(
        @Query("south")
        south: Double,
        @Query("west")
        west: Double,
        @Query("north")
        north: Double,
        @Query("east")
        east: Double,
        @Query("zoom")
        zoom: Float
    ): ApiResponse<List<HlmData.HlmWide>>

    @GET("hlm")
    suspend fun getHlmCity(
        @Query("south")
        south: Double,
        @Query("west")
        west: Double,
        @Query("north")
        north: Double,
        @Query("east")
        east: Double,
        @Query("zoom")
        zoom: Float
    ): ApiResponse<List<HlmData.HlmCity>>

    @GET("rpls")
    suspend fun getRpls(
        @Query("south")
        south: Double,
        @Query("west")
        west: Double,
        @Query("north")
        north: Double,
        @Query("east")
        east: Double,
        @Query("zoom")
        zoom: Float
    ): ApiResponse<List<Cluster>>

    @GET("rpls_details")
    suspend fun getRplsDetails(
        @Query("id")
        id: Int
    ): ApiResponse<Rpls>

    @GET("search_address")
    suspend fun searchAddress(
        @Query("q")
        query: String
    ): ApiResponse<List<Address>>
}
