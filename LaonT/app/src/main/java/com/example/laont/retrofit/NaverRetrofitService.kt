package com.example.laont.retrofit

import com.example.laont.dto.GeoCodingDto
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
        @Header("X-NCP-APIGW-API-KEY-ID") key_id: String,
        @Header("X-NCP-APIGW-API-KEY") key: String
    ) : Call<ReverseGeocodingDto>

    @GET("map-geocode/v2/geocode")
    fun geocoding (
        @Query("query") address: String,
        @Header("X-NCP-APIGW-API-KEY-ID") key_id: String,
        @Header("X-NCP-APIGW-API-KEY") key: String
    ) : Call<GeoCodingDto>
}