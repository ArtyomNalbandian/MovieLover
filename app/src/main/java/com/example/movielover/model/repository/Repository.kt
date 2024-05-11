package com.example.movielover.model.repository

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.example.movielover.view.profile.User
import com.example.movielover.model.dataclasses.Doc
import com.example.movielover.model.dataclasses.MovieToSearch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


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
    private val myFavouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val myFavouriteMoviesList = ArrayList<Doc>()

    //Мои подписки
    private val mySubsList = ArrayList<User>()
    private val mySubsLiveData: MutableLiveData<ArrayList<User>> by lazy { MutableLiveData<ArrayList<User>>() }


    //Для получения списка фильмов определенного жанра
    private val criminalMoviesByGenreLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val thrillerMoviesByGenreLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val actionMoviesByGenreLiveData:   MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val melodramaMoviesByGenreLiveData:MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val dramaMoviesByGenreLiveData:    MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val fantasticMoviesByGenreLiveData:MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val animeMoviesByGenreLiveData:    MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val seriesMoviesByGenreLiveData:   MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val cartoonMoviesByGenreLiveData:  MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }

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
        database.child("Favourite Movies/$currentUser/${movie.id}").setValue(movieInfo)

    }

    fun downloadFavouriteMovies() {
        database.child("Favourite Movies").child("$currentUser").get().addOnSuccessListener {
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

    fun downloadMyFavouriteMovies(user: User) {
        database.child("Favourite Movies").child("${user.uid}").get().addOnSuccessListener {
            myFavouriteMoviesList.clear()
            if (it.exists()) {
                for (movies in it.children) {
                    val movie = movies.getValue(Doc::class.java)
                    myFavouriteMoviesList.add(movie!!)
                }
                myFavouriteMoviesListLiveData.postValue(myFavouriteMoviesList)
            }
        }
    }

    fun getMyFavouriteMoviesList(): ArrayList<Doc> {
        return myFavouriteMoviesList
    }

    fun getFavouriteMoviesList(): ArrayList<Doc> {
        return favouriteMoviesList
    }

    fun getMyFavouriteMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return myFavouriteMoviesListLiveData
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

    //Функция отправления запроса для получения массива фильмов
    fun sendRequest(nameOfMovieToSearch: String) {
        Log.d("testLog", "request has been sent")
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie/search?page=1&limit=10&query=$nameOfMovieToSearch")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "JHWCY0W-Z8N47AE-JK5S5TK-ZJE2W2E")
            .build()

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

    //Функция получения списка фильмов определенного жанра
    fun getMoviesByGenre(genre: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=200&genres.name=$genre")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("testLog", "fail")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body()?.string()
                val gson = Gson()
                val movieList = gson.fromJson(responseBody, MovieToSearch::class.java)
                when (genre) {
                    "криминал"  -> criminalMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                    "триллер"   -> thrillerMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                    "боевик"    -> actionMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                    "мелодрама" -> melodramaMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                    "драма"     -> dramaMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                    "фантастика"-> fantasticMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                }
            }
        })
    }

    fun getAnimeSeriesCartoon() {
        val client = OkHttpClient()

        val seriesRequest = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=200&type=tv-series&isSeries=true")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
            .build()

        val cartoonRequest = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=200&type=animated-series&type=cartoon&isSeries=true")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
            .build()

        val animeRequest = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=200&type=anime&isSeries=true")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
            .build()

        client.newCall(seriesRequest).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("testLog", "fail")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body()?.string()
                val gson = Gson()
                val movieList = gson.fromJson(responseBody, MovieToSearch::class.java)
                seriesMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
            }
        })

        client.newCall(cartoonRequest).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("testLog", "fail")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body()?.string()
                val gson = Gson()
                val movieList = gson.fromJson(responseBody, MovieToSearch::class.java)
                cartoonMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
            }
        })

        client.newCall(animeRequest).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("testLog", "fail")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body()?.string()
                val gson = Gson()
                val movieList = gson.fromJson(responseBody, MovieToSearch::class.java)
                animeMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
            }
        })
    }

    fun getCriminalMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return criminalMoviesByGenreLiveData
    }

    fun getThrillerMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return thrillerMoviesByGenreLiveData
    }

    fun getActionMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return actionMoviesByGenreLiveData
    }

    fun getMelodramaMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return melodramaMoviesByGenreLiveData
    }

    fun getDramaMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return dramaMoviesByGenreLiveData
    }

    fun getFantasticMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return fantasticMoviesByGenreLiveData
    }

    fun getAnimeMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return animeMoviesByGenreLiveData
    }

    fun getSeriesMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return seriesMoviesByGenreLiveData
    }

    fun getCartoonMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return cartoonMoviesByGenreLiveData
    }

    private var myUserInfo = User("")
    fun downloadMyUserInfo(): User {
        database.child("Users").child("$currentUser").get().addOnSuccessListener {
            Log.d("testLog", "it --- ${it.getValue(User::class.java)}")
            if (myUserInfo.profileImage == null) {
                myUserInfo.profileImage = ""
            }
            myUserInfo = it.getValue(User::class.java)!!

            Log.d("testLog", "myUserInfo --- $myUserInfo")
        }
        return myUserInfo
    }

    private var userInfo = User("")
    fun downloadUserInfo(user: User): User {
        database.child("Users").child("${user.uid}").get().addOnSuccessListener {
            if (userInfo.profileImage == null) {
                userInfo.profileImage = ""
            }
        }
        return userInfo
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

    fun getUserInfo(): User {
        return userInfo
    }

    fun getAllUsersList(): ArrayList<User> {
        return allUsersList
    }

    fun getAllUsersLiveData(): MutableLiveData<ArrayList<User>> {
        return allUsersListLiveData
    }

    //Subscription logic
    fun subscribeToUser(user: User) {
        Firebase.database.getReference("Users/$currentUser/Subscriptions/${user.uid}").setValue(user)
    }

    fun getMySubscriptions() {
        mySubsList.clear()
        database.child("Users").child("$currentUser").child("Subscriptions").get().addOnSuccessListener {
            for (users in it.children) {
                val user = users.getValue(User::class.java)
                mySubsList.add(0, user!!)
                mySubsLiveData.postValue(mySubsList)
            }
            Log.d("testLog", "subs --- $mySubsList")
        }
    }

    fun unsubscribeFromUser(user: User) {
        Firebase.database.getReference("Users/$currentUser/Subscriptions/${user.uid}").removeValue()
    }

    fun getMySubsList(): ArrayList<User> {
        Log.d("testLog", "mySubsList --- $mySubsList")
        return mySubsList
    }

    fun getMySubsListLiveData(): MutableLiveData<ArrayList<User>> {
        Log.d("testLog", "mySubsListLiveData --- ${mySubsLiveData.value}")
        return mySubsLiveData
    }

    fun deleteMovieFromFavourite(movie: Doc) {
        Firebase.database.getReference("Favourite Movies/$currentUser/${movie.id}").removeValue()
    }

}