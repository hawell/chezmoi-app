package org.chordsoft.chezmoi.data.model

import com.google.gson.annotations.SerializedName

sealed class HlmData {
    abstract val centerLatitude: Double
    abstract val centerLongitude: Double
    abstract fun id(): String
    abstract fun label(): String
    data class HlmTele(
        @SerializedName("lat")
        val latitude: Double?,
        @SerializedName("lng")
        val longitude: Double?,
        @SerializedName("center_lat")
        override val centerLatitude: Double,
        @SerializedName("center_lng")
        override val centerLongitude: Double,
        @SerializedName("number")
        val number: String,
        @SerializedName("address")
        val address: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("postal_code")
        val postalCode: String,
        @SerializedName("building_type")
        val buildingType: String,
        @SerializedName("construction_year")
        val constructionYear: String?,
        @SerializedName("first_rent_year")
        val firstRentYear: String?,
        @SerializedName("hlm_entery_year")
        val hlmEntryYear: String?,
        @SerializedName("num_plai")
        val numPlai: Int,
        @SerializedName("num_plus")
        val numPlus: Int,
        @SerializedName("num_pls")
        val numPls: Int,
        @SerializedName("num_pli")
        val numPli: Int,
        @SerializedName("num_unknown")
        val numUnknown: Int
    ): HlmData() {
        override fun id(): String {
            return "tele-${centerLatitude}-${centerLongitude}-${address}-${number}"
        }

        override fun label(): String {
            return address
        }
    }

    data class HlmWide(
        @SerializedName("center_lat")
        override val centerLatitude: Double,
        @SerializedName("center_lng")
        override val centerLongitude: Double,
        @SerializedName("count")
        val count: Int
    ): HlmData() {
        override fun id(): String {
            return "wide-${centerLatitude}-${centerLongitude}"
        }

        override fun label(): String {
            return count.toString()
        }
    }

    data class HlmCity(
        @SerializedName("lat")
        override val centerLatitude: Double,
        @SerializedName("lng")
        override val centerLongitude: Double,
        @SerializedName("count")
        val count: Int,
        @SerializedName("postal_code")
        val postalCode: String,
        @SerializedName("city")
        val city: String
    ): HlmData() {
        override fun id(): String {
            return "wide-${city}-${centerLatitude}-${centerLongitude}"
        }

        override fun label(): String {
            return "$city: $count"
        }
    }
}