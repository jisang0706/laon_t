package com.example.laont.retrofit

import com.example.laont.dto.ActionDto
import com.example.laont.dto.NotiDetailDto
import com.example.laont.dto.NotiListDto
import com.example.laont.dto.UserInfoDto
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("user/join")
    fun userJoin (
        @Field("google_token") google_token: String,
        @Field("nickname") nickname: String,
        @Field("email") email: String
    ) : Call<ActionDto>

    @FormUrlEncoded
    @POST("user/login")
    fun userLogin (
        @Field("google_token") google_token: String
    ) : Call<UserInfoDto>

    @GET("noti/list/{paginate}")
    fun getNotiList (
        @Path("paginate") paginate: Int,
        @Query("base") base: Int,
    ) : Call<NotiListDto>

    @GET("noti/{id}")
    fun getNotiDetail (
        @Path("id") id: Int,
    ) : Call<NotiDetailDto>
}