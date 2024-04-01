package com.example.movielover.repository.dataclasses

data class MovieToSearch(
    val docs: List<Doc>,
    val limit: Int,
    val page: Int,
    val pages: Int,
    val total: Int
)