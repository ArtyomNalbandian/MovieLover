package com.example.movielover.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.movielover.databinding.FragmentHomePageBinding
import com.example.movielover.profile.ProfileMoviesAdapter
import com.example.movielover.searchmovie.SearchViewModel

class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var adapter: ProfileMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)

        adapter = ProfileMoviesAdapter(this, viewModel)
        //mBinding.  .layoutManager = LinearLayoutManager(context)
        //mBinding.profileRecyclerView.adapter = adapter

        //Надо будет потом на splash экран
        viewModel.downloadFavouriteMovies()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.updateData()
    }
}