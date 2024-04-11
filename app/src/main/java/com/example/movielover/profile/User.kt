package com.example.movielover.profile

import com.example.movielover.repository.dataclasses.Doc

data class User(
    var login: String? = null,
    var email: String? = null,
    var password: String? = null,
    var profileImage: String? = null,
    var uid: String? = null,
    var favouriteMovies: ArrayList<Doc>? = null,
    var subscriptions: ArrayList<User>? = null
    )
