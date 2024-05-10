package com.example.movielover.model.dataclasses

data class MovieToSearch(
    val docs: List<Doc>?=null,
    val limit: Int?=null,
    val page: Int?=null,
    val pages: Int?=null,
    val total: Int?=null
)