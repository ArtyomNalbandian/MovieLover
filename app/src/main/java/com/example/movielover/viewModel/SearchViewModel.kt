package com.example.movielover.viewModel

import android.net.Uri
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movielover.view.profile.User
import com.example.movielover.model.repository.Repository
import com.example.movielover.model.dataclasses.Doc

class SearchViewModel : ViewModel() {

    private val repository = Repository()

    //Текст в поисковике
    //private val searchMovieTextLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    //private val searchUserTextLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val searchTextLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private var moreDocPostAboutFragment: Doc? = null
    private var userInfoFragment: User? = null

    //Массив найденных пользователей
    private val foundUsersListLiveData: MutableLiveData<ArrayList<User>> by lazy { MutableLiveData<ArrayList<User>>() }
    private var foundUsersList = ArrayList<User>()

    private var tabPosition: Int = 0
    private var state: Int = 0
    private var flag: Int = 1

    fun getSearchMoviesListLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getSearchMoviesLiveData()
    }

    fun getSearchMoviesList(): ArrayList<Doc> {
        return repository.getSearchMoviesList()
    }

    fun setTabPosition(newTabPosition: Int) {
        if (tabPosition != newTabPosition) {
            tabPosition = newTabPosition
            state = 1
        } else {
            state = 0
        }
    }

    private var profileTabPosition = 0
    fun setProfileTabPosition(newTabPosition: Int) {
        profileTabPosition = newTabPosition
    }

    fun getProfileTabPosition(): Int {
        return profileTabPosition
    }

    fun getTabPosition(): Int {
        return tabPosition
    }

    fun setTestSearchText(searchText: String, tabPosition: Int) {
        if (tabPosition == 0) {
            if (searchTextLiveData.value != searchText) {
                searchTextLiveData.value = searchText
                flag = 0
                Log.d("testLog", "setter tab is 0 --- $searchText --- $flag")
                repository.sendRequest(searchText)
            } else if (searchTextLiveData.value.equals(searchText) && state == 1 && flag == 0) {
                flag = 1
                repository.sendRequest(searchText)
                Log.d("testLog", "else if --- $searchText --- $flag")
            } else {
                Log.d("testLog", "else --- $searchText")
            }
        } else {
            if (searchTextLiveData.value != searchText) {
                //searchTextLiveData.value = searchText
                flag = 0
                foundUsersListLiveData.postValue(foundUsersList)
                Log.d("testLog", "setter tab is 1 --- $searchText")
            } else if (searchTextLiveData.value.equals(searchText) && state == 1) {
                foundUsersListLiveData.postValue(foundUsersList)
                flag = 1
                Log.d("testLog", "else if --- $searchText")
            } else {
                Log.d("testLog", "else --- $searchText")
            }
        }
    }

    fun setFoundUsers(foundUsers: ArrayList<User>) {
        foundUsersList.clear()
        foundUsersList = foundUsers
        foundUsersListLiveData.postValue(foundUsersList)
    }

    fun getFoundUsersLiveData(): MutableLiveData<ArrayList<User>> {
        return foundUsersListLiveData
    }

    fun downloadAllUsers() {
        repository.downloadAllUsers()
    }

    fun getAllUsersList(): ArrayList<User> {
        return repository.getAllUsersList()
    }

    fun getAllUsersListLiveData(): MutableLiveData<ArrayList<User>> {
        return repository.getAllUsersLiveData()
    }

    fun getMoreDocPostAboutFragment(): Doc? {
        return moreDocPostAboutFragment
    }

    fun setMoreDocPostAboutFragment(post: Doc) {
        moreDocPostAboutFragment = post
    }

    fun setUserInfoFragment(user: User) {
        userInfoFragment = user
    }

    fun getUserInfoFragment(): User? {
        return userInfoFragment
    }

    fun addToMyFavouriteList(movie: Doc) {
        repository.addToMyFavouriteList(movie)
    }

    fun downloadFavouriteMovies() {
        repository.downloadFavouriteMovies()
    }

//    fun createAccount(email: EditText, password: EditText, login: EditText) {
//        repository.createAccount(email, password, login)
//    }

    fun createAccount(email: String, password: String, login: String, onSuccess: () -> Unit, onError: () -> Unit) {
        repository.createAccount(email, password, login, onSuccess, onError)
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        repository.loginUser(email, password, onSuccess)
    }

    fun getFavouriteMoviesList(): ArrayList<Doc> {
        return repository.getFavouriteMoviesList()
    }

    fun downloadProfileFavouriteMovies(user: User) {
        repository.downloadProfileFavouriteMovies(user)
    }

    fun getMyFavouriteMoviesList(): ArrayList<Doc> {
        return repository.getMyFavouriteMoviesList()
    }

    fun getMyFavouriteMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getMyFavouriteMoviesLiveData()
    }

//    fun getFavouriteMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
//        return repository.getFavouriteMoviesListLiveData()
//    }

    fun getProfileFavouriteMoviesListLive(): MutableLiveData<ArrayList<Doc>> {
        return repository.getProfileFavouriteMoviesListLive()
    }

    fun getMoviesByGenre(genre: String) {
        repository.getMoviesByGenre(genre)
    }

    fun getAnimeSeriesCartoon() {
        repository.getAnimeSeriesCartoon()
    }

    fun getCriminalMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getCriminalMoviesLiveData()
    }

    fun getThrillerMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getThrillerMoviesLiveData()
    }

    fun getActionMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getActionMoviesLiveData()
    }

    fun getMelodramaMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getMelodramaMoviesLiveData()
    }

    fun getDramaMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getDramaMoviesLiveData()
    }

    fun getFantasticMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getFantasticMoviesLiveData()
    }

    fun getAnimeMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getAnimeMoviesLiveData()
    }

    fun getSeriesMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getSeriesMoviesLiveData()
    }

    fun getCartoonMoviesLiveData(): MutableLiveData<ArrayList<Doc>> {
        return repository.getCartoonMoviesLiveData()
    }

    fun downloadMyUserInfo() {
        repository.downloadMyUserInfo()
    }

    fun downloadUserInfo(user: User) {
        repository.downloadUserInfo(user)
    }

    fun getMyUserInfo(): User {
        return repository.getMyUserInfo()
    }

    fun getUserInfo(): User {
        return repository.getUserInfo()
    }

    private var isFirstInstanceMovie = true
    private var isFirstInstanceUser = true

    fun setIsFirstInstanceMovie() {
        Log.d("testLog", "firstInstance --- movie")
        isFirstInstanceMovie = false
    }
    fun getIsFirstInstanceMovie(): Boolean {
        return isFirstInstanceMovie
    }
    fun setIsFirstInstanceUser() {
        Log.d("testLog", "firstInstance --- user")
        isFirstInstanceUser = false
    }
    fun getIsFirstInstanceUser(): Boolean {
        return isFirstInstanceUser
    }

    fun subscribeToUser(user: User) {
        repository.subscribeToUser(user)
    }

    fun getMySubscriptions() {
        repository.getMySubscriptions()
    }

    fun getProfileSubscriptions(user: User): ArrayList<User> {
        return repository.getProfileSubscriptions(user)
    }
    fun getMySubsList(): ArrayList<User> {
        return repository.getMySubsList()
    }


    fun getMySubsListLiveData(): MutableLiveData<ArrayList<User>> {
        return repository.getMySubsListLiveData()
    }

    fun unsubscribeFromUser(user: User) {
        repository.unsubscribeFromUser(user)
    }

    fun deleteMovieFromFavourite(movie: Doc) {
        repository.deleteMovieFromFavourite(movie)
    }

    fun uploadImage(filePath: Uri?) {
        repository.uploadImage(filePath)
    }

    fun getMyUserDataLive(): MutableLiveData<User> {
        return repository.getMyUserDataLive()
    }

    fun getUserDataLive(): MutableLiveData<User> {
        return repository.getUserDataLive()
    }
}