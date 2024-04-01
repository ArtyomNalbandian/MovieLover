package com.example.movielover.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.movielover.repository.dataclasses.Doc
import com.example.movielover.repository.dataclasses.MovieToSearch
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class Repository {

    private val moviesListLiveData : MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }

    fun getLiveData(): MutableLiveData<ArrayList<Doc>> {
        return  moviesListLiveData
    }

    fun sendRequest(nameOfMovieToSearch: String) {
        Log.d("testLog", "request has been sent")
        val client = OkHttpClient()

        val url = HttpUrl.parse("https://api.kinopoisk.dev/v1.4/movie/search")?.newBuilder()
            ?.addQueryParameter("page", "1")
            ?.addQueryParameter("limit", "10")
            ?.addQueryParameter("query", nameOfMovieToSearch)
            ?.build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
            .build()

        if (request != null) {
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
    }
}