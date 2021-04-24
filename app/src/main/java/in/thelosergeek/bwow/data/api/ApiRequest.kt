package `in`.thelosergeek.bwow.data.api

import retrofit2.Call
import retrofit2.http.GET

const val BASE_URL = "https://api.jsonbin.io"


interface ApiRequest {

    @GET("/b/60816ce39a9aa933335504a8")
    fun getDetails(): Call<Response>
}
