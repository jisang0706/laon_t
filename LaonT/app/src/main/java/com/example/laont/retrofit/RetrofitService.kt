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

    @FormUrlEncoded
    @POST("user/nickname")
    fun setNickname (
        @Header("GOOGLETOKEN") google_token: String,
        @Field("nickname") nickname: String
    ) : Call<ActionDto>

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
    ) : Call<BoardListDto>

    @GET("area/{area}/{paginate}")
    fun getAreaSearch (
        @Path("area") area: String,
        @Path("paginate") paginate: Int,
        @Query("search") search: String
    ) : Call<BoardListDto>

    @GET("area/{board_id}")
    fun getArea (
        @Path("board_id") board_id: Int
    ) : Call<BoardDto>

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
    fun getAreaComment (
        @Path("board_id") board_id: Int,
        @Path("paginate") paginate: Int
    ) : Call<CommentListDto>

    @FormUrlEncoded
    @POST("area/{board_id}/comment")
    fun uploadAreaComment (
        @Path("board_id") board_id: Int,
        @Field("google_token") google_token: String,
        @Field("group_id") group_id: Int,
        @Field("content") content: String
    ) : Call<CommentListDto>

    @DELETE("area/{board_id}")
    fun deleteArea (
        @Path("board_id") board_id: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<ActionDto>

    @DELETE("area/{comment_id}/comment")
    fun deleteAreaComment (
        @Path("comment_id") comment_id: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<ActionDto>

    @GET("playground/{pg_name}/{paginate}")
    fun getPGList (
        @Path("pg_name") pg_name: String,
        @Path("paginate") paginate: Int,
        @Query("base") base: Int
    ) : Call<BoardListDto>

    @GET("playground/{pg_name}/{paginate}")
    fun getPGSearch (
        @Path("pg_name") pg_name: String,
        @Path("paginate") paginate: Int,
        @Query("search") search: String
    ) : Call<BoardListDto>

    @GET("playground/{board_id}")
    fun getPGDetail (
        @Path("board_id") board_id: Int
    ) : Call<BoardDto>

    @FormUrlEncoded
    @POST("playground/upload")
    fun uploadPG (
        @Field("google_token") google_token: String,
        @Field("playground_name") playground_name: String,
        @Field("content") content: String
    ) : Call<IdDto>

    @DELETE("playground/{board_id}")
    fun deletePG (
        @Path("board_id") board_id: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<ActionDto>

    @FormUrlEncoded
    @POST("playground/{board_id}/like")
    fun likePG (
        @Path("board_id") board_id: Int,
        @Field("google_token") google_token: String
    ) : Call<CountDto>

    @GET("playground/{board_id}/comment/{paginate}")
    fun getPGComment (
        @Path("board_id") board_id: Int,
        @Path("paginate") paginate: Int
    ) : Call<CommentListDto>

    @FormUrlEncoded
    @POST("playground/{board_id}/comment")
    fun uploadPGComment (
        @Path("board_id") board_id: Int,
        @Field("google_token") google_token: String,
        @Field("group_id") group_id: Int,
        @Field("content") content: String
    ) : Call<CommentListDto>

    @DELETE("playground/{comment_id}/comment")
    fun deletePGComment (
        @Path("comment_id") comment_id: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<ActionDto>

    @GET("user/area/{paginate}")
    fun getWritedAreaList (
        @Path("paginate") paginate: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<BoardListDto>

    @GET("user/area/comment/{paginate}")
    fun getWritedAreaCommentList (
        @Path("paginate") paginate: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<BoardListDto>

    @GET("user/playground/{paginate}")
    fun getWritedPGList (
        @Path("paginate") paginate: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<BoardListDto>

    @GET("user/playground/comment/{paginate}")
    fun getWritedPGCommentList (
        @Path("paginate") paginate: Int,
        @Header("GOOGLETOKEN") google_token: String
    ) : Call<BoardListDto>

    @GET("area/{board_id}/name")
    fun getAreaBoardName (
        @Path("board_id") board_id: Int
    ) : Call<ActionDto>

    @GET("playground/{board_id}/name")
    fun getPGBoardName (
        @Path("board_id") board_id: Int
    ) : Call<ActionDto>
}