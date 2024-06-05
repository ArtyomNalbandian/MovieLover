package com.example.movielover.view.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentMyProfileBinding
import com.example.movielover.viewModel.SearchViewModel
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var moviesAdapter: MyProfileMoviesAdapter
    private lateinit var usersAdapter: MyProfileUsersAdapter

    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        moviesAdapter = MyProfileMoviesAdapter(this, viewModel)
        usersAdapter = MyProfileUsersAdapter(this, viewModel)
//        usersAdapter = ProfileUsersAdapter(this, viewModel)
        mBinding.profileRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.profileRecyclerView.adapter = moviesAdapter

//        viewModel.downloadFavouriteMovies()

//        moviesAdapter.moviesList = viewModel.getMyFavouriteMoviesList()
//        moviesAdapter.updateData()
//        usersAdapter.usersList = viewModel.getMySubsList()
//        usersAdapter.updateData()

        viewModel.getMyFavouriteMoviesLiveData().observe(viewLifecycleOwner) {
            moviesAdapter.moviesList = viewModel.getMyFavouriteMoviesLiveData().value!!
            moviesAdapter.updateData()
        }

        viewModel.getMySubsListLiveData().observe(viewLifecycleOwner) {
            usersAdapter.usersList = viewModel.getMySubsListLiveData().value!!
            usersAdapter.updateData()
        }

        viewModel.downloadMyUserInfo()

        viewModel.getMyUserDataLive().observe(viewLifecycleOwner) {
            Log.d("testLog", "myUserLiveData --- ${it.login}")
            it.let {
                mBinding.userNameProfile.text = it.login
                if (it.profileImage?.isNotEmpty() == true) {
                    Picasso.get().load(it.profileImage).into(mBinding.profileImage)
                }
            }
        }

        mBinding.profileTabs.getTabAt(viewModel.getProfileTabPosition())!!.select()
        if (viewModel.getProfileTabPosition() == 0) {
            mBinding.profileRecyclerView.adapter = moviesAdapter
        } else {
            mBinding.profileRecyclerView.adapter = usersAdapter
        }

        mBinding.profileTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> {
                        viewModel.setProfileTabPosition(0)
                        mBinding.profileRecyclerView.adapter = moviesAdapter
                    }

                    1 -> {
                        viewModel.setProfileTabPosition(1)
                        mBinding.profileRecyclerView.adapter = usersAdapter
                    }
                }
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })

        return mBinding.root
    }

    private val pickImageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.data != null) {
                filePath = result.data!!.data

                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver, filePath
                    )
                    mBinding.profileImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                viewModel.uploadImage(filePath)
                Toast.makeText(context, "Фото профиля обновлено!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageActivityResultLauncher.launch(intent)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.profileImage.setOnClickListener {
            selectImage()
        }

    }
}