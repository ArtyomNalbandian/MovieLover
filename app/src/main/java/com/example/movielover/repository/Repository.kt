package com.example.movielover.repository

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.example.movielover.profile.User
import com.example.movielover.repository.dataclasses.Doc
import com.example.movielover.repository.dataclasses.MovieToSearch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    //Массив фильмов который получаем при поиске
    private val searchMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val searchMoviesList = ArrayList<Doc>()

    //Получение всех пользователей кроме меня с Firebase
    private val allUsersList = ArrayList<User>()
    private val allUsersListLiveData: MutableLiveData<ArrayList<User>> by lazy { MutableLiveData<ArrayList<User>>() }

    //private val favouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val favouriteMoviesList = ArrayList<Doc>()

    //Для получения списка фильмов определенного жанра
    private var criminalMoviesByGenre = ArrayList<Doc>()
    private var thrillerMoviesByGenre = ArrayList<Doc>()
    private var actionMoviesByGenre = ArrayList<Doc>()
    private val criminalMoviesByGenreLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val thrillerMoviesByGenreLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val actionMoviesByGenreLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }


    fun getSearchMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return searchMoviesListLiveData
    }

    fun getSearchMoviesList(): ArrayList<Doc> {
        return searchMoviesList
    }

    //Функция добавления фильма в мой список
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

    fun getFavouriteMoviesList(): ArrayList<Doc> {
        return favouriteMoviesList
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
                //.addHeader("X-API-KEY", "JHWCY0W-Z8N47AE-JK5S5TK-ZJE2W2E")
                .addHeader("X-API-KEY", "9WBCW0P-4284VRY-GQMAP77-H0PJGFM")
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
                    searchMoviesListLiveData.postValue(movieList.docs as ArrayList<Doc>?)
                    Log.d("testLog", "movies --- ${movieList.docs}")
                }
            })
        }
    }

    //Функция получения списка фильмов определенного жанра
    fun getMoviesByGenre(genre: String) {
        val client = OkHttpClient()

        val url = HttpUrl.parse("https://api.kinopoisk.dev/v1.4/movie")?.newBuilder()
            ?.addQueryParameter("page", "1")
            ?.addQueryParameter("limit", "20")
            ?.addQueryParameter("genres.name", genre) // Добавляем параметр для фильтрации по жанру
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
                    when (genre) {
////                        "криминал" -> criminalMoviesByGenre = (movieList.docs as ArrayList<Doc>?)!!
////                        "триллер"  -> thrillerMoviesByGenre = (movieList.docs as ArrayList<Doc>?)!!
////                        "боевик"   -> actionMoviesByGenre = (movieList.docs as ArrayList<Doc>?)!!
                        "криминал" -> criminalMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                        "триллер"  -> thrillerMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                        "боевик"   -> actionMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                    }
                }
            })
        }
    }

    fun getCriminalMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return criminalMoviesByGenreLiveData
    }

    fun getCriminalMoviesList(): ArrayList<Doc> {
        return criminalMoviesByGenre
    }

    fun getThrillerMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return thrillerMoviesByGenreLiveData
    }

    fun getActionMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return actionMoviesByGenreLiveData
    }


    private var myUserInfo = User("")
    fun downloadMyUserInfo(): User {
        database.child("Users").child("$currentUser").get().addOnSuccessListener {
            if (myUserInfo.profileImage == null) {
                myUserInfo.profileImage = ""
                myUserInfo.favouriteMovies = ArrayList()
                myUserInfo.subscriptions = ArrayList()
            }
            myUserInfo = it.getValue(User::class.java)!!

            Log.d("testLog", "myUserInfo --- $myUserInfo")
        }
        return myUserInfo
    }

    fun getMyUserInfo(): User {
        return myUserInfo
    }

    fun downloadAllUsers() {
        allUsersList.clear()
        database.child("Users").get().addOnSuccessListener {
            for (users in it.children) {
                val user = users.getValue(User::class.java)
                if (user?.uid != currentUser){
                    allUsersList.add(user!!)
                    allUsersListLiveData.postValue(allUsersList)
                }
            }
            Log.d("testLog", "allUsersList --- ${allUsersListLiveData.value}")
        }
    }

    fun getAllUsersList(): ArrayList<User> {
        return allUsersList
    }

    fun getAllUsersLiveData(): MutableLiveData<ArrayList<User>> {
        return allUsersListLiveData
    }

}