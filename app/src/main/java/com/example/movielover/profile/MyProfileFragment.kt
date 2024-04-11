package com.example.movielover.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentMyProfileBinding
import com.example.movielover.searchmovie.SearchViewModel

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var adapter: ProfileMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        adapter = ProfileMoviesAdapter(this, viewModel)
        mBinding.profileRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.profileRecyclerView.adapter = adapter

        /*viewModel.getLiveData().observe(viewLifecycleOwner) {
            adapter.moviesList = viewModel.getLiveData().value!!
            adapter.updateData()
        }*/
        adapter.moviesList = viewModel.getFavouriteMoviesList()// переделать на LiveData
        adapter.updateData()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}