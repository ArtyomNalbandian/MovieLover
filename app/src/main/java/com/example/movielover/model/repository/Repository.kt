package com.example.movielover.model.repository

import android.net.Uri
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.example.movielover.view.profile.User
import com.example.movielover.model.dataclasses.Doc
import com.example.movielover.model.dataclasses.MovieToSearch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class Repository {

    private val database = Firebase.database.reference
//    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    //Массив фильмов который получаем при поиске
    private val searchMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val searchMoviesList = ArrayList<Doc>()

    //Получение всех пользователей кроме меня с Firebase
    private val allUsersList = ArrayList<User>()
    private val allUsersListLiveData: MutableLiveData<ArrayList<User>> by lazy { MutableLiveData<ArrayList<User>>() }

    //private val favouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val myFavouriteMoviesList = ArrayList<Doc>()
    private val myFavouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
//    private val favouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val profileFavouriteMoviesListLiveData: MutableLiveData<ArrayList<Doc>> by lazy { MutableLiveData<ArrayList<Doc>>() }
    private val profileFavouriteMoviesList = ArrayList<Doc>()

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
           val currentUser = FirebaseAuth.getInstance().currentUser?.uid
           val movieInfo = hashMapOf(
            "id" to movie.id,
            "name" to movie.name,
            "poster" to movie.poster,
            "description" to movie.description,
            "year" to movie.year,
            "country" to movie.countries?.get(0)?.name
        )
        database.child("Favourite Movies/$currentUser/${movie.id}").setValue(movieInfo)
        myFavouriteMoviesList.add(movie)
        myFavouriteMoviesListLiveData.postValue(myFavouriteMoviesList)
    }

    fun downloadFavouriteMovies() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        database.child("Favourite Movies").child("$currentUser").get().addOnSuccessListener {
            myFavouriteMoviesList.clear()
            if (it.exists()) {
                for (movies in it.children) {
                    Log.d("testLog", "for --- $movies")
                    val movie = movies.getValue(Doc::class.java)
                    myFavouriteMoviesList.add(movie!!)
                    myFavouriteMoviesListLiveData.postValue(myFavouriteMoviesList)
                    Log.d("testLog", "movie --- $movie")
                }
            }
        }
    }

    fun downloadProfileFavouriteMovies(user: User) {
        database.child("Favourite Movies").child("${user.uid}").get().addOnSuccessListener {
            profileFavouriteMoviesList.clear()
            if (it.exists()) {
                for (movies in it.children) {
                    val movie = movies.getValue(Doc::class.java)
                    profileFavouriteMoviesList.add(movie!!)
                    profileFavouriteMoviesListLiveData.postValue(profileFavouriteMoviesList)
                }
            }
        }
    }

    fun getProfileFavouriteMoviesListLive(): MutableLiveData<ArrayList<Doc>> {
        return profileFavouriteMoviesListLiveData
    }

    fun getMyFavouriteMoviesList(): ArrayList<Doc> {
        return myFavouriteMoviesList
    }

    fun getFavouriteMoviesList(): ArrayList<Doc> {
        return profileFavouriteMoviesList
    }

    fun getMyFavouriteMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return myFavouriteMoviesListLiveData
    }

    fun createAccount(email: String, password: String, login: String, onSuccess: () -> Unit, onError: () -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        val userInfo = hashMapOf(
                            "uid" to user.uid,
                            "email" to email,
                            "login" to login,
                            "profileImage" to "https://firebasestorage.googleapis.com/v0/b/movieloverapp-c5f6a.appspot.com/o/images%2F%D0%BF%D1%83%D1%81%D1%82%D0%B0%D1%8F%D0%90%D0%B2%D0%B0.png?alt=media&token=fd89ecb2-60ba-4dff-8aa4-68dd81413d2c"
                        )

                        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                            .setValue(userInfo)
                            .addOnCompleteListener { databaseTask ->
                                if (databaseTask.isSuccessful) {
                                    onSuccess()
                                } else {
                                    onError()
                                }
                            }
                    }
                } else {
                    onError()
                }
            }
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }
            }
    }

    fun sendRequest(nameOfMovieToSearch: String) {
        Log.d("testLog", "request has been sent")
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie/search?page=1&limit=10&query=$nameOfMovieToSearch")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "9WBCW0P-4284VRY-GQMAP77-H0PJGFM")
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

    fun getMoviesByGenre(genre: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=20&genres.name=$genre")
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
                val arrayOfMovies: ArrayList<Doc> = ArrayList()
                for (doc in movieList.docs!!) {
                    if (doc.poster != null && doc.backdrop!!.previewUrl != null) {
                        arrayOfMovies.add(doc)
                    }
                }
                when (genre) {
                    "криминал" -> criminalMoviesByGenreLiveData.postValue(arrayOfMovies.shuffled() as ArrayList<Doc>?)
                    "триллер" -> thrillerMoviesByGenreLiveData.postValue(arrayOfMovies.shuffled() as ArrayList<Doc>?)
                    "боевик" -> actionMoviesByGenreLiveData.postValue(arrayOfMovies.shuffled() as ArrayList<Doc>?)
                    "мелодрама" -> melodramaMoviesByGenreLiveData.postValue(arrayOfMovies.shuffled() as ArrayList<Doc>?)
                    "драма" -> dramaMoviesByGenreLiveData.postValue(arrayOfMovies.shuffled() as ArrayList<Doc>?)
                    "фантастика" -> fantasticMoviesByGenreLiveData.postValue(arrayOfMovies.shuffled() as ArrayList<Doc>?)
//                    "криминал"  -> criminalMoviesByGenreLiveData.postValue(movieList.docs as ArrayList<Doc>?)
////                    "криминал"  -> criminalMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
//                    "триллер"   -> thrillerMoviesByGenreLiveData.postValue(movieList.docs as ArrayList<Doc>?)
////                    "триллер"   -> thrillerMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
//                    "боевик"    -> actionMoviesByGenreLiveData.postValue(movieList.docs as ArrayList<Doc>?)
////                    "боевик"    -> actionMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
//                    "мелодрама" -> melodramaMoviesByGenreLiveData.postValue(movieList.docs as ArrayList<Doc>?)
////                    "мелодрама" -> melodramaMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
//                    "драма"     -> dramaMoviesByGenreLiveData.postValue(movieList.docs as ArrayList<Doc>?)
////                    "драма"     -> dramaMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
//                    "фантастика"-> fantasticMoviesByGenreLiveData.postValue(movieList.docs as ArrayList<Doc>?)
////                    "фантастика"-> fantasticMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                }
            }
        })
    }

    fun getAnimeSeriesCartoon() {
        val client = OkHttpClient()

        val seriesRequest = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=50&type=tv-series&isSeries=true")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
            .build()

        val cartoonRequest = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=50&type=animated-series&type=cartoon&isSeries=true")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("X-API-KEY", "X57R8H6-WVK4RP3-M01YV79-TYKPE7B")
            .build()

        val animeRequest = Request.Builder()
            .url("https://api.kinopoisk.dev/v1.4/movie?page=1&limit=50&type=anime&isSeries=true")
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
//                cartoonMoviesByGenreLiveData.postValue(movieList.docs?.shuffled() as ArrayList<Doc>?)
                cartoonMoviesByGenreLiveData.postValue(movieList.docs as ArrayList<Doc>?)
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
    private val myUserDataLiveData: MutableLiveData<User> by lazy { MutableLiveData<User>() }
    private val userDataLiveData: MutableLiveData<User> by lazy { MutableLiveData<User>() }

//    fun downloadMyUserInfo() {
//        currentUser?.let { userId ->
//            database.child("Users").child(userId).addListenerForSingleValueEvent(object :
//                ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val login = snapshot.child("login").value.toString()
//                    var profileImage = snapshot.child("profileImage").value.toString()
//                    if (profileImage == "") {
//                        profileImage = "https://firebasestorage.googleapis.com/v0/b/movieloverapp-c5f6a.appspot.com/o/images%2F%D0%BF%D1%83%D1%81%D1%82%D0%B0%D1%8F%D0%90%D0%B2%D0%B0.png?alt=media&token=fd89ecb2-60ba-4dff-8aa4-68dd81413d2c"
//                    }
//                    Log.d("testLog", "myUserIs --- ${User(login, "", "", profileImage)}")
//                    myUserDataLiveData.postValue(User(login, "", "", profileImage))
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//            })
//        }
//    }

    fun downloadMyUserInfo() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child("Users").child(currentUser).get().addOnSuccessListener {
            val userInfo = it.getValue(User::class.java)
            Log.d("testLog", "currentUserIs --- $currentUser")
            if (userInfo != null) {
                if (userInfo.profileImage == null) {
                    userInfo.profileImage = "https://firebasestorage.googleapis.com/v0/b/movieloverapp-c5f6a.appspot.com/o/images%2F%D0%BF%D1%83%D1%81%D1%82%D0%B0%D1%8F%D0%90%D0%B2%D0%B0.png?alt=media&token=fd89ecb2-60ba-4dff-8aa4-68dd81413d2c"
                }
                myUserInfo = userInfo
                myUserDataLiveData.postValue(myUserInfo)
            } else {
                Log.d("testLog", "User data is null")
            }
        }.addOnFailureListener {
            Log.d("testLog", "Failed to fetch user info", it)
        }
    }


//    fun downloadMyUserInfo() {
//        database.child("Users").child("$currentUser").get().addOnSuccessListener {
//            Log.d("testLog", "it --- ${it.getValue(User::class.java)}")
//            Log.d("testLog", "currentUser --- $currentUser")
//            if (myUserInfo.profileImage == null) {
//                myUserInfo.profileImage = "https://firebasestorage.googleapis.com/v0/b/movieloverapp-c5f6a.appspot.com/o/images%2F%D0%BF%D1%83%D1%81%D1%82%D0%B0%D1%8F%D0%90%D0%B2%D0%B0.png?alt=media&token=fd89ecb2-60ba-4dff-8aa4-68dd81413d2c"
//            }
//
//            myUserInfo = it.getValue(User::class.java)!!
//
//            Log.d("testLog", "myUserInfo --- $myUserInfo")
//            myUserDataLiveData.postValue(myUserInfo)
//        }
//    }

    fun getMyUserDataLive(): MutableLiveData<User> {
        return myUserDataLiveData
    }

    private var userInfo = User("")
//    fun downloadUserInfo(user: User) {
//        database.child("Users").child(user.uid.toString()).get().addOnSuccessListener {
//            val userInfo = it.getValue(User::class.java)
//            Log.d("testLog", "fragmentUserInfo --- $userInfo")
//            if (userInfo != null) {
//                if (userInfo.profileImage == null) {
//                    userInfo.profileImage = "https://firebasestorage.googleapis.com/v0/b/movieloverapp-c5f6a.appspot.com/o/images%2F%D0%BF%D1%83%D1%81%D1%82%D0%B0%D1%8F%D0%90%D0%B2%D0%B0.png?alt=media&token=fd89ecb2-60ba-4dff-8aa4-68dd81413d2c"
//                }
////                userInfo = userInfo
//                userDataLiveData.postValue(myUserInfo)
//            } else {
//                Log.d("testLog", "User data is null")
//            }
//        }.addOnFailureListener {
//            Log.d("testLog", "Failed to fetch user info", it)
//        }
//    }
    fun downloadUserInfo(user: User) {
        database.child("Users").child("${user.uid}").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val login = snapshot.child("login").value.toString()
                var profileImage = snapshot.child("profileImage").value.toString()
                if (profileImage == "") {
                    profileImage = "https://firebasestorage.googleapis.com/v0/b/movieloverapp-c5f6a.appspot.com/o/images%2F%D0%BF%D1%83%D1%81%D1%82%D0%B0%D1%8F%D0%90%D0%B2%D0%B0.png?alt=media&token=fd89ecb2-60ba-4dff-8aa4-68dd81413d2c"
                }
                userDataLiveData.postValue(User("", login, profileImage))
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun getUserDataLive(): MutableLiveData<User> {
        return userDataLiveData
    }

    fun getMyUserInfo(): User {
        return myUserInfo
    }

    fun downloadAllUsers() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
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

    fun uploadImage(filePath: Uri?) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        filePath?.let {
            val uid = currentUser
            val storageReference = FirebaseStorage.getInstance().getReference("images/$uid")
            storageReference.putFile(filePath)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        FirebaseDatabase.getInstance().getReference("Users").child(uid!!)
                            .child("profileImage").setValue(uri.toString())
                    }
                }
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

    fun subscribeToUser(user: User) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        Firebase.database.getReference("Users/$currentUser/Subscriptions/${user.uid}").setValue(user)
        mySubsList.add(user)
        mySubsLiveData.postValue(mySubsList)
    }

    fun getMySubscriptions() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
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

    fun getProfileSubscriptions(user: User): ArrayList<User> {
        val profileSubsList = ArrayList<User>()
        database.child("Users").child("${user.uid}").child("Subscriptions").get().addOnSuccessListener {
            for (users in it.children) {
                val user = users.getValue(User::class.java)
                profileSubsList.add(0, user!!)
            }
            Log.d("testLog", "subs --- $profileSubsList")
        }
        return profileSubsList
    }

    fun unsubscribeFromUser(user: User) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        Firebase.database.getReference("Users/$currentUser/Subscriptions/${user.uid}").removeValue()
        mySubsList.remove(user)
        mySubsLiveData.postValue(mySubsList)
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
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        Firebase.database.getReference("Favourite Movies/$currentUser/${movie.id}").removeValue()
        myFavouriteMoviesList.remove(movie)
        myFavouriteMoviesListLiveData.postValue(myFavouriteMoviesList)
    }

}