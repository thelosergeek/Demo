package `in`.thelosergeek.bwow.data.model

import com.google.gson.annotations.SerializedName

data class Data (
    @SerializedName("heart-rate") val heartRate: String? = null,
    @SerializedName("sleep-time") val sleepTime: String?= null,
    @SerializedName("training-time") val trainingTime: String?= null
)
