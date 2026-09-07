package org.chordsoft.chezmoi.data.model

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("number")
    val number: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("postal_code")
    val postalCode: String,
    @SerializedName("score")
    val score: Double,
)
