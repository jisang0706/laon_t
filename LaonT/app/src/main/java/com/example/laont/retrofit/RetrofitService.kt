package com.example.laont.retrofit

import com.example.laont.dto.*
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

    @GET("area/{area}/{paginate}")
    fun getAreaList (
        @Path("area") area: String,
        @Path("paginate") paginate: Int,
        @Query("base") base: Int
    ) : Call<AreaListDto>

    @GET("area/{board_id}")
    fun getArea (
        @Path("board_id") board_id: Int
    ) : Call<AreaDto>

    @FormUrlEncoded
    @POST("area/{board_id}/like")
    fun likeArea (
        @Path("board_id") board_id: Int,
        @Field("google_token") google_token: String
    ) : Call<CountDto>

    @FormUrlEncoded
    @POST("area/upload")
    fun uploadArea (
        @Field("google_token") google_token: String,
        @Field("area_full_name") area_full_name: String,
        @Field("area_end_name") area_end_name: String,
        @Field("content") content: String
    ) : Call<IdDto>

    @GET("area/{board_id}/comment/{paginate}")
    fun getComment (
        @Path("board_id") board_id: Int,
        @Path("paginate") paginate: Int
    ) : Call<CommentListDto>

    @FormUrlEncoded
    @POST("area/{board_id}/comment")
    fun uploadComment (
        @Path("board_id") board_id: Int,
        @Field("google_token") google_token: String,
        @Field("group_id") group_id: Int,
        @Field("content") content: String
    ) : Call<CommentListDto>

    @DELETE("area/{board_id}")
    fun deleteBoard (
        @Path("board_id") board_id: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<ActionDto>

    @DELETE("area/{comment_id}/comment")
    fun deleteComment (
        @Path("comment_id") comment_id: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<ActionDto>
}