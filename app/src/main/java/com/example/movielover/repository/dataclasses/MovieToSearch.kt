package com.example.movielover.repository.dataclasses

data class MovieToSearch(
    val docs: List<Doc>?=null,
    val limit: Int?=null,
    val page: Int?=null,
    val pages: Int?=null,
    val total: Int?=null
)