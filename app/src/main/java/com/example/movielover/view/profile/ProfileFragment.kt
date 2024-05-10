package com.example.movielover.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentProfileBinding
import com.example.movielover.viewModel.SearchViewModel
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var currentUser: User
    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var movieAdapter: ProfileMoviesAdapter
    private lateinit var userAdapter: ProfileUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        movieAdapter = ProfileMoviesAdapter(this, viewModel)
        userAdapter = ProfileUsersAdapter(this, viewModel)
        mBinding.profileRecyclerView.layoutManager = LinearLayoutManager(context)

        currentUser = viewModel.getUserInfoFragment()!!
        mBinding.userNameProfile.text = currentUser.login
        mBinding.profileRecyclerView.adapter = movieAdapter

        if (currentUser.profileImage != "") {
            Picasso.get().load(currentUser.profileImage).into(mBinding.profileImage)
        }

//        currentUser = viewModel.getUserInfo()
//        Log.d("testLog", "user - ${viewModel.getUserInfo()}")

        viewModel.downloadMyFavouriteMovies(currentUser)
        movieAdapter.moviesList = viewModel.getMyFavouriteMoviesList()
        movieAdapter.updateData()
        viewModel.getMyFavouriteMoviesLiveData().observe(viewLifecycleOwner) {
            movieAdapter.moviesList = viewModel.getMyFavouriteMoviesLiveData().value!!
            movieAdapter.updateData()
        }

        //viewModel.downloadUserInfo(currentUser)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backFromUserCardBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }
}