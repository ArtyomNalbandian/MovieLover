package com.example.movielover.repository.dataclasses

data class Doc(
    val ageRating: Int?=null,
    val alternativeName: String?=null,
    val backdrop: Backdrop?=null,
    val countries: List<Country>?=null,
    val description: String?=null,
    val enName: String?=null,
    val externalId: ExternalId?=null,
    val genres: List<Genre>?=null,
    val id: Int?=null,
    val isSeries: Boolean?=null,
    val logo: Logo?=null,
    val movieLength: Int?=null,
    val name: String?=null,
    val names: List<Name>?=null,
    val poster: Poster?=null,
    val rating: Rating?=null,
    val ratingMpaa: String?=null,
    val releaseYears: List<ReleaseYear>?=null,
    val seriesLength: Int?=null,
    val shortDescription: String?=null,
    val status: String?=null,
    val ticketsOnSale: Boolean?=null,
    val top10: Any?=null,
    val top250: Any?=null,
    val totalSeriesLength: Any?=null,
    val type: String?=null,
    val typeNumber: Int?=null,
    val votes: Votes?=null,
    val year: Int?=null
)