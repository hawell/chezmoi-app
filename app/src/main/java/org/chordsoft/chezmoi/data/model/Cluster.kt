package org.chordsoft.chezmoi.data.model

import com.google.gson.annotations.SerializedName

data class Cluster(
    @SerializedName("id")
    val id: Int,
    @SerializedName("center_lat")
    val centerLatitude: Double,
    @SerializedName("center_lng")
    val centerLongitude: Double,
    @SerializedName("count")
    val count: Int,
) {
    fun id(): String {
        return "rpls-cluster-$id"
    }

    fun label(): String {
        return count.toString()
    }
}
