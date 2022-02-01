package com.example.laont.retrofit

import com.example.laont.dto.PGResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenAPIRetrofitService {

    @GET("openapi/service/rest/ChildPlyFcltInfoService/getFcltInfo")
    fun getPlayGround (
        @Query("serviceKey", encoded = true) serviceKey: String,
        @Query("areaNm", encoded = true) areaNm: String,
        @Query("numOfRows", encoded = true) numOfRows: Int
    ) : Call<PGResponseDto>
}