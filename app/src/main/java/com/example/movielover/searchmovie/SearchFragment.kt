package com.example.movielover.searchmovie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentSearchBinding
import com.example.movielover.profile.User
import com.google.android.material.tabs.TabLayout

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var movieAdapter: SearchMovieAdapter
    private lateinit var userAdapter: SearchUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        movieAdapter = SearchMovieAdapter(this, viewModel)
        userAdapter = SearchUserAdapter(this, viewModel)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.getSearchMoviesList()

        viewModel.getSearchMoviesListLiveData().observe(viewLifecycleOwner) {
            movieAdapter.moviesList = viewModel.getSearchMoviesListLiveData().value!!
            movieAdapter.updateData()
        }

        viewModel.getFoundUsersLiveData().observe(viewLifecycleOwner) {
            userAdapter.usersList = viewModel.getFoundUsersLiveData().value!!
            userAdapter.updateData()
        }

        mBinding.searchTabs.getTabAt(viewModel.getTabPosition())!!.select()
        if (viewModel.getTabPosition() == 0) {
            searchMovie(viewModel.getTabPosition())
            mBinding.recyclerView.adapter = movieAdapter
        } else {
            searchUsers(viewModel.getTabPosition())
            mBinding.recyclerView.adapter = userAdapter
        }

        mBinding.searchTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> {
                        viewModel.setTabPosition(0)
                        Log.d("testLog", "tabPosition is --- 0")
                        searchMovie(0)
                        mBinding.recyclerView.adapter = movieAdapter
                    }

                    1 -> {
                        viewModel.setTabPosition(1)
                        Log.d("testLog", "tabPosition is --- 1")
                        searchUsers(1)
                        mBinding.recyclerView.adapter = userAdapter
                    }
                }
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })

        return mBinding.root
    }

    fun searchMovie(tabPosition: Int) {
        if (viewModel.getIsFirstInstanceMovie()) {
            viewModel.setIsFirstInstanceMovie()
            val text = mBinding.searchView.query.toString()
            viewModel.setTestSearchText(text, tabPosition)
            Log.d("testLog", "base query --- ${mBinding.searchView.query}")
        }
        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.setTestSearchText(newText, tabPosition)
                } else {
                    viewModel.setTestSearchText("", tabPosition)
                }
                Log.d("testLog", "query --- ${mBinding.searchView.query}")

                return true
            }
        })
    }

    fun searchUsers(tabPosition: Int) {
        if (viewModel.getIsFirstInstanceUser()) {
            viewModel.setIsFirstInstanceUser()
            val text = mBinding.searchView.query.toString()
            val allUsersArray = viewModel.getAllUsersList()
            val foundUsers = ArrayList<User>()
            for (user in allUsersArray) {
                if (user.login!!.contains(text)) {
                    foundUsers.add(user)
                }
            }
            viewModel.setFoundUsers(foundUsers)
            viewModel.setTestSearchText(text, tabPosition)
        }
        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val allUsersArray = viewModel.getAllUsersList()
                    val foundUsers = ArrayList<User>()
                    for (user in allUsersArray) {
                        if (user.login!!.contains(newText)) {
                            foundUsers.add(user)
                        }
                    }
                    viewModel.setFoundUsers(foundUsers)
                    viewModel.setTestSearchText(newText, tabPosition)
                } else {
                    viewModel.setTestSearchText("", tabPosition)
                    viewModel.setFoundUsers(viewModel.getAllUsersList())
                }
                Log.d("testLog", "query --- ${mBinding.searchView.query}")
                return true
            }

        })
    }
}