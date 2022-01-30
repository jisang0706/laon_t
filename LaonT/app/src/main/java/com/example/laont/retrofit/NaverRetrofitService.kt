package com.example.laont.retrofit

import com.example.laont.dto.ReverseGeocodingDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverRetrofitService {

    @GET("map-reversegeocode/v2/gc")
    fun reverseGeocoding (
        @Query("coords") coords: String,
        @Query("output") output: String,
        @Query("orders") orders: String,
        @Query("X-NCP-APIGW-API-KEY-ID") key_id: String,
        @Query("X-NCP-APIGW-API-KEY") key: String
    ) : Call<ReverseGeocodingDto>
}