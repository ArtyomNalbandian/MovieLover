package com.example.movielover.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentProfileBinding
import com.example.movielover.viewModel.SearchViewModel
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var currentUser: User
    private lateinit var myUser: User
    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var movieAdapter: ProfileMoviesAdapter
    private lateinit var userAdapter: ProfileUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        currentUser = viewModel.getUserInfoFragment()!!

        movieAdapter = ProfileMoviesAdapter(this, viewModel)
        userAdapter = ProfileUsersAdapter(this, viewModel)
        mBinding.profileRecyclerView.layoutManager = LinearLayoutManager(context)
        movieAdapter.moviesList = ArrayList()
        movieAdapter.updateData()
        mBinding.profileRecyclerView.adapter = movieAdapter

        movieAdapter.updateData()
        viewModel.downloadProfileFavouriteMovies(currentUser)

        movieAdapter.moviesList = viewModel.getFavouriteMoviesList()
        userAdapter.usersList = viewModel.getProfileSubscriptions(currentUser)

        viewModel.getProfileFavouriteMoviesListLive().observe(viewLifecycleOwner) {
            movieAdapter.moviesList = viewModel.getProfileFavouriteMoviesListLive().value!!
            movieAdapter.updateData()
        }

//        myUser = viewModel.getMyUserInfo()
//        mBinding.userNameProfile.text = currentUser.login
//        mBinding.profileRecyclerView.adapter = movieAdapter

//        if (currentUser.profileImage != "") {
//            Picasso.get().load(currentUser.profileImage).into(mBinding.profileImage)
//        } else {
//            mBinding.profileImage.setImageResource(R.drawable.ic_person)
//        }

//        movieAdapter.moviesList = viewModel.getFavouriteMoviesList()
//        movieAdapter.updateData()

        viewModel.getUserDataLive().observe(viewLifecycleOwner) {
            it.let {
                mBinding.userNameProfile.text = it.login
                if (it.profileImage?.isNotEmpty() == true) {
                    Picasso.get().load(it.profileImage).into(mBinding.profileImage)
                }
            }
        }
        viewModel.downloadUserInfo(currentUser)

        var mySubsList = viewModel.getMySubsList()

        viewModel.getMySubsListLiveData().observe(viewLifecycleOwner) {
            mySubsList = it
        }

        for (users in mySubsList) {
            if (users.uid == currentUser.uid) {
                mBinding.subscribeBtn.text = "Отписаться"
            }
        }

        mBinding.subscribeBtn.setOnClickListener {
            if (mBinding.subscribeBtn.text == "Подписаться") {
                viewModel.subscribeToUser(currentUser)
                mBinding.subscribeBtn.text = "Отписаться"
                Toast.makeText(context, "Вы подписались!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.unsubscribeFromUser(currentUser)
                mBinding.subscribeBtn.text = "Подписаться"
                Toast.makeText(context, "Вы отписались!", Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.profileTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> {
                        mBinding.profileRecyclerView.adapter = movieAdapter
                    }

                    1 -> {
                        mBinding.profileRecyclerView.adapter = userAdapter
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backFromUserCardBtn.setOnClickListener {
//            movieAdapter.moviesList = ArrayList()
//            mBinding.profileRecyclerView.adapter = movieAdapter
//            movieAdapter.updateData()
            parentFragmentManager.popBackStack()
        }

    }

}