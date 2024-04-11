package com.example.movielover.searchmovie

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movielover.profile.User
import com.example.movielover.repository.Repository
import com.example.movielover.repository.dataclasses.Doc
import com.google.firebase.auth.FirebaseUser

class SearchViewModel : ViewModel() {

    private val repository = Repository()

    private val searchTextLiveData : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private var moreDocPostAboutFragment: Doc? = null

    fun getLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getLiveData()
    }

    fun setSearchText(searchText: String) {
        if(searchTextLiveData.value!=searchText){
            searchTextLiveData.value = searchText
            repository.sendRequest(searchText)
        }

        Log.d("testLog", "setSearchTextLiveData --- ${searchTextLiveData.value}")
    }

    fun getMoreDocPostAboutFragment(): Doc? {
        return moreDocPostAboutFragment
    }

    fun setMoreDocPostAboutFragment(post: Doc) {
        moreDocPostAboutFragment = post
    }
    fun addToMyFavouriteList(movie: Doc) {
        repository.addToMyFavouriteList(movie)
    }

    fun downloadFavouriteMovies() {
        repository.downloadFavouriteMovies()
    }

    fun createAccount(email: EditText, password: EditText, login: EditText) {
        repository.createAccount(email, password, login)
    }

    fun getFavouriteMoviesList(): ArrayList<Doc> {
        return repository.getFavouriteMoviesList()
    }

    fun getMoviesByGenre(genre: String) {
        repository.getMoviesByGenre(genre)
    }

    fun getCriminalMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getCriminalMoviesLiveData()
    }

    fun getCriminalMoviesList(): ArrayList<Doc> {
        return repository.getCriminalMoviesList()
    }

    fun getThrillerMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getThrillerMoviesLiveData()
    }

    fun getActionMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getActionMoviesLiveData()
    }

    fun downloadMyUserInfo() {
        repository.downloadMyUserInfo()
    }

    fun getMyUserInfo(): User {
        return repository.getMyUserInfo()
    }

}