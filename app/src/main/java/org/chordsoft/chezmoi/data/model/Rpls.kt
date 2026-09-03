package org.chordsoft.chezmoi.data.model

import com.google.gson.annotations.SerializedName

data class Rpls(
    @SerializedName("id")
    val id: Int,
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lng")
    val longitude: Double,
    @SerializedName("number")
    val number: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("postal_code")
    val postalCode: String,
    @SerializedName("num_plai")
    val numPlai: Int,
    @SerializedName("num_plus")
    val numPlus: Int,
    @SerializedName("num_pls")
    val numPls: Int,
    @SerializedName("num_pli")
    val numPli: Int,
    @SerializedName("num_unknown")
    val numUnknown: Int,
    @SerializedName("construction_year")
    val constructionYear: String?,
)