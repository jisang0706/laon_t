package com.example.laont.dto

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

data class ActionDto (
    val action: String
        )

data class UserInfoDto (
    val action: String,
    val nickname: String
        )

data class ReverseGeocodingDto (
    val status: RGStatusDto,
    val results: List<RGResultDto>
        )

data class RGResultDto (
    val region: RGRegionDto
        )

data class RGRegionDto (
    val area0: RGAreaDto,
    val area1: RGAreaDto,
    val area2: RGAreaDto,
    val area3: RGAreaDto,
    val area4: RGAreaDto
        )

data class RGStatusDto (
    val code: Int,
    val name: String,
    val message: String
        )

data class RGAreaDto (
    val name: String,
    val coords: RGCoordsDto
        )

data class RGCoordsDto (
    val center: RGCenterDto
        )

data class RGCenterDto (
    val x: Double,
    val y: Double
        )

data class GeoCodingDto (
    val addresses: List<GCCoordsDto>
        )

data class GCCoordsDto (
    val y: Double,
    val x: Double
        )

data class NotiListDto (
    val list: List<NotiDetailDto>
        )

data class NotiDetailDto (
    val id: Int,
    val title: String,
    var created_at: String,
    val content: String,
    val images: List<String>
        )

@Xml(name="response")
data class PGResponseDto (
    @Element(name="header")
    val header: PGHeaderDto,
    @Element(name="body")
    val body: PGBodyDto
        )

@Xml(name="header")
data class PGHeaderDto (
    @PropertyElement(name="resultCode")
    val resultCode: String,
    @PropertyElement(name="resultMsg")
    val resultMsg: String
        )

@Xml(name="body")
data class PGBodyDto (
    @Element(name="items")
    val items: PGItemsDto
        )

@Xml(name="items")
data class PGItemsDto (
    @Element(name="item")
    val itemlist: List<PGItemDto>
        )

@Xml(name="item")
data class PGItemDto (
    @PropertyElement(name="ciCode1")
    var id: String?,
    @PropertyElement(name="ciRaddr1")
    var roadAddress: String?,
    @PropertyElement(name="ciNaddr1")
    var groundAddress1: String?,
    @PropertyElement(name="ciNaddr2")
    var groundAddress2: String?,
    @PropertyElement(name="ciName")
    var name: String?,
        )

data class BoardListDto (
    val list: List<BoardDto>
        )

data class BoardDto (
    val id: Int,
    val content: String,
    val created_at: String,
    val like: Int,
    val comment: Int,
    val writer_nickname: String
        )

data class CountDto (
    val count: Int
        )

data class IdDto (
    val id: Int
        )

data class CommentListDto (
    val list: List<CommentDto>
        )

data class CommentDto (
    val id: Int,
    val content: String,
    val created_at: String,
    val writer_nickname: String,
    var reply: MutableList<ReplyDto>
        ) {
}

data class ReplyDto (
    val id: Int,
    val content: String,
    val created_at: String,
    val writer_nickname: String
        )

data class Playground (
    var id: String,
    var address: String,
    var name: String,
    var latitude: Double,
    var longitude: Double,
        )