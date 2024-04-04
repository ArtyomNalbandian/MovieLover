package com.example.movielover.searchmovie

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movielover.repository.Repository
import com.example.movielover.repository.dataclasses.Doc

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


}