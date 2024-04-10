package com.example.movielover.repository

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.example.movielover.repository.dataclasses.Doc
import com.example.movielover.repository.dataclasses.MovieToSearch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class Repository {

    private val database = Firebase.database.reference
    private val currentUser = FirebaseAuth.getInstance().uid

    private val moviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val moviesList = ArrayList<Doc>()

    private val favouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val favouriteMoviesList = ArrayList<Doc>()
    private val favouriteMoviesID = ArrayList<String>()
    private val myMoviesList = ArrayList<Doc>()

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
                .addHeader("X-API-KEY", "JHWCY0W-Z8N47AE-JK5S5TK-ZJE2W2E")
                .build()
        }

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

    //Функция добавления фильма в мой список
    /*fun addToMyFavouriteList(movie: Doc) {
        favouriteMoviesID.add(movie.id.toString())
        favouriteMoviesListLiveData.postValue(favouriteMoviesList)
        Log.d("testLog", "${movie.id} --- ${movie.name}")
        Firebase.database.getReference("Users/$currentUser/Favourite Movies/${movie.id}").setValue(movie.name)
    }*/

    fun addToMyFavouriteList(movie: Doc) {
        val movieInfo = hashMapOf(
            "id" to movie.id,
            "name" to movie.name,
            "poster" to movie.poster,
            "description" to movie.description,
            "year" to movie.year,
            "country" to movie.countries?.get(0)?.name
        )
        //database.child("Users/$currentUser/Favourite Movies/${movie.id}").setValue(movieInfo)
        database.child("Favourite Movies/${movie.id}").setValue(movieInfo)
    }

    fun downloadFavouriteMovies() {
        database.child("Favourite Movies").get().addOnSuccessListener {
        //database.child("Users/$currentUser/Favourite Movies").get().addOnSuccessListener {
            favouriteMoviesList.clear()
            if (it.exists()) {
                for (movies in it.children) {
                    Log.d("testLog", "for --- $movies")
                    val movie = movies.getValue(Doc::class.java)
                    favouriteMoviesList.add(movie!!)
                    Log.d("testLog", "movie --- $movie")
                }
            }
        }
    }

    /*fun downloadFavouriteMoviesList() {
        myMoviesList.clear()
        Log.d("testLog", "--- ${database.child("Users").child("$currentUser").child("Favourite Movies")}")
        database.child("Users").child("$currentUser").child("Favourite Movies").get().addOnSuccessListener {
            for (myFavouriteMovie in it.children) {
                val movieID = myFavouriteMovie.getValue(String::class.java)
                Log.d("testLog", "+++ ${database.child("Users").child("$currentUser").child("Favourite Movies").child("$movieID")}")
                database.child("Users").child("$currentUser").child("Favourite Movies").child("$movieID").get()
                    .addOnSuccessListener { movie ->
                        val moviePost = movie.getValue(Doc::class.java)
                        myMoviesList.add(moviePost!!)
                        favouriteMoviesListLiveData.postValue(moviesList)
                        }
                    }
            }
    }*/


    //Функция создания аккаунта
    fun createAccount(email_: EditText, password_: EditText, login_: EditText) {
        val email = email_.text.toString()
        val login = login_.text.toString()
        val password = password_.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("testLog", "successfulTask scope --- ${FirebaseAuth.getInstance().currentUser}")
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        val userInfo = hashMapOf(
                            "uid" to user.uid,
                            "email" to email,
                            "login" to login,
                            "profileImage" to ""
                        )

                        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                            .setValue(userInfo)
                            //.addOnCompleteListener { databaseTask ->
                                /*if (databaseTask.isSuccessful) {
                                    findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Ошибка при регистрации пользователя",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }*/
                    }
                } else {
                    Log.d("testLog", "else scope --- ")
                    /*Toast.makeText(
                        context,
                        "Ошибка при регистрации пользователя",
                        Toast.LENGTH_SHORT
                    ).show()*/
                }
            }
    }

}