package com.example.movielover.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.movielover.repository.dataclasses.Doc
import com.example.movielover.repository.dataclasses.MovieToSearch
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class Repository {

    private val database = Firebase.database.reference
    private val moviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val myMoviesList = ArrayList<Doc>()

    private val favouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    //private val favouriteMoviesListCopy: ArrayList<Doc>()

    fun getLiveData(): MutableLiveData<ArrayList<Doc>> {
        return moviesListLiveData
    }

    //Функция отправления запроса для получения массива фильмов
    fun sendRequest(nameOfMovieToSearch: String) {
        Log.d("testLog", "request has been sent")
        val client = OkHttpClient()

        val url = HttpUrl.parse("https://api.kinopoisk.dev/v1.4/movie/search")?.newBuilder()
            ?.addQueryParameter("page", "1")
            ?.addQueryParameter("limit", "10")
            ?.addQueryParameter("query", nameOfMovieToSearch)
            ?.build()

        val request = url?.let {
            Request.Builder()
                .url(it)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
                .build()
        }

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("testLog", "fail")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body()?.string()
                val gson = Gson()
                val movieList = gson.fromJson(responseBody, MovieToSearch::class.java)
                moviesListLiveData.postValue(movieList.docs as ArrayList<Doc>?)
            }
        })
    }

    //Функция добавления фильма в мой список
    fun addToMyFavouriteList(movie: Doc) {
        myMoviesList.add(movie)
        moviesListLiveData.postValue(myMoviesList)
        Log.d("testLog", "${movie.id} --- ${movie.name}")
        Firebase.database.getReference("Users/FavouriteMovies/${movie.id}").setValue(movie.name)
    }

    /*fun downloadFavouriteMoviesList() {
        val favouriteMoviesList = ArrayList<Doc>()
        database.child("Users").get().addOnSuccessListener {
            for (favoriteMovie  in it.children) {
                val movieName = favoriteMovie.getValue(String::class.java)
                //favouriteMoviesList.add(movieName)

            }
        }
    }*/



}