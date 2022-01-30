package com.example.laont.dto

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
    val area0: AreaDto,
    val area1: AreaDto,
    val area2: AreaDto,
    val area3: AreaDto,
    val area4: AreaDto
        )

data class RGStatusDto (
    val code: Int,
    val name: String,
    val message: String
        )

data class AreaDto (
    val name: String,
    val coords: CoordsDto
        )

data class CoordsDto (
    val center: CenterDto
        )

data class CenterDto (
    val x: Double,
    val y: Double
        )

