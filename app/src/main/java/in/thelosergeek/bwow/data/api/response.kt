package `in`.thelosergeek.bwow.data.api

import `in`.thelosergeek.bwow.data.model.Data

data class Response(
    val code: Int,
    val `data`: Data,
    val success: String
)