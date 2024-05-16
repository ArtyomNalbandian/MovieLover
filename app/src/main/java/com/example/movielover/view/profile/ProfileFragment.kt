package com.example.movielover.view.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.R
import com.example.movielover.databinding.FragmentProfileBinding
import com.example.movielover.viewModel.SearchViewModel
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

        movieAdapter = ProfileMoviesAdapter(this, viewModel)
        userAdapter = ProfileUsersAdapter(this, viewModel)
        mBinding.profileRecyclerView.layoutManager = LinearLayoutManager(context)

        currentUser = viewModel.getUserInfoFragment()!!
        myUser = viewModel.getMyUserInfo()
        mBinding.userNameProfile.text = currentUser.login
        mBinding.profileRecyclerView.adapter = movieAdapter

        if (currentUser.profileImage != "") {
            Picasso.get().load(currentUser.profileImage).into(mBinding.profileImage)
        } else {
            mBinding.profileImage.setImageResource(R.drawable.ic_person)
        }

        movieAdapter.moviesList = viewModel.getFavouriteMoviesList()
        movieAdapter.updateData()

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
                Toast.makeText(context, "Вы подписались!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.unsubscribeFromUser(currentUser)
                Toast.makeText(context, "Вы отписались!", Toast.LENGTH_SHORT).show()
            }
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backFromUserCardBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }
}